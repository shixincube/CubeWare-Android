package cube.ware.data.api;

import cube.ware.data.model.dataModel.CubeAvatar;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * 网络请求接口
 *
 * @author LiuFeng
 * @data 2020/2/3 18:19
 */
public interface ApiService {

    /**
     * 创建新用户
     *
     * @param params
     *
     * @return
     */
    @POST("user/created")
    @FormUrlEncoded
    Observable<ResultData<LoginCubeData>> createUser(@FieldMap Map<String, String> params);

    /**
     * 获取授权token
     *
     * @param params
     *
     * @return
     */
    @POST("cube/login")
    @FormUrlEncoded
    Observable<ResultData<LoginData>> queryCubeToken(@FieldMap Map<String, String> params);

    /**
     * 查询appId下的用户列表
     *
     * @param params
     *
     * @return
     */
    @POST("user/page/findByAppId")
    @FormUrlEncoded
    Observable<ResultData<TotalData>> queryUsers(@FieldMap Map<String, String> params);

    /**
     * 上传更新头像
     *
     * @param params
     * @param file
     *
     * @return
     */
    @Multipart
    @POST("https://dev.upload.shixincube.cn/v3/file/uploadAvatar")
    Observable<ResultData<CubeAvatar>> uploadAvatar(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file);
}
