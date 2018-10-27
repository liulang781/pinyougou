package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import Bean.SpecificationAndOptionGroup;
import com.pinyougou.pojo.TbSpecification;

import page.PageResult;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SpecificationAndOptionGroup specificationGroup);
	
	
	/**
	 * 修改
	 */
	public void update(SpecificationAndOptionGroup specificationGroup);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SpecificationAndOptionGroup findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param page 当前页 码
	 * @param rows 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int page, int rows);

	/**
	 * 模板管理中的规格下拉列表
	 *
	 */
	List<Map> selectOptionList();
}
