 //控制层 
app.controller('contentController' ,function($scope,contentService){

    $scope.contentList=[];//广告集合,里面是map集合
	//通过广告分类id查询广告列表
	$scope.findByCategoryId=function (categoryId) {
		contentService.findByCategoryId(categoryId).success(
			function (response) {
				$scope.contentList[categoryId]=response;//contentList[{categoryId:response},{...}]
            }
		)

    }

    $scope.search=function () {//跳转到搜索页面
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }



});	
