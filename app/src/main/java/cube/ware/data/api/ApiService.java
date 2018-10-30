package cube.ware.data.api;

import cube.ware.data.model.dataModel.CubeAvator;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiService {

    @POST("user/created")
    @FormUrlEncoded
    Call<ResultData<LoginCubeData>> login(@FieldMap Map<String, String> params);

    @POST("user/login")
    @FormUrlEncoded
    Call<ResultData<LoginData>> getCubeToken(@FieldMap Map<String, String> params);

    @POST("user/page/findByAppId")
    @FormUrlEncoded
    Call<ResultData<TotalData>> find(@FieldMap Map<String, String> params);


    @Multipart
    // @POST("http://upload.shixincube.com/v3/file/uploadAvatar")//正式服
    //目前只有测试服，正式服还没有部署
    @POST("http://125.208.1.67:6011/v3/file/uploadAvatar")
    Call<ResultData<CubeAvator>> uploadAvatar(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file);
}
