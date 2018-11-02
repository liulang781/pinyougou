package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;


@Service(timeout =5000 )
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索结果高亮显示 总的搜索,进行方法的剥离封装
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) { //从前端传入的多条件查询
        Map map = new HashMap();
        //创建新的Map结合将返回的高亮集合追加到新集合,一遍后续操作

        //搜索关键词的空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        map.putAll(searchList(searchMap));
        //分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //品牌列表和规格选项列表,完善规格和品牌列表查询
        //当通过关键词keywords进行查询时可能会得到多个商品的分类例:关键词:三星 得到商品分类:手机 , 平板电视;两个分类
        String category = (String) searchMap.get("category");
        if(!"".equals(category)){ //即选择了其中的一个商品分类
            //就按照选择的商品分类名称查询
            map.putAll(searchBrandAndSpecList(category));
        }else{
            if(categoryList.size()>0){//说明可以查到商品分类列表
                //如果没有选则商品分类就将第一个商品设为默认分类列表并查询所有的产品类型和规格选项
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }


        return map;

    }

    /**
     * 更新索引库
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 获得高亮集合操作
     * @param searchMap
     * @return
     */
    private  Map searchList(Map searchMap){
        Map map = new HashMap();
        //高亮显示搜索
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮显示的域,即搜索的内容在什么地方高亮显示,如果title上有搜索的内容就高亮显示
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
        //设置高连显示的前缀即想以什么格式高亮显示
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);



        //**************************搜索信息的过滤查询******************************
        //1.1按照关键字查询
        Criteria criteria= new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);


        //1.2按照商品分类查询

        if(!"".equals(searchMap.get("category"))){//如果有分类的查询
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            //向高亮搜索对象添加过滤搜索对象
            query.addFilterQuery(filterQuery);
        }
        //1.3按照商品的品牌查询
        if(!"".equals(searchMap.get("brand"))){
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            //向高亮搜索对象添加过滤搜索对象
            query.addFilterQuery(filterQuery);
        }
        //1.4按照规格过滤
       if(searchMap.get("spec")!=null){
            //spec{"网络":"移动3G","机身内存":"32G"};
           Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
           for (String key : specMap.keySet()) {
               FilterQuery filterQuery = new SimpleFacetQuery();
               Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
               filterQuery.addCriteria(filterCriteria);
               //向高亮搜索对象添加过滤搜索对象
               query.addFilterQuery(filterQuery);
           }
       }

       //1.5按照价格筛选
       if(!"".equals(searchMap.get("price"))){
          String[] prices = ((String)searchMap.get("price")).split("-");
          if(!prices[0].equals("0")){//如果价格起点的区间不为0
              FilterQuery filterQuery = new SimpleFacetQuery();
              Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);//比第一个元素大6
              filterQuery.addCriteria(filterCriteria);
              //向高亮搜索对象添加过滤搜索对象
              query.addFilterQuery(filterQuery);
          }
          if(!prices[1].equals("*")){//如果区间终点不等于*
              FilterQuery filterQuery = new SimpleFacetQuery();
              Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(prices[1]);//比第二个元素小
              filterQuery.addCriteria(filterCriteria);
              //向高亮搜索对象添加过滤搜索对象
              query.addFilterQuery(filterQuery);
          }

       }
       //1.6按照价格的升降序查询

        String sortValue = (String) searchMap.get("sort");
        String sortFiled = (String) searchMap.get("sortFiled");
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort= new Sort(Sort.Direction.ASC,"item_"+sortFiled);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort= new Sort(Sort.Direction.DESC,"item_"+sortFiled);
                query.addSort(sort);
            }
        }


        //***********  获取高亮结果集  ***********
        //高亮页对象
        //出入的参数和solr库匹配,查询数据并分页
        //tbItems是高亮显示对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
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


    /**
     * 分组查询,将搜索的关键字查询所有分类管理显示
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap){
        List list = new ArrayList();
        //创建搜索对象,执行搜索功能
        Query query= new SimpleQuery("*:*");
        //添加搜索条线,从复制域中查询关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));//where...分组条件
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//group by  分组
        query.setGroupOptions(groupOptions);
        //分组查询获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据域获取分组结果对象,传入的参数必须是分组选项中的域,分组选项可以设置多个分组域,根据域名取结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组的入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        //获得分组对象
        for (GroupEntry<TbItem> entry : entryList) {
                //将分组的结果添加到集合中
            list.add(entry.getGroupValue());

        }

         return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 通过商品分类名称查询从缓存中查询brandList和specList并返回
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        //从redis缓存中根据商品分类名称查询模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //根据模板ID查询产品类型brandList和specList
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
        map.put("brandList",brandList);
        List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("specList",specList);

        return map;

    }

}
