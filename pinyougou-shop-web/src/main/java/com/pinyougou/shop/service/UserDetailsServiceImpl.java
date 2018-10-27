package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义认证类用来管理商家登录商家运营后台
 * 并实现springSecurity框架中的UserDetailsService接口重写方法
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * username是商家登录的用户名
     * 用于认证商家信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /**
         * 添加商家角色进行登录认证
         */
        List<GrantedAuthority> grantedAuthorities =new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        System.out.println(sellerService);
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){
            //该商家已经存在并且status=1已经被审核
            if("1".equals(seller.getStatus())){
                return new User(username,seller.getPassword(),grantedAuthorities);
            }
        }
        return null;
    }
}
