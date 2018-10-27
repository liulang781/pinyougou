package com.pinyougou.manager.controller;
import java.util.List;
import java.util.Map;

import Bean.Result;
import Bean.SpecificationAndOptionGroup;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import page.PageResult;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll.do")
	public List<TbSpecification> findAll(){			
		return specificationService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage.do")
	public PageResult findPage(int page, int rows){
		return specificationService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param specificationGroup
	 * @return
	 * SpecificationAndOptionGroup对象中封装的是规格实体和规格选项集合
	 *
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody SpecificationAndOptionGroup specificationGroup){
		try {
			specificationService.add(specificationGroup);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param specificationGroup
	 * @return
	 * 修改采用的方法是先删除在添加
	 *
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody SpecificationAndOptionGroup specificationGroup){
		try {
			specificationService.update(specificationGroup);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne.do")
	public SpecificationAndOptionGroup findOne(Long id){
		return specificationService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete.do")
	public Result delete(Long[] ids){
		try {
			specificationService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param specification
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult search(@RequestBody TbSpecification specification, int page, int rows  ){
		return specificationService.findPage(specification,page,rows);
	}


	@RequestMapping("/selectOptionList.do")
	public List<Map> selectOptionList(){
		return specificationService.selectOptionList();
	}
	
}
