package yzx.sauser.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.sacommon.domain.entity.SysUser;
import yzx.sacommon.mapper.SysUserMapper;
import yzx.sauser.service.SysUserService;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {
}
