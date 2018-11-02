package com.pinyougou.service;


import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索
     * @param specMap
     * @return
     */
    public Map search(Map specMap);

    /**
     * 批量导入索引库
     * @param list
     */
    public void importList(List list);

    /**
     * 批量删除索引库数据
     * @param goodsIds
     */
    public void deleteByGoodsIds(List goodsIds);


}
