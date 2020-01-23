package cube.ware.utils;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.common.utils.utils.GsonUtil;
import com.common.utils.utils.log.LogUtil;

import java.lang.reflect.Type;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */
@Route(path = "/service/json")
public class JsonServiceImpl implements SerializationService {


    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return GsonUtil.toBean(input, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return GsonUtil.toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return GsonUtil.toBean(input, clazz);
    }

    @Override
    public void init(Context context) {

        LogUtil.i("JsonServiceImpl init...");
    }
}
