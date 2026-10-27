package yzx.sauser.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.sacommon.domain.entity.SysMenu;
import yzx.sacommon.mapper.SysMenuMapper;
import yzx.sauser.service.SysMenuService;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {
}
