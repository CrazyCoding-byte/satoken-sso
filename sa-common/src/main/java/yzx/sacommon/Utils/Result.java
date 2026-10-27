package yzx.sacommon.Utils;

import java.util.HashMap;

/**
 * @className: Result
 * @author: yzx
 * @date: 2026/10/27 11:09
 * @Version: 1.0
 * @description:
 */
public class Result<T> extends HashMap<Integer, T> {
    private Result() {
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.put(200, data);
        return result;
    }

    public static <T> Result<T> error(T data) {
        Result<T> result = new Result<T>();
        result.put(500, data);
        return result;
    }
}
