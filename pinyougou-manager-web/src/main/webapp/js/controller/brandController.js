

app.controller("brandController",function ($scope,$controller, brandService) {


    $controller("baseController",{$scope:$scope})//继承
    //查询商品品牌数据
    $scope.findAll=function () {
        //ajax请求
        brandService.findAll().success(
            //date就是响应数据(response)
            function (response) {
                $scope.list=response;
            });
    };

    //分页查询
    $scope.findByPage=function (pageNum,pageSize) {
        brandService.findByPage(pageNum,pageSize).success(
            function (response) {
                $scope.list=response.rows;//显示当前页的数据
                //将响应的数据返回并更新总的数据数
                $scope.paginationConf.totalItems=response.total;
            }
        )
    };
    //通过id查询单一商品
    $scope.findOneById=function (id) {
        brandService.findOneById(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    //添加或者修改数据
    $scope.saveOrUpdateBrand=function () {
        //向后端传输的数据是对象所有需要用post请求
        brandService.saveOrUpdateBrand($scope.entity).success(
            function (response) {
                if(response.success){//如果成功就刷新页面
                    $scope.reloadList();
                }else{
                    alert(response.message)
                }
            }
        )
    };


    //删除商品
    $scope.delBrandById=function () {
        if (confirm("确定要删除选中的商品么?")) {
            brandService.delBrandById($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();
                    } else {
                        alert(response.message);
                    }
                }
            )

        }

    };

    //条件查询商品
    $scope.searchEntity={};//定义对象封装查询条件
    $scope.search=function (pageNum,pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;//更新总每页的数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )

    }
});