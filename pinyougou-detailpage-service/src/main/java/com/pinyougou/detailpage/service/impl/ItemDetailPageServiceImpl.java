package com.pinyougou.detailpage.service.impl;

//import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.detailpage.service.ItemDetailPageService;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: ali.liulang
 * @Date: 2018/10/30 20:05
 * @Version 1.0
 */
@Service
public class ItemDetailPageServiceImpl implements ItemDetailPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 根据商品id查询商品的sku列表tb_item
     *
     * @param goodsId
     * @param goodsId
     * @return 使用步骤：
     * 第一步：创建一个 Configuration 对象注入
     * 第二步：加载一个模板，创建一个模板对象。
     * 第三步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
     * 第四步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
     * 第五步：调用模板对象的 process 方法输出文件。
     * 第六步：关闭流
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //创建模板
            Template template = configuration.getTemplate("item.ftl");
            //数据模型
            Map dataModel = new HashMap();
            //商品主表
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", tbGoods);
            //商品副表扩展表数据
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", tbGoodsDesc);
            //商品的分类名称category1,category2,category3
            String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            dataModel.put("category1",category1);
            dataModel.put("category2",category2);
            dataModel.put("category3",category3);

            //商品的sku列表
            TbItemExample example =new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);//匹配spu
            example.setOrderByClause("is_default desc");//按照状态降序，保证第一个sku为默认

            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

            //输出对象
//            FileWriter writer = new FileWriter(pagedir+goodsId+".html"); //出现乱码问题
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pagedir + goodsId + ".html"), "utf-8");
            template.process(dataModel, writer);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 根据goodsId 删除商品详细页
     * @param ids
     * @return
     */

    @Override
    public boolean deleteItemHtml(Long[] ids){
        try {
            for (Long goodsId : ids) {
                new File(pagedir + goodsId + ".html").delete();
            }
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
