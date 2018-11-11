package com.pinyougou.shoppingcart.service.impl;

import Bean.Cart;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/5 14:57
 * @Version 1.0
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *
     * @param cartList  本地购物车和redis购物车
     * @param itemId    商品详细id
     * @param num       商品数量
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据sku id(itemId)查询sku信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw  new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        //2.根据sku id 获取商家Id(sellerId)
        String sellerId = item.getSellerId();
        //3.根据商家的id判断购物车列表中是否有该商家分类的购物车列表
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.判断结果
        if(cart==null){
            //4.1如果不存在该商家的分类购物车,则创建该商家的购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //构建商品详细
            TbOrderItem orderItem = createOrderItem(item, num);
            //创建购物车明细列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2将该商家的购物车添加到购物车列表中
            cartList.add(cart);
        }else{
            //5.如果存在该商家的分类购物车列表,查询该购物车列表的商品详细
            //判断该列表中是否已经存在相同的商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem==null){
                //5.1没有:新增购物车商品详细
                orderItem= createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else{
                //5.2当购物车该商品的订单数量不<=0:添加该商品的数量,计算金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //当商品的明细数量<=0时,即该购物车不存在该订单(可能会存在这种情况:商家恶意修改购物车数量)移除
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果该商家的购物车没有订单明细商品存在则移除该商家的购物车
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }

    /**
     *  根据用户名获取redis中的购物车列表
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        //判断
        if(cartList==null){
            cartList= new ArrayList<>();
        }
        return cartList;
    }

    /**
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车");
        redisTemplate.boundHashOps("cartList").put(username,cartList);

    }

    /**
     * 合并cookie和redis中 的购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }

        }

        return cartList1;
    }

    /**
     * 通过商家id查询该商家购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
            
        }
        return null;
    }

    /**
     * 新增订单详细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<=0){
            throw new RuntimeException("商品数量不合理");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        //这里的金额需要从数据库中查询不能直接在redis或者cookie中使用
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));//综合金额

        return orderItem;
    }


    /**
     * 根据商品的sku id在商家购物车分类列表中查询商品明细对象
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }

        }
        return null;
    }
}
