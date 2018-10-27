package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import page.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired(required = true)
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//新增广告则通过categoryId键找到值先删除在添加
		contentMapper.insert(content);
		Long categoryId = content.getCategoryId();
		//下次查询在存入缓存
		redisTemplate.boundHashOps("content").delete(categoryId);


	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的categoryId(有可能修改的就是categoryId)
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//删除缓存
		redisTemplate.boundHashOps("content").delete(categoryId);
		//如果categoryId被修改,那就要删除新categoryId的缓存在添加
		if(categoryId.longValue()!=content.getCategoryId().longValue()){
			//删除缓存
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		contentMapper.updateByPrimaryKey(content);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//数据库删除广告数据则清楚缓存
			redisTemplate.boundHashOps("content").delete(contentMapper.selectByPrimaryKey(id).getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {

		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if(contentList==null){
			System.out.println("从数据库去数据存入redis缓存");
			TbContentExample example= new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			//查询有效的广告
			criteria.andStatusEqualTo("1");
			//排序
			example.setOrderByClause("sort_order");
			contentList=contentMapper.selectByExample(example);
			redisTemplate.boundHashOps("content").put(categoryId,contentList);

		}else{
			System.out.println("从redis缓存取数据");
		}
		return contentList;
	}
}
