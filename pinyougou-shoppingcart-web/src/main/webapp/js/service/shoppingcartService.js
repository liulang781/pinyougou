

app.service("cartService",function ($http) {

//购物车列表
    this.findCartList=function () {
        return $http.get("../cart/findCartList.do")
    }

    this.addGoodsToCartList=function (itemId,num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
        
    }

    //计算购物车的金额(结算)
    this.sum=function (cartList) {
        //定义合计数对象
        var totalValue={"totalNum":0,"totalMoney":0.00};
        //遍历购物车列表
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j]//订单明细
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;

            }

        }
        return totalValue;
        
    }
})