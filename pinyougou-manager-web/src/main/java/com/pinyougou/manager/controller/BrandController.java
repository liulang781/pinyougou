package com.pinyougou.manager.controller;


import Bean.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import page.PageResult;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findByPage.do")
    public PageResult findByPage(int pageNum, int pageSize){
        PageResult pageResult = brandService.findByPage(pageNum, pageSize);
        return pageResult;
    }

    @RequestMapping("/findOneById.do")
    public TbBrand findOneById(Long id){
        return brandService.findOneById(id);

    }

    @RequestMapping("/addOrUpdateBrand.do")
    public Result addOrUpdateBrand(@RequestBody TbBrand tbBrand){

        return brandService.addOrUpdateBrand(tbBrand);
    }

    @RequestMapping("/delete.do")
    public Result deleteByIds(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search.do")
    public PageResult findBySearch(@RequestBody TbBrand tbBrand, int pageNum, int pageSize){

        return brandService.findBySearch(tbBrand,pageNum,pageSize);
    }


    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionListForBrand(){
        return brandService.selectOptionListForBrand();
    }

}
