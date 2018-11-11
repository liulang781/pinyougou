app.service("cartService", function ($http) {

//购物车列表
    this.findCartList = function () {
        return $http.get("../cart/findCartList.do")
    }

    this.addGoodsToCartList = function (itemId, num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num)

    }
    //计算选中的购物车列表的金额(结算)
    this.sum = function (cartList) {
        //定义合计数对象
        var totalValue = {"totalNum": 0, "totalMoney": 0.00};
        //遍历购物车列表
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j]//订单明细
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    }


    //获取登录用户
    this.showName = function () {
        return $http.get("../login//name.do")

    }

    //获取用户的收货地址
    this.findAddressList = function () {
        return $http.get("../address/findListByUserId.do")
    }

    //提交订单并保存
    this.submitOrder=function (order) {
        return $http.post("../order/add.do",order)
    }

});