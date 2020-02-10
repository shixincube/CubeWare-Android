package cube.ware.data.api;

/**
 * api异常处理
 *
 * @author LiuFeng
 * @data 2020/2/10 11:14
 */
public class ApiException extends RuntimeException {

    private int      code;
    private String   desc;
    private BaseData data;

    public ApiException(ResultData resultData) {
        this.data = resultData.data;
        this.code = resultData.state.code;
        this.desc = resultData.state.desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public BaseData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ApiException{" + "code=" + code + ", desc='" + desc + '\'' + ", data=" + data + '}';
    }
}
