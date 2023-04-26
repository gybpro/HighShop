package com.high.shop.mapper;

import com.high.shop.domain.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author high
* @description 针对表【sys_user(系统用户)】的数据库操作Mapper
* @createDate 2023-04-26 18:02:25
* @Entity com.high.shop.domain.SysUser
*/
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<String> selectPermsListByUserId(Long userId);

}




