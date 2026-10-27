package yzx.sauser.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.sacommon.domain.entity.SysDept;
import yzx.sacommon.mapper.SysDeptMapper;
import yzx.sauser.service.SysDeptService;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {
}
