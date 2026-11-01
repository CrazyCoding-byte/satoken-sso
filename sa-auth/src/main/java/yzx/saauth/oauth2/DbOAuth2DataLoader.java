package yzx.saauth.oauth2;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import yzx.sacommon.domain.entity.SysOAuthClient;
import yzx.sacommon.mapper.SysOAuthClientMapper;

import java.util.Arrays;
import java.util.List;

/**
 * Sa-Token 通过这个接口拿应用信息，它自己不知道数据库长什么样。
 */
@Component
public class DbOAuth2DataLoader implements SaOAuth2DataLoader {

    private final SysOAuthClientMapper clientMapper;

    public DbOAuth2DataLoader(SysOAuthClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public SaClientModel getClientModel(String clientId) {
        SysOAuthClient client = clientMapper.selectOne(new LambdaQueryWrapper<SysOAuthClient>()
                .eq(SysOAuthClient::getClientId, clientId));

        if (client == null || !"0".equals(client.getStatus())) {
            return null;
        }

        SaClientModel model = new SaClientModel()
                .setClientId(client.getClientId())
                .setClientSecret(client.getClientSecret())
                .setContractScopes(split(client.getContractScope()))
                .setAllowRedirectUris(split(client.getRedirectUri()));

        model.addAllowGrantTypes("authorization_code", "refresh_token", "password", "client_credentials");

        // 必须显式设置，否则 !getIsAutoConfirm() 拆箱会 NPE
        model.setIsAutoConfirm(false);

        // 用全局配置补齐超时时间、令牌上限等默认字段
        SaOAuth2Strategy.instance.setSaClientModelDefaultFields.run(model);
        return model;
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}