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

public class ApiFactory {
    private static ApiFactory instance = new ApiFactory();

    private ApiService mApiService;

    private ApiFactory() {
        mApiService = ApiManager.getInstance().getApiService();
    }

    public static ApiFactory getInstance() {
        return instance;
    }

    public void createUser(String appId, String appKey, Callback<ResultData<LoginCubeData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        this.mApiService.createUser(params).enqueue(callback);
    }

    public void getCubeToken(String appId, String appKey, String cube, Callback<ResultData<LoginData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("cube", cube);
        this.mApiService.getCubeToken(params).enqueue(callback);
    }

    public void queryUsers(String appId, String appKey, int page, int rows, Callback<ResultData<TotalData>> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appKey", appKey);
        params.put("page", String.valueOf(page));
        params.put("rows", String.valueOf(rows));
        this.mApiService.queryUsers(params).enqueue(callback);
    }

    public void uploadAvatar(String token, File file, Callback<ResultData<CubeAvator>> callback) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("token", RequestBody.create(null, token));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        this.mApiService.uploadAvatar(params, body).enqueue(callback);
    }
}
