package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Bean.Goods;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import page.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;


	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
		PageInfo pageInfo = new PageInfo(tbGoods);
		return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//组合Goods实体类,添加商品录入待审核
		goods.getGoods().setAuditStatus("0");
		//插入商品基本信息
		goodsMapper.insert(goods.getGoods());
		//添加goodsDesc对象,因为tb_goods_desc表中含有goodsId 所以需要添加
		//在goodsMapper.xml文件中添加主键自增加的sql语句
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());

		//插入将goods对象中的itemList集合中的数据插入到tb_item表中
		//遍历集合,插入
		saveItemList(goods);

	}

	/**
	 * 抽离添加商品信息中的添加规格代码
	 * @param goods
	 */
	private void saveItemList(Goods goods){

		//插入将goods对象中的itemList集合中的数据插入到tb_item表中
		//遍历集合
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				//添加标题title goodsName + color + spec ;
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += "" + specMap.get(key);
				}
				item.setTitle(title);
				setItemValue(goods, item);
				itemMapper.insert(item);
			}

		}else{
			TbItem item=new TbItem();
			//商品KPU+规格描述串作为SKU名称
			item.setTitle(goods.getGoods().getGoodsName());
			//价格
			item.setPrice( goods.getGoods().getPrice() );
			//状态
			item.setStatus("1");
			//是否默认
			item.setIsDefault("1");
			//库存数量
			item.setNum(99999);
			item.setSpec("{}");
			setItemValue(goods,item);
			itemMapper.insert(item);
		}
	}


	/**
	 * 保存数据到item表中
	 * @param goods
	 * @param item
	 */
	private void setItemValue(Goods goods,TbItem item){
		//设置商品的spu编号goodsId
		item.setGoodsId(goods.getGoods().getId());
		//设置商家店铺
		item.setSellerId(goods.getGoods().getSellerId());
		//设置商品分类编号3级
		item.setCategoryid(goods.getGoods().getCategory3Id());
		//创建日期
		item.setCreateTime(new Date());
		//修改日期
		item.setUpdateTime(new Date());
		//状态
		item.setStatus("1");
		//品牌名称
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(tbBrand.getName());

		//分类名称
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());
		//店铺名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());

		//设置图片地址取第一张的图片
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(imageList.size()>0){
			item.setImage((String)imageList.get(0).get("url"));
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新商品基本表数据
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更新扩展表数据
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//更新规格列表思路先删除在添加
		//通过goodsid删除
		TbItemExample example= new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加调用saveItemList方法
		saveItemList(goods);


	}

	/**
	 * 商品的修改通过表tb_goods中id查询表tb_goods_desc;tb_item获取数据封装到Goods实体类中
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods= new Goods();
		//通过id从表tb_goods中获取数据
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//通过goodsId从表tb_goods_desc中获取数据
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);
		//通过id查找itemList
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> list = itemMapper.selectByExample(example);
		goods.setItemList(list);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//逻辑删除而不是业务删除,即改变字段isDelete的状态还不是真的将数据删除
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();


		if(goods!=null){
			criteria.andIsDeleteIsNull();
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
		}

			List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
			PageInfo pageInfo = new PageInfo(tbGoods);
			return new PageResult(pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 批量审核商品
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * ids为审核后的商品goodsId
	 * @param ids
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdAndStatus(Long[] ids, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		List<TbItem> list = itemMapper.selectByExample(example);
		return list;
	}
}
