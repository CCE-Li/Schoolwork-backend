package cn.uptra.schoolwork.common.result;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 全局统一响应体
 * @param <T> 响应数据类型
 */
@Data
public class R<T> {
    // 状态码
    private int code;
    // 响应描述
    private String message;
    // 业务数据（成功时返回，失败时返回 null)
    private T data;
    // 响应时间
    private LocalDateTime timestamp;

    // 构造方法私有化
    // TODO：搞清楚构造方法私有化的作用
    private R() {
        this.timestamp = LocalDateTime.now();
    }

    // 1.成功响应（带业务数据）
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("成功");
        r.setData(data);
        return r;
    }

    // 2.成功响应（不带业务数据）
    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("成功");
        r.setData(null);
        return r;
    }

    // 成功响应 （返回自定义数据）
    public static <T> R<T> success(String str) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(str);
        r.setData(null);
        return r;
    }


    // 3.失败响应 （自定义状态码 + 自定义描述）
    public static <T> R<T> error(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(null);
        return r;
    }

    // 4.失败响应 （默认状态码 + 自定义描述
    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(400);
        r.setMessage(message);
        r.setData(null);
        return r;
    }
}
