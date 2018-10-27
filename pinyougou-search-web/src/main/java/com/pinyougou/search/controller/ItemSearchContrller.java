package com.pinyougou.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("itemSearch")
public class ItemSearchContrller {

    @Reference
    private ItemSearchService itemSearchService;


    /**
     * post请求
     * @param specMap
     * @return
     */
    @RequestMapping("/search.do")
    public Map itemSearch(@RequestBody Map specMap){
        return itemSearchService.search(specMap);

    }
}
