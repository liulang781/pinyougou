package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/10 21:08
 * @Version 1.0
 */
public interface WeiXinPayService {

    /**
     * 创建本地支付(交易类型)
     * @param out_trade_no  商品订单号
     * @param total_fee     标价金额
     * @return
     * 用于生成支付二维码的url(信息)
     */
    public Map createNativePay(String out_trade_no,String total_fee);


    /**
     * 通过订单号查询支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);



}
