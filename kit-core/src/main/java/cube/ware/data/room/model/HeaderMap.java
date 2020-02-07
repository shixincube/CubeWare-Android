package cube.ware.data.room.model;

import java.io.Serializable;

/**
 * Created by Guoxin on 2018/1/30.
 */

public class HeaderMap implements Serializable {

    public String key;
    public Object value;

    @Override
    public String toString() {
        return "HeaderMap{" + "key='" + key + '\'' + ", value='" + value + '\'' + '}';
    }
}
