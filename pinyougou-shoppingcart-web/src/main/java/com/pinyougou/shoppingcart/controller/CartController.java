package com.pinyougou.shoppingcart.controller;

import Bean.Cart;
import Bean.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.shoppingcart.service.CartService;
import com.pinyougou.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/5 20:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 从本地cookie中取出购物车订单明细
     *
     * @return
     */
    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList() {
        //获取当前用户登录的账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取cookieName="cartList"的数据,cookie存储的还能是字符串
        String cartListString = CookieUtil.getCookieValue(request, "cookieList", "utf-8");
        //判断cookie
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        //有数据不为空
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if (username.equals("anonymousUser")) {
            //如果当前没有用户登录怎username为anonymousUser有spring-security配置文件配置而来并不是null(注意)
            System.out.println("从cookie中取购物车");
            return cartList_cookie;
        } else {
            //已经登录,从redis中获取购物车列表并将cookie中的购物车合并
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            //判断本地购物车是否有有商品
            if (cartList_cookie.size() > 0) {
                //合并两个购物车,只有当用户登录了才会合并,所以当用户登录就从redis中取数据
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //在存入合并后在存入redis中
                cartService.saveCartListToRedis(username, cartList);
                //存入完后清楚本地cookie中的购物车
                CookieUtil.deleteCookie(request, response, "cookieList");
                return cartList;
            }
            return cartList_redis;
        }
    }


    /**
     * 向购物车中添加商品订单明细,在存入cookie中
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList.do")
    @CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")//注解解决跨域请求,在被跨域请求的方法上添加那个域请求就在头信息设置该域的地址
    public Result addGoodsToCartList(Long itemId, Integer num) {
     /*   js的跨域请求这里说的js跨域是指通过js在不同的域之间进行数据传输或通信，
         比如用ajax向一个不同的域请求数据，或者通过js获取页面中不同域的框架中(iframe)的数据。
         只要协议、域名、端口有任何一个不同，都被当作是不同的域。
        需要浏览器和服务器同时支持,即前端的js跨域访问到后端,后端服务需要去识别解决
*/
//     response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//     response.setHeader("Access-Control-Allow-Credentials", "true");
        //获取当前用户登录的账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //从cookie中获取购物车列表信息
            List<Cart> cartList = findCartList();
            //将商品添加到列表中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")) {//未登录
                //将新的购物车存入cookie
                //转换成JSON字符串
                String cartListString = JSON.toJSONString(cartList);
                //存入cookie
                CookieUtil.setCookie(request, response, "cookieList", cartListString, 3600 * 24, "UTF-8");
                System.out.println("向cookie中存储购物车");
            } else {//如果登录
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "成功加入购物车!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }

    }

}
