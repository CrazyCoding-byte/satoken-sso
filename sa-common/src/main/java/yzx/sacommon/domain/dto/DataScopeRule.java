package yzx.sacommon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 单个角色对应的数据范围规则
 * <p>
 * 一个用户可能有多个角色，所以 CurrentUser 里是 List&lt;DataScopeRule&gt;，
 * 由 DataScopeHandler 用 OR 合并。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据范围：1全部 2本部门 3本部门及下级 4仅本人 5自定义 */
    private Integer scope;

    /**
     * 该规则命中的部门ID列表。
     * <p>
     * scope=2/3/5 时有值；scope=4（仅本人）时为 null，因为按 create_by 过滤。
     */
    private List<Long> deptIds;
}
