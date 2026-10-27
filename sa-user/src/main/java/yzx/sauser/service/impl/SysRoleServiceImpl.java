package yzx.sauser.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.sacommon.domain.entity.SysRole;
import yzx.sacommon.mapper.SysRoleMapper;
import yzx.sauser.service.SysRoleService;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {
}
