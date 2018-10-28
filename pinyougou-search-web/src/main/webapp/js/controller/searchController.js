

app.controller("searchController",function ($scope,$location, searchService) {

    /**
	 * 搜索
     */
	$scope.search=function () {
		searchService.search($scope.searchMap).success(
			function (response) {
				$scope.resultMap=response;
            }
		)
    };

    //添加排序搜索(降序升序)sort,添加排序的字段域sortFiled
	$scope.searchMap={"keywords":'',"category":'',"brand":'',"spec":{},"price":'',"sort":'',"sortFiled":''}//定义搜索对象
	//搜索searchMap的值
	$scope.addSearchItem=function (key, value) {
		if(key=="category" || key =="brand" || key=="price" ){//点击选则分类和品牌
			$scope.searchMap[key]=value;
		}else{//选则规格
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();//将选中的搜索条件搜索
    }
    //撤销searchMap的值
    $scope.removeSearchItem=function (key) {
        if(key=="category" || key =="brand" || key=="price" ){//点击选则分类和品牌
            $scope.searchMap[key]='';//返回原来初始化
        }else{//撤销选则规格,将原来的key删除
           delete  $scope.searchMap.spec[key]
        }
        $scope.search();//移除搜索
    }

    $scope.sortSearch=function (sortFiled, sort) {
		$scope.searchMap.sortFiled=sortFiled;
		$scope.searchMap.sort=sort;
		$scope.search();
    }


    //隐藏品牌列表: 如果搜索的关键字中含有品牌的列表中的关键词那么就不显示品牌列表
    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }
        }
        return false;
    }


    //portal-web和首页的对接
    $scope.loadKeywords=function () {
		$scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }
});