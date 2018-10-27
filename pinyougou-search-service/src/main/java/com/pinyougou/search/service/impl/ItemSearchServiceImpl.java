package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service(timeout =5000 )
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索结果高亮显示
     * @param specMap
     * @return
     */
    @Override
    public Map search(Map specMap) {
        Map map = new HashMap();
//        Query query=new SimpleQuery("*:*");
        //添加查询条件制定查询的域,传入的是map集合.键keywords,获取要查询的数据value

        //高亮显示搜索
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        //设置高亮显示的域,即搜索的内容在什么地方高亮显示,如果title上有搜索的内容就高亮显示
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
        //设置高连显示的前缀即想以什么格式高亮显示
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        //设置高亮选项
        highlightQuery.setHighlightOptions(highlightOptions);


        Criteria criteria= new Criteria("item_keywords").is(specMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
       //出入的参数和solr库匹配,查询数据并分页
        //tbItems是高亮显示对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        //遍历ptbItems得到高亮显示的对象即高亮显示的入口
        for (HighlightEntry<TbItem> tbItemHighlightEntry : page.getHighlighted()) {//高亮入口
            TbItem item = tbItemHighlightEntry.getEntity();//获得实体
            //获取高亮列表(高亮域的个数)
            List<HighlightEntry.Highlight> highlightList = tbItemHighlightEntry.getHighlights();

           /* for (HighlightEntry.Highlight highlight : highlights) {
                //每个域中可能有多值得高亮数据
                List<String> snipplets = highlight.getSnipplets();
                for (String snipplet : snipplets) {
                    //层层筛选拿到高亮数据snipplet
                    System.out.println(snipplet);
                }
            }*/

           //层层删选是为了而找到高亮数据,所以需要判断最后高亮数据存不存在
            if(highlightList.size()>0&&highlightList.get(0).getSnipplets().size()>0){
                //虽然高亮有多值直选第一个符合给item
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }

        }

        //每页的数据
        map.put("rows",page.getContent());
        return map;
    }
}
