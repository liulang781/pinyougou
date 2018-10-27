package com.pinyougou.solrutil;


import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 向solr库中导入搜索数据
     */
    public void importItemData(){
        //从数据库中查询审核通过的item
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);

        for (TbItem item : itemList) {
            //获取字段spec的数据转成json的map集合
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
        }
        //添加到索引库中并提交
        solrTemplate.saveBean(itemList);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        ApplicationContext context= new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SolrUtil solrUtil = context.getBean(SolrUtil.class);
        solrUtil.importItemData();
    }

}
