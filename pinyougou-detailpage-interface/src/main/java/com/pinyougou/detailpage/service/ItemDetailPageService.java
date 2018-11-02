package com.pinyougou.detailpage.service;

/**
 * @Author: ali.liulang
 * @Date: 2018/10/30 20:00
 * @Version 1.0
 *
 * 商品详细页接口
 */
public interface ItemDetailPageService {

    /**
     * 通过商品spu的Id查询sku生成商品详细页Html
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 通过商品goodsId删除详细页
     * @param ids
     * @return
     */
    boolean deleteItemHtml(Long[] ids);
}
