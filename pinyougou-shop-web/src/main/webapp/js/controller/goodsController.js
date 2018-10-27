 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		//js服务本身的方法接受访问地址栏中携带的参数(黑马旅游网中有运用到)
		var id =$location.search()["id"];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//商品介绍
				editor.html($scope.entity.goodsDesc.introduction)
				//图片
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //显示规格选项
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//获取商品的SKU列表
				for(var i=0;i<$scope.entity.itemList.length;i++){
					//获取表tb_item中的spec字段转换成json对象
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				};

			}
		);				
	}
	//定义一个显示规格选项的方法
	$scope.checkAttributeValue=function (specName, optionValue) {
		var items=$scope.entity.goodsDesc.specificationItems;
	    var object=$scope.searchObjectByKey(items,"attributeName",specName);
	    if(object==null){
	    	return false;
		}else{
	    	//规格选项数组中的索引大于0说明有值
	    	if(object.attributeValue.indexOf(optionValue)>=0){
				return true
			}else{
	    		return false;
			}
		}

    };
    //保存
    $scope.save=function(){
        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert('保存成功');
                    location.href='goods.html';//跳转页面
                }else{
                    alert(response.message);
                }
            }
        );
    };

	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
    //添加state数组
    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	$scope.itemCatList=[];//定义分类数组
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
            }
		)
    };
	//搜索
	$scope.search=function(page,rows){
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//上传文件
	$scope.uploadFile=function () {
        uploadService.uploadFile().success(
			function (response) {
				if(response.success){
					//获取上传文件保存的地址
					$scope.image_entity.url=response.message;
				}else{
					alert(response.message);
				}
            });

    };

	//在向表goodsDesc中添加字段变量集合,集合中存的内容[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},]
    $scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
    //将当前上传的图片实体存入图片列表
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //移除图片
	$scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }


    //商品分类一级下拉框
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List=response;
            }
		)
    };



    //将一级分类的id传到二级
    //商品分类的二级下拉框根据上级Id查询
	//更新变量触发绑定的方法变量绑定法
	$scope.$watch("entity.goods.category1Id",function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
            }
		)

    });

    $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List=response;
            }
        )

    });


    //查询模板type_id
    $scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId=response.typeId;//更新模板id
            }
        )

    });

    //通过goods中的typeId查询商品的品牌
    $scope.$watch("entity.goods.typeTemplateId",function (newValue, oldValue){
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;
				//JSON.parse将json字符串转换为json对象,因为brandIds是对象typeTemplate的属性列表不是一个对象
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
				//获取扩展属性绑定给goodsDesc
				if($location.search()["id"]==null){
					//必须加判断不然修改和添加冲突.当修改操作时是有id传入此时不应该执行这条语句否则得不到扩展属性的值
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        );
        //通过模板id查询规格和规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList=response;
            }
        );
    });

    //勾选复原按钮更新规格选项
    $scope.updateSpecAttribute=function ($event,specName,value) {
		//调用方法
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName);
		//判断
		if(object!=null){//说明attributeName已经存在,所以再次更新规格选项就只会向attributeValue数组中添加
			//判断当前复选框的状态,如果被选中则将value添加
			if($event.target.checked){//被选中
				object.attributeValue.push(value);
			}else{//取消选中删除已经保存进去的value
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);//移除选项
				if(object.attributeValue.length==0){//即没有选项存在了 则移除attributeName;
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);

				}

			}

		}else {//不存在object 那么就要创建一个新的
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[value]});

		}
    };


    //创建SKU列表
    $scope.createItemList=function () {
		//初始化每行SKU的数据格式,集合中的每个元素都代表一个SKU行
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:"0",isDefault:"0"}];//初始化集合

		//spec中封装的是$scope.entity.goodsDesc.specificationItems中的内容,并进行循环嵌套
		//定义变量
		var items=$scope.entity.goodsDesc.specificationItems;
		//遍历[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
		for(var i=0;i<items.length;i++){
            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
    };


    //创建添加SKU行的方法
	addColumn=function (list,columnName,columnValue) {
		//新建一个集合
		var newList=[];
		//遍历集合
		for(var i =0;i<list.length;i++){
			//获取就行
			var oldRow=list[i];//{spec:{},price:0,num:9999,status:"0",isDefault:"0"} ;spec:{{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}}
			//深克隆创建新的行
			for(var j =0;j<columnValue.length;j++){
				//将老的行转换成json字符串在转换成新的行对象
				var newRow=JSON.parse(JSON.stringify(oldRow));
				//添加新的属性即遍历出来的attributeName,嵌套attributeValue的值
				newRow.spec[columnName]=columnValue[j];
				newList.push(newRow);
			}

		}
			return newList;
    }

});	
