



app.controller("cartController",function ($scope,cartService) {


    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;
                // alert( JSON.stringify($scope.cartList))
                //结算总合计
                $scope.totalValue=cartService.sum($scope.cartList);

            }
        )
    }

    $scope.addGoodsToCartList=function (itemId,num) {
        // $scope.orderItemList=[];
        cartService.addGoodsToCartList(itemId,num).success(
            function (resposne) {
                if(resposne.success){
                    $scope.findCartList();//刷新
                }else{
                    alert(resposne.message);
                }
            }
        )
        
    }
    //获取当前登录名
    $scope.showName=function () {
        cartService.showName().success(
            function (response) {
                $scope.loginName=response.loginName;

            }
        )
    }


    //获取用户收货地址
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList=response;
                for(var i=0; i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break ;
                    }
                }
                
            }
        )
    };

    //选择地址
    $scope.selectAddress=function (address) {
        //address对象等于选择的地址(相当于封装)
        $scope.address=address;
        
    };

    //判断当前是否是选中的地址
    $scope.isSelectedAddress=function (address) {
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }
    $scope.order={"paymentType":'1'}
    
    //支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    };


    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //跳转到支付页面
                    if($scope.order.paymentType=="1"){
                        //跳转到微信支付
                        location.href="pay.html";
                    }else{
                        //货到付款,跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    // alert(response.message)//支付失败
                    location.href="payfail.html"


                }
                
            }
        )
        
    }

});