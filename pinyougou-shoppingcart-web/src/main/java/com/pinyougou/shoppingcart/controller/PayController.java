package com.pinyougou.shoppingcart.controller;

import Bean.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.utils.IdWorker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: ali.liulang
 * @Date: 2018/11/11 9:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeiXinPayService weiXinPayService;

    @RequestMapping("createNativePay.do")
    public Map  createNativePay(){
        //用雪花生成器生成商品订单
        IdWorker idWorker = new IdWorker();
        return weiXinPayService.createNativePay(idWorker.nextId()+"","1");

    }


    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no){
        //后端循环检车订单的支付状态,前端调用后端循环结果
        Result result=null;
        int x=0;
        while(true){
            //调用接口查询
            Map<String,String> map = weiXinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                //支付出错
                result=new Result(false,"支付出错");
                break;//出错就跳出循环
            }
            //支付成功
            if(map.get("trade_state").equals("SUCCESS")){
                result=new Result(true,"支付成功");
                break;
            }
            try {
                //线程休眠停歇3秒后子训话,减少循环服务器压力
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //为了不让后端程序无限循环(用户一直处于未支付状态),所以需要定义一个变量来规定超时时间设置时间5分钟
            x++;
            if(x>=100){
                result=new Result(false,"二维码已过期");
                break;
            }
        }
        return result;

    }


}
