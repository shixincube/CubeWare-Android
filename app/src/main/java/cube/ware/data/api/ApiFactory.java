package cube.ware.data.api;

import com.common.utils.utils.log.LogUtil;

import cube.service.user.model.User;
import cube.ware.CubeUI;
import cube.ware.data.model.dataModel.CubeAvator;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ApiFactory {
    private static ApiFactory instance = new ApiFactory();
    private ApiService mApiService;

    private ApiFactory() {
        mApiService = ApiManager.getInstance().getApiService();
    }

    public static ApiFactory getInstance() {
        return instance;
    }

    public void login(String appId, String appKey,Callback<ResultData<LoginCubeData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        this.mApiService.login(params).enqueue(callback);
    }

    public void getCubeToken(String appId, String appKey, String cube,Callback<ResultData<LoginData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("cube", cube);
        this.mApiService.getCubeToken(params).enqueue(callback);
    }

    public void find(String appId, int page, int rows, Callback<ResultData<TotalData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("page", String.valueOf(page));
        params.put("rows", String.valueOf(rows));
        this.mApiService.find(params).enqueue(callback);
    }

    public void uploadAvatar(String token, File file, Callback<ResultData<CubeAvator>> callback) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("token", RequestBody.create(null, token));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        this.mApiService.uploadAvatar(params,body).enqueue(callback);
    }


    /**
     * 查询用户信息，根据用户cube号
     * @param cubeId
     * @return
     */
    public Observable<User> queryUser(String cubeId) {
        //不知道接口是什么，之前的坐标里面比较的都是本地缓存的和数据库中的
        //怎么去查呀，我的哥。。。。。。。
        return null;
    }
}
