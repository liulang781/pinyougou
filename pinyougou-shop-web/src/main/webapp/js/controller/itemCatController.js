 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}




	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentId;
			serviceObject=itemCatService.add( $scope.entity );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //定义上级id
    $scope.parentId=0;
    //根据上级分类ID查询列表
    $scope.findByParentId=function(parentId){
        //记住上级id
        $scope.parentId=parentId;
        itemCatService.findByParentId(parentId).success(
            function(response){
                $scope.list=response;
            }
        );
    };




    //面包屑分类
	//第一步:定义等级当parentId=0的为一级分类
	$scope.grade=1;//设置等级分类列表的等级
	//定义设置等级的方法
	$scope.setGrade=function (value) {
		//从前端调用该方法,当点击查询下一级是就从新设置该等级
		$scope.grade=value;
    };

    //查询当前等级下的所有数据并显示面包屑
	//参数P_entity对象
	$scope.selectList=function (p_entity) {
		//判断当前等级
		if($scope.grade==1){
			$scope.entity_1=null; //当前为顶级分类列表下,则二三级等等为null
			$scope.entity_2=null;
		}
		if($scope.grade==2){//处在二等级
			$scope.entity_1=p_entity; //等于当前等级id下的对象
			$scope.entity_2=null
		}
		if($scope.grade==3){
			$scope.entity_2=p_entity;
		}
		//调用findByParentId方法,将该等级下的所有含当前p_entity对象id的分类(即显示所有parent_id=p_entity.id的分类)
		$scope.findByParentId(p_entity.id);
    }
});	
