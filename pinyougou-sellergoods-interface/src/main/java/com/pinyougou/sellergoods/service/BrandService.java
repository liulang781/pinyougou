package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import Bean.Result;
import com.pinyougou.pojo.TbBrand;
import page.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	/**
	 * 从tb_brand表中查询所有的brand
	 * @return
	 */
	 List<TbBrand> findAll();

	/**
	 * 分页查询结果封装到PageBean中
	 * @param pageNum  当前页
	 * @param pageSize  当前页显示数据的条目数
	 * @return
	 */
	PageResult findByPage(int pageNum, int pageSize);

/*
	*/
/**
	 * 添加商品
	 * @param tbBrand
	 *//*

	void addBrand(TbBrand tbBrand);

	*/
/**
	 * 修改商品
	 * @param tbBrand
	 *//*

	void updateBrand(TbBrand tbBrand);
*/

	/**
	 * 添加或者更新商品并返回结果信息
	 * @param tbBrand
	 * @return
	 */
	Result addOrUpdateBrand(TbBrand tbBrand);

	/**
	 * 通过id查询商品
	 * @param id
	 * @return
	 */
	TbBrand findOneById(Long id);

	void deleteByIds(Long[] ids);

	/**
	 * 搜索查询
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
    PageResult findBySearch(TbBrand tbBrand, int pageNum, int pageSize);

	/**
	 * 返回模板管理中的品牌下拉列表
	 */

	List<Map> selectOptionListForBrand();
}
