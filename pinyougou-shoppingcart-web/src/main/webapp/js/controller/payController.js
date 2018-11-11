
app.controller("payController",function ($scope,$location,payService) {
     $scope.createNativePay=function () {
        payService.createNativePay().success(
            function (response) {
                $scope.money=(response.total_fee/100).toFixed(2);//金额(以分计算)
                $scope.out_trade_no= response.out_trade_no;//订单号
                alert( $scope.out_trade_no)
                //前端生成二维码
                var qr=new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                queryPayStatus();//查询支付状态
            }
        )
    }

    queryPayStatus=function () {
         payService.queryPayStatus($scope.out_trade_no).success(
             function (response) {
                 if(response.success){
                     location.href="paysuccess.html#?money="+$scope.money;//支付成功将金额传递到成功页面显示
                 }else{
                     if(response.message=="二维码已过期"){
                         $scope.createNativePay();//重新生成二维码
                     }else{
                         location.href="payfail.html";
                     }
                 }

             }
         )
    }
    $scope.getMoney=function () {
         return $location.search()["money"]
        
    }

})