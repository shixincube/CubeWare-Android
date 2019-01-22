package cube.ware.data.api;

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

/**
 * 网络请求api工厂
 *
 * @author LiuFeng
 * @data 2019/1/22 14:26
 */
public class ApiFactory {
    private static ApiFactory instance = new ApiFactory();

    private ApiFactory() {}

    public static ApiFactory getInstance() {
        return instance;
    }

    private ApiService getApiService() {
        return ApiManager.getInstance().getApiService();
    }

    public void login(String appId, String appKey, Callback<ResultData<LoginCubeData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        getApiService().login(params).enqueue(callback);
    }

    public void getCubeToken(String appId, String appKey, String cube, Callback<ResultData<LoginData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("cube", cube);
        getApiService().getCubeToken(params).enqueue(callback);
    }

    public void find(String appId, int page, int rows, Callback<ResultData<TotalData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("page", String.valueOf(page));
        params.put("rows", String.valueOf(rows));
        getApiService().find(params).enqueue(callback);
    }

    public void uploadAvatar(String token, File file, Callback<ResultData<CubeAvator>> callback) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("token", RequestBody.create(null, token));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        getApiService().uploadAvatar(params, body).enqueue(callback);
    }
}
