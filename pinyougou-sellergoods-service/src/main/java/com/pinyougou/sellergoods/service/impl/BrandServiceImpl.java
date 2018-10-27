package com.pinyougou.sellergoods.service.impl;

import Bean.Result;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageResult;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }


    /**
     * 分页插件
     *
     * @param pageNum  当前页
     * @param pageSize 当前页显示数据的条目数
     * @return 改进:可以使用PageInfo 来封装数据并进行分页
     */
    @Override
    public PageResult findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        PageInfo pageInfo = new PageInfo(tbBrands);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    @Override
    public Result addOrUpdateBrand(TbBrand tbBrand) {
        Result result =new Result();
        //判断tbBrand对象中的id
        if(tbBrand.getId()==null){
            //当前执行的是添加保存操作
            try {
                tbBrandMapper.insert(tbBrand);
                result.setSuccess(true);
                result.setMessage("新建成功");
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("新建失败");
                e.printStackTrace();

            }
        }
        if(tbBrand.getId()!=null){
            //当前执行的是修改操作
            try {
                tbBrandMapper.updateByPrimaryKey(tbBrand);
                result.setSuccess(true);
                result.setMessage("修改成功");
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("修改失败");
                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public TbBrand findOneById(Long id) {
              return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findBySearch(TbBrand tbBrand, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        //判断搜索框中输出的数据是否封装到可TbBrand
        if(tbBrand!=null){
            if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                //模糊查询已经由逆向工程封装
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){

                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo(tbBrands);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<Map> selectOptionListForBrand() {
        return tbBrandMapper.selectOptionListForBrand();
    }
}
