package cn.iocoder.yudao.module.amazon.openapi.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Result<T> {

    /**
     * 自定义业务码
     */
    private String code;

    /**
     * 自定义业务提示说明
     */
    private String msg;

    /**
     * 自定义返回 数据结果集
     */
    private T data;

    // 手动添加getter和setter方法
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
