

app.controller("indexController",function ($scope, loginService) {


    //显示当前用户
    $scope.showLoginName=function () {

        loginService.showLoginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )

    }
});