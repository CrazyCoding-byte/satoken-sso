package yzx.samodel.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yzx.samodel.domain.entity.BizOrder;
import yzx.samodel.mapper.BizOrderMapper;
import yzx.samodel.service.BizOrderService;

@Service
public class BizOrderServiceImpl extends ServiceImpl<BizOrderMapper, BizOrder>
        implements BizOrderService {
}
