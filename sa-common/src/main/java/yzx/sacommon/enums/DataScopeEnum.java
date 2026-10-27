package yzx.sacommon.enums;

/**
 * 数据权限范围
 * <p>
 * 数字越小范围越大，但多角色合并时不要简单取最小值，
 * 而是把每个角色的范围都保留下来用 OR 合并（见 DataScopeHandler）。
 */
public enum DataScopeEnum {

    /** 全部数据 */
    ALL(1),

    /** 本部门数据 */
    DEPT(2),

    /** 本部门及下级部门数据 */
    DEPT_AND_CHILD(3),

    /** 仅本人数据 */
    SELF(4),

    /** 自定义部门数据 */
    CUSTOM(5);

    private final Integer code;

    DataScopeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static DataScopeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (DataScopeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
