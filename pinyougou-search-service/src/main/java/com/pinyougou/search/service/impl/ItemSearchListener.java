package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/1 21:48
 * @Version 1.0
 */
@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        TextMessage textMessage = (TextMessage) message;
        try {
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(),TbItem.class);
          /*  for (TbItem item : itemList) {
                //将spec字符串转换成Map对象
                Map specMap = JSON.parseObject(item.getSpec(), Map.class);
                //添加到specMap(动态域搜索)
                item.setSpecMap(specMap);
            }*/
            //添加到solr库中
            itemSearchService.importList(itemList);
            System.out.println("成功导入到索引库");
        } catch (JMSException e) {
            e.printStackTrace();

        }

    }
}
