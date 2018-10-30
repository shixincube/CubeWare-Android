package cube.ware.eventbus;

/**
 * Created by dth
 * Des: eventbus 发送事件包装类
 * Date: 2018/9/20.
 */

public class MessageEvent<T> {


    /**
     * event 标识
     */
    private String msg;

    /**
     * 具体的内容。
     */
    private T data;


    public MessageEvent(T data) {
        this.data = data;
    }

    public MessageEvent(String msg) {
        this.msg = msg;
        this.data = null;
    }

    public MessageEvent(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
