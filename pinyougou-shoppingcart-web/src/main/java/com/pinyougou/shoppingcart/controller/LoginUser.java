package com.pinyougou.shoppingcart.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/5 9:30
 * @Version 1.0
 */

@RestController
@RequestMapping("/login")
public class LoginUser {

    @RequestMapping("/name.do")
    public Map showName(){
        //获取登录用于名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map= new HashMap();
        map.put("loginName",name);
        return map;
    }
}
