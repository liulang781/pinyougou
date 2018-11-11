


//商品详细页（控制层）
app.controller('itemController',function($scope,$http){

    //购物车数量操作
    $scope.addNum=function(x){
        $scope.num=$scope.num+x;
        if($scope.num<1){
            $scope.num=1;
        }
    }


    $scope.specificationItems={};//记录用户选择的规格对象specificationItems={"网络":"移动4G","机身内存":"128G",...}

    //用户选择规格
    $scope.selectSpecifcation=function(key,value){
        $scope.specificationItems[key]=value;

        //调用
        $scope.searchSku();
    }
    //被选中的规格改变样式
    $scope.isSelectSpecifcation=function(key ,value ){
        if($scope.specificationItems[key]==value){
            return true;
        }else{
            return false;
        }
    }

    $scope.sku={};//sku对象
    $scope.loadSku=function(){
        $scope.sku=skuList[0];//默认是第一个sku商品
        //浅克隆,内容和引用地址都一样,其中一个变量改变另一个也会改变
        //深克隆,内容一样,但是引用地址不一样是两个对象,(现将一个对象转换成json字符串,在转成json对象这样就变成深克隆)
        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //比较两个对象的内容是否相等,当选择的sku规格specificationItems和skuList中数据有相同spec则显示价格
    matchObject=function(map1,map2){
        if(map1.size!=map2.size){
            return false;
        }
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        return true;
    }

    //根据选择的specificationItems规格查询skuList列表匹配
    $scope.searchSku=function(){

        //遍历SKUList
        for(var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku=skuList[i];
                return;
            }
        }
        //如果用户选择的规格和数据库的skuList不存在即没有匹配上
        //则自定义$scope.sku={};转UNcode编码格式否则浏览器乱码
        $scope.sku={id:0,title:'\u8865\u8d27\u4e2d',price:0};//如果没有匹配的

    }

    //添加购物车
    $scope.addToCart=function(){
    	//js的跨域请求这里说的js跨域是指通过js在不同的域之间进行数据传输或通信，
		// 比如用ajax向一个不同的域请求数据，或者通过js获取页面中不同域的框架中(iframe)的数据。
		// 只要协议、域名、端口有任何一个不同，都被当作是不同的域。
        //跨域操作cookie需要客户端和服务端设置协议,"withCredentials":true为true才可以操作cookie
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{"withCredentials":true}).success(
			function (response) {
				if(response.success){
					alert(response.message);
					//先不做跳转
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
				}else{
					alter(response.message);
				}
				
            }
		);
    }




});