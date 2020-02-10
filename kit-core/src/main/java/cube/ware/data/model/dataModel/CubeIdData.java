package cube.ware.data.model.dataModel;

import cube.ware.data.api.BaseData;

public class CubeIdData extends BaseData {
    public String cube;

    public String getCube() {
        return cube;
    }

    public void setCube(String cube) {
        this.cube = cube;
    }

    @Override
    public String toString() {
        return "LoginCubeData{" + "cube='" + cube + '\'' + '}';
    }
}
