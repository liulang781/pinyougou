//服务层
app.service('userService',function($http){

	//增加 
	this.add=function(entity,smscode){
		return  $http.post('../user/add.do?smscode='+smscode,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//发送验证码
	this.sendCode=function (phone) {
		return $http.get("../user/sendCode.do?phone="+phone)
    }
    this.showName=function () {
		return $http.get("../login/name.do")
		
    }

});
