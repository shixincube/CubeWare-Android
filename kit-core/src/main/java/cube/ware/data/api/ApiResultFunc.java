package cube.ware.data.api;

import com.common.utils.utils.log.LogUtil;
import java.util.Map;
import rx.functions.Func1;

/**
 * 处理Http的ResponseState,并将ResultData的Data部分剥离出来返回给subscriber
 *
 * @author LiuFeng
 * @data 2020/2/10 11:07
 */
public class ApiResultFunc<T extends BaseData> implements Func1<ResultData<T>, T> {
    private Map<String, String> params;
    private StackTraceElement[] stackTrace;

    public ApiResultFunc() {}

    public ApiResultFunc(Map<String, String> params) {
        this.params = params;
        this.stackTrace = new Exception().getStackTrace();
    }

    @Override
    public T call(ResultData<T> resultData) {
        int code = resultData.state.code;
        // 不是200
        if (code != 200) {
            LogUtil.e("ResponseState Error: ", resultData.toString() + "\nparams:" + params + "\n" + getStackTrace());
            throw new ApiException(resultData);
        }
        return resultData.data;
    }

    /**
     * 获取堆栈信息
     * 备注：用于网络请求错误时定位
     *
     * @return
     */
    private String getStackTrace() {
        return stackTrace[1].toString();
    }

    /**
     * 获取api参数
     * 备注：用于网络请求错误时定位
     *
     * @return
     */
    private Map<String, String> getParams() {
        return params;
    }
}
