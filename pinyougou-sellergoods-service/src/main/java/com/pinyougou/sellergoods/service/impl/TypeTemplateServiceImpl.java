package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import page.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbTypeTemplate> tbTypeTemplates = typeTemplateMapper.selectByExample(null);
		PageInfo pageInfo=new PageInfo(tbTypeTemplates);
		return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}

			List<TbTypeTemplate> tbTypeTemplates = typeTemplateMapper.selectByExample(example);
			PageInfo pageInfo=new PageInfo(tbTypeTemplates);

			//缓存处理,讲不经常变更的数据存入缓存中,因为每次都要经过分页查询所以在此进行缓存的存储
            saveRedis();
			return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}



    @Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 将品牌列表和规格列表存入缓存
     *
	 */
	private void saveRedis(){
        List<TbTypeTemplate> typeTemplateList = findAll();
        for (TbTypeTemplate typeTemplate : typeTemplateList) {
            //品牌列表
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            //缓存品牌列表
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);

            //规格列表,通过规格id在查出规格选项调用方法
            List<Map> specList = findSpecList(typeTemplate.getId());
            //缓存列表
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
        }
    }




    @Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Override
	public List<Map> findSpecList(Long id) {
            //查询规格
            TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
            //获取字段spec_ids的数据
            String specIds = tbTypeTemplate.getSpecIds();
            //将specIds转换成json集合对象,参数1:需要转换的json字符串,参数2;被转换的数据类型
            List<Map> list = JSON.parseArray(specIds, Map.class);
            //遍历集合获取到规格id并通过规格id获取规格列表
            for (Map map : list) {
                TbSpecificationOptionExample example=new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                //将获取到的集合以键值对存入map
                map.put("options",options);
            }
        return list;
	}
}
