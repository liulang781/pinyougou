package com.pinyougou.shoppingcart.service;

import Bean.Cart;
import com.pinyougou.pojo.TbOrderItem;

import java.util.List;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/5 14:52
 * @Version 1.0
 */
public interface CartService {


    /**
     *添加商品到购物车
     * @param cartList  本地购物车和redis购物车
     * @param itemId    商品详细id
     * @param num       商品数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList ,Long  itemId, Integer num );


    /**
     * 用户登录状态从redis中查询购物车列表
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);


    /**
     * 用户登录状态下将购物车保存到redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 将cookie和redis中的购物车合并
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);


}
