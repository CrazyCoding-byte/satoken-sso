package yzx.sauser.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.sacommon.domain.entity.SysTenant;
import yzx.sacommon.mapper.SysTenantMapper;
import yzx.sauser.service.SysTenantService;

@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant>
        implements SysTenantService {
}
