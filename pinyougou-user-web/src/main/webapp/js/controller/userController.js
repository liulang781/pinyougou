 //控制层 
app.controller('userController' ,function($scope ,$controller  ,userService){

	//注册
	$scope.reg=function () {
		if($scope.entity.password!=$scope.password){
			alert("两次密码输入不一致,请重新确认!")
			return;
		}
		if($scope.entity.phone==null||$scope.entity.phone.length==0){
            alert("手机号码不能为空否则将无法发送短信验证!")
            return;
		}
		if($scope.smscode==null||$scope.smscode.length==0){
			alert("验证码不能为空!")
			return;
		}
		userService.add($scope.entity,$scope.smscode).success(
			function (response) {
				if(response.success){
					alert(response.message);
					location.href="#";
				}
            }
		)
    };

	//发送验证码
	$scope.sendCode=function () {
		if($scope.entity.phone==null||$scope.entity.phone.length==0){//手机号不能为空
			alert("请输入手机号!");
			return;
		}
		userService.sendCode($scope.entity.phone).success(
			function (response) {
				alert(response.message);
            }
		)
    }

    //获取当前登录名
	$scope.showName=function () {
		userService.showName().success(
			function (response) {
				$scope.loginName=response.loginName;
				
            }
		)
    }
});	
