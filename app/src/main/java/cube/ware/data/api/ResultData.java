package cube.ware.data.api;

/**
 * 服务器响应的结果数据
 *
 * @param <T>
 */
public class ResultData<T extends BaseData> {

    /**
     * 数据
     */
    public T data;

    public State state;

    public static class State {
        public int    code;
        public String desc;

        @Override
        public String toString() {
            return "State{" + "code=" + code + ", desc='" + desc + '\'' + '}';
        }
    }

    @Override
    public String toString() {
        return "ResultData{" + "data=" + data + ", state=" + state + '}';
    }
}
