package com.pinyougou.sellergoods.service.impl;
import java.util.List;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.data.redis.core.RedisTemplate;
import page.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(null);
		PageInfo pageInfo=new PageInfo(tbItemCats);
		return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}

			List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
			PageInfo pageInfo=new PageInfo(tbItemCats);
			return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 将商品分类存入缓存中tb_item_cat
	 * @param parentId
	 * @return
	 */
	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		TbItemCatExample example=new TbItemCatExample();
        Criteria criteria = example.createCriteria();


        //查询条件
        criteria.andParentIdEqualTo(parentId);
        //将分类表数据存入redis缓存中tb_item_cat
		//1.获取所有的分类
		List<TbItemCat> itemCatList = findAll();
		//2.遍历存入redis中
		for (TbItemCat itemCat : itemCatList) {
			//分类的名称作为key,模板Id作为value
			redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
		}
		return itemCatMapper.selectByExample(example);
	}

}
