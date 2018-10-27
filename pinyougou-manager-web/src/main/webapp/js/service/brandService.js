//定义brandService前端的分层模式
app.service("brandService",function ($http) {
    //添加方法绑定controller层的方法,均为和后端交互的方法
    this.findAll=function () {
        return  $http.get("../brand/findAll.do");
    };
    this.findByPage=function (pageNum,pageSize){
        return $http.get("../brand/findByPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    };
    this.findOneById=function (id) {
        return $http.get("../brand/findOneById.do?id="+id);
    };
    this.saveOrUpdateBrand=function (entity) {
        return $http.post("../brand/addOrUpdateBrand.do",entity)
    };
    this.delBrandById=function (ids) {
        return  $http.get("../brand/delete.do?ids=" +ids)
    };
    this.search=function (pageNum,pageSize,searchEntity) {
        return  $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity)
    }
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }


});