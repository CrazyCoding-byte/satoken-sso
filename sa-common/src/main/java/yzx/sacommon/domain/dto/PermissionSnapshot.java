package yzx.sacommon.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @className: PermissionSnapshot
 * @author: yzx
 * @date: 2026/10/28 11:54
 * @Version: 1.0
 * @description:
 * 权限快照
 */
public record PermissionSnapshot(List<String> permissions, List<String> roles) implements Serializable {
}
