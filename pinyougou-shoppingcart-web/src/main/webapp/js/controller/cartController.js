



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
});