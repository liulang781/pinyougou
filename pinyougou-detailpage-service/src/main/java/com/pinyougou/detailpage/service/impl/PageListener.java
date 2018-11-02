package com.pinyougou.detailpage.service.impl;

import com.pinyougou.detailpage.service.ItemDetailPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/2 15:42
 * @Version 1.0
 */


@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemDetailPageService itemDetailPageService;
    @Override
    public void onMessage(Message message) {
        System.out.println("接收到订阅消息");
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            for (Long id : ids) {
                boolean b = itemDetailPageService.genItemHtml(id);
            }
            System.out.println("商品详细页生成");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
