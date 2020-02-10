package cube.ware.data.api;

import cube.ware.data.model.dataModel.CubeAvatar;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * 网络请求api工厂
 *
 * @author LiuFeng
 * @data 2020/2/3 18:16
 */
public class ApiFactory {
    private static ApiFactory instance = new ApiFactory();

    private ApiService mApiService;

    private ApiFactory() {
        mApiService = ApiManager.getInstance().getApiService();
    }

    public static ApiFactory getInstance() {
        return instance;
    }

    /**
     * 创建新用户
     *
     * @param appId
     * @param appKey
     */
    public Observable<LoginCubeData> createUser(String appId, String appKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        return this.mApiService.createUser(params).map(new ApiResultFunc<LoginCubeData>(params)).subscribeOn(Schedulers.io());
    }

    /**
     * 获取授权token
     *
     * @param appId
     * @param appKey
     * @param cube
     */
    public Observable<LoginData> queryCubeToken(String appId, String appKey, String cube) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("cube", cube);
        return this.mApiService.queryCubeToken(params).map(new ApiResultFunc<LoginData>(params)).subscribeOn(Schedulers.io());
    }

    /**
     * 查询appId下的用户列表
     *
     * @param appId
     * @param appKey
     * @param page
     * @param rows
     */
    public Observable<TotalData> queryUsers(String appId, String appKey, int page, int rows) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("page", String.valueOf(page));
        params.put("rows", String.valueOf(rows));
        return this.mApiService.queryUsers(params).map(new ApiResultFunc<TotalData>(params)).subscribeOn(Schedulers.io());
    }

    /**
     * 上传更新头像
     *
     * @param token
     * @param file
     */
    public Observable<CubeAvatar> uploadAvatar(String token, File file) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("token", RequestBody.create(null, token));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return this.mApiService.uploadAvatar(params, body).map(new ApiResultFunc<CubeAvatar>()).subscribeOn(Schedulers.io());
    }
}
