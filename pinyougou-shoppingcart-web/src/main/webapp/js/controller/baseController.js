

app.controller("baseController",function ($scope,cartService) {

    //刷线列表页面选项数据
    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);

    };

    //分页控件配置currentPage:当前页  totalItems:总记录数  itemsPerPage:每页显示的数据 perPageOptions:分页选项 onChange:当前页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();
        }
    };





    // $scope.orderItemList=[];//定义复选框勾选的id集合
    // $scope.cart={};//定义购物车
    // $scope.list=[];//定义购物车列表
    //用户勾选复选框,并将id存入到集合中
    $scope.updateSelect=function ($event, orderItem) {
        //$event表示源即input标签的源
        //先判断复选框的状态
        if($event.target.checked){//表示被选中
            $scope.orderItemList.push(orderItem);//向集合中添加id
        }else{
            /*
            * 当复选框选中时就已经将选中brand的id添加到了集合中,如果不想在选择该brand时取消复选框选项后,集合中任然存在该brand的id
            * 所以需要再次的将该brand的id从集合中删除,即当该id下的checked为不选中时就从集合中删除该id
            * */
            var index=$scope.orderItemList.indexOf(orderItem)//查找该id在集合的索引
            $scope.orderItemList.splice(index,1);//参数1：移除的位置 参数2：移除的个数

        }
        //计算选中商品的金额(此处的变量在cartController.js中已经定义)
        //结算总合计
        $scope.totalValue=cartService.sum($scope.orderItemList);

    };
    
    $scope.isChecked=function (orderItem) {
        for(var i=0;i<$scope.orderItemList.length;i++){

            if($scope.orderItemList[i]==orderItem){
                return true;
            }else{
                return false;
            }
        }

        
    }



    //将json字符串转换成其中的属性值方法
    $scope.jsonToString=function(jsonString,key) {
        var json=JSON.parse(jsonString);
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=",";
            }
            value+=json[i][key];
        }
        return value;
    };

});


