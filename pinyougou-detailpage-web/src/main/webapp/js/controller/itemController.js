


//��Ʒ��ϸҳ�����Ʋ㣩
app.controller('itemController',function($scope){

	//���ﳵ��������
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	
	$scope.specificationItems={};//��¼�û�ѡ��Ĺ�����specificationItems={"����":"�ƶ�4G","�����ڴ�":"128G",...}
	
	//�û�ѡ����
	$scope.selectSpecifcation=function(key,value){
		$scope.specificationItems[key]=value;
		
		//����
		$scope.searchSku();
	}
	//��ѡ�еĹ��ı���ʽ
	$scope.isSelectSpecifcation=function(key ,value ){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};//sku����
	$scope.loadSku=function(){
		$scope.sku=skuList[0];//Ĭ���ǵ�һ��sku��Ʒ
		//ǳ��¡,���ݺ����õ�ַ��һ��,����һ�������ı���һ��Ҳ��ı�
		//���¡,����һ��,�������õ�ַ��һ������������,(�ֽ�һ������ת����json�ַ���,��ת��json���������ͱ�����¡)
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//�Ƚ���������������Ƿ����,��ѡ���sku���specificationItems��skuList����������ͬspec����ʾ�۸�
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
	
	//����ѡ���specificationItems����ѯskuList�б�ƥ��
	$scope.searchSku=function(){
		
		//����SKUList
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		//����û�ѡ��Ĺ������ݿ��skuList�����ڼ�û��ƥ����
		//���Զ���$scope.sku={};תUNcode�����ʽ�������������
		$scope.sku={id:0,title:'\u8865\u8d27\u4e2d',price:0};//���û��ƥ���		
		
	}
	
	//��ӹ��ﳵ
	$scope.addToCart=function(){
		alert('skuid:'+$scope.sku.id);				
	}
	
	
});