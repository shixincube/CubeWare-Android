package cube.ware.data.model.dataModel;

import cube.ware.data.api.BaseData;

/**
 * author: kun .
 * date:   On 2018/9/7
 */
public class CubeAvatarData extends BaseData {
    /**
     * fileId : f86291ae-a632-4757-a7f3-97ccb7f73b47
     * name : avatar.jpg
     * url : http://125.208.1.67:6003/file/avatar/36172
     */

    private String fileId;
    private String name;
    private String url;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
