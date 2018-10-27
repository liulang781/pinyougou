package com.pinyougou.sellergoods.service;
import java.util.List;

import Bean.Goods;
import com.pinyougou.pojo.TbGoods;

import page.PageResult;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	 * 改造 参数对象是Goods组合实体类
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);


	/**
	 *通过tb_goods表中 id属性查询分装到Goods实体中
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param goods 分装信息
	 * @param pageNum  当前页码
	 * @param pageSize 每页数据
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 批量审核
	 * @param ids
	 * @param status
	 */

	void updateStatus(Long[] ids, String status);
}
