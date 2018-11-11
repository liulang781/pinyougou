package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/10 21:21
 * @Version 1.0
 */
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {


    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;
    /**
     *新建本地支付(生成二维码)
     * @param out_trade_no  商品订单号
     * @param total_fee     标价金额
     * @return
     */
    @Override
    public Map createNativePay(String out_trade_no, String total_fee) {
        //封装参数,参照微信支付的官方sdk进行请求参数的封装
        Map param = new HashMap();
        param.put("appid",appid); //公众账号ID
        param.put("mch_id",partner);//商户
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("notify_url",notifyurl);//通知地址(无要求)
        param.put("body","品优购");//商品描述
        param.put("out_trade_no",out_trade_no);//商品订单号
        param.put("total_fee",total_fee);//金额
        param.put("trade_type","NATIVE");//交易类型
        param.put("spbill_create_ip","127.0.0.1");//终端Ip

        //生成要发送的xmlString文件
        try {
            //自动生成一个带签名的xml字符串
            String xmlParam= WXPayUtil.generateSignedXml(param,partnerkey);
            System.out.println("发送请求:"+xmlParam);
            //发送https请求请求微信的url(将请求的参数)
            //HttpClient对象就相当于一个浏览器,发送请求后获得响应结果
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);//确定https
            client.setXmlParam(xmlParam);
            client.post();//post请求

            //获得结果client(响应结果)
            String xmlResult = client.getContent();
            System.out.println("*********************************");
            System.out.println("返回结果:"+xmlResult);
            //将返回的结果字符串转化成毛map集合(微信返回的返回值)
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            Map<String, String> map=new HashMap<>();
            //返回的数据中包含敏感的数据,所以不需要全部都接受你返回前端只需要将二维码连接返回个前端用于用户扫描二维码调转到微信支付界面
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("out_trade_no", out_trade_no);//订单编号
            map.put("out_trade_no", out_trade_no);//订单编号
            map.put("total_fee", total_fee);//总金额
            //返回结果
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 通过订单号查询支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.封装参数
        Map param =new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            //2.发送请求
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            //3.返回结果
            return new HashMap();
        }

    }
}
