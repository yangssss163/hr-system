package com.hr.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.system.dto.UserDTO;
import com.hr.module.system.dto.UserQuery;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.dto.UserVO;

public interface SysUserService {
    IPage<UserVO> page(UserQuery query);
    UserVO getById(Long id);
    void create(UserDTO dto);
    void update(Long id, UserDTO dto);
    void delete(Long id);
    void assignRoles(Long userId, java.util.List<Long> roleIds);
}
