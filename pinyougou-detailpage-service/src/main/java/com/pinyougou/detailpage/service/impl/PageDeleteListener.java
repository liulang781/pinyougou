package com.pinyougou.detailpage.service.impl;

import com.pinyougou.detailpage.service.ItemDetailPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/2 16:19
 * @Version 1.0
 */
@Service
public class PageDeleteListener implements MessageListener {


    @Autowired
    private ItemDetailPageService itemDetailPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+ids);
            boolean b = itemDetailPageService.deleteItemHtml(ids);
            System.out.println("网页删除结果："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
