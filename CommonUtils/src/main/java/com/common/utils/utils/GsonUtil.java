package com.common.utils.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Gson工具类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class GsonUtil {
    private static Gson gson = new Gson();

    private GsonUtil() {}

    /**
     * object转成json
     *
     * @param object
     *
     * @return
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * object转成json
     *
     * @param object
     * @param typeOfT
     *
     * @return
     */
    public static String toJson(Object object, Type typeOfT) {
        return gson.toJson(object, typeOfT);
    }

    /**
     * json转成bean
     *
     * @param jsonStr
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> cls) {
        return gson.fromJson(jsonStr, cls);
    }

    /**
     * json转成bean
     *
     * @param jsonStr
     *
     * @return
     */
    public static <T> T toBean(String jsonStr, Type typeOfT) {
        return gson.fromJson(jsonStr, typeOfT);
    }

    /**
     * object转成bean
     *
     * @param object
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(Object object, Class<T> cls) {
        return gson.fromJson(gson.toJson(object), cls);
    }

    /**
     * object转成bean
     *
     * @param object
     * @param typeOfT
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(Object object, Type typeOfT) {
        return gson.fromJson(gson.toJson(object), typeOfT);
    }

    /**
     * JSONObject转成bean
     *
     * @param jsonObject
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(JSONObject jsonObject, Class<T> cls) {
        return gson.fromJson(jsonObject.toString(), cls);
    }

    /**
     * JSONObject转成bean
     *
     * @param jsonObject
     * @param typeOfT
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(JSONObject jsonObject, Type typeOfT) {
        return gson.fromJson(jsonObject.toString(), typeOfT);
    }

    /**
     * JSONArray转成bean
     *
     * @param jsonArray
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(JSONArray jsonArray, Class<T> cls) {
        return gson.fromJson(jsonArray.toString(), cls);
    }

    /**
     * JSONArray转成bean
     *
     * @param jsonArray
     * @param typeOfT
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(JSONArray jsonArray, Type typeOfT) {
        return gson.fromJson(jsonArray.toString(), typeOfT);
    }

    /**
     * object转成JSONObject
     *
     * @param object
     *
     * @return
     *
     * @throws JSONException
     */
    public static JSONObject toJSONObject(Object object) throws JSONException {
        return new JSONObject(gson.toJson(object));
    }

    /**
     * json转成JSONObject
     *
     * @param jsonStr
     *
     * @return
     *
     * @throws JSONException
     */
    public static JSONObject toJSONObject(String jsonStr) throws JSONException {
        return new JSONObject(jsonStr);
    }

    /**
     * object转成JSONArray
     *
     * @param object
     *
     * @return
     *
     * @throws JSONException
     */
    public static JSONArray toJSONArray(Object object) throws JSONException {
        return new JSONArray(toJson(object));
    }

    /**
     * json转成JSONArray
     *
     * @param jsonStr
     *
     * @return
     *
     * @throws JSONException
     */
    public static JSONArray toJSONArray(String jsonStr) throws JSONException {
        return new JSONArray(jsonStr);
    }

    /**
     * Collection转成JSONArray
     *
     * @param collection
     *
     * @return
     */
    public static JSONArray toJSONArray(Collection collection) throws JSONException {
        return new JSONArray(toJson(collection));
    }

    /**
     * json转成list
     *
     * @param jsonStr
     *
     * @return
     */
    public static <T> List<T> toList(String jsonStr) {
        return toBean(jsonStr, new TypeToken<List<T>>() {}.getType());
    }

    /**
     * json转成map
     *
     * @param jsonStr
     *
     * @return
     */
    public static <T> Map<String, T> toMap(String jsonStr) {
        return toBean(jsonStr, new TypeToken<Map<String, T>>() {}.getType());
    }

    /**
     * Json转List集合,遇到解析不了的，就使用这个
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        List<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (JsonElement elem : array) {
            mList.add(gson.fromJson(elem, cls));
        }
        return mList;
    }

    /**
     * 判断是否json字符串
     *
     * @param content
     *
     * @return
     */
    public static boolean isJson(String content) {
        try {
            Object object = new JSONTokener(content).nextValue();
            return object instanceof JSONObject || object instanceof JSONArray;
        } catch (JSONException ignored) {
        }
        return false;
    }

    /**
     * 将json中的null替换成""
     *
     * 用法：
     * GsonBuilder gb = new GsonBuilder();
     * gb.registerTypeAdapter(String.class, new StringConverter());
     * Gson gson = gb.create();
     */
    public static class StringConverter implements JsonSerializer<String>, JsonDeserializer<String> {
        @Override
        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return new JsonPrimitive("");
            }
            else {
                return new JsonPrimitive(src);
            }
        }

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.getAsJsonPrimitive().getAsString();
        }
    }

    /**
     * 将json中的null替换成""
     * 自定义Strig适配器
     */
    public static class StringTypeAdapter extends TypeAdapter<String> {

        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                // 在这里处理null改为空字符串
                writer.value("");
                return;
            }
            writer.value(value);
        }
    }
}
