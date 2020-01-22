package cube.ware.data.model;

import android.text.TextUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import cube.ware.data.room.model.CubeMessage;
import java.io.Serializable;

/**
 * 消息列表展示
 *
 * @author Wangxx
 * @date 2017/1/18
 */
public class CubeMessageViewModel implements Serializable, MultiItemEntity {
    public CubeMessage mMessage;
    public String      userNme;
    public String      userFace;
    public String      remark;

    @Override
    public int getItemType() {
        return mMessage.getMessageType().value;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (o instanceof CubeMessageViewModel) {
            CubeMessageViewModel message = (CubeMessageViewModel) o;
            return this.mMessage.getMessageSN() == message.mMessage.getMessageSN();
        }
        return false;
    }

    /**
     * 是否是群消息
     *
     * @return
     */
    public boolean isGroupMessage() {
        return !TextUtils.isEmpty(this.mMessage.getGroupId());
    }

    /**
     * 是否是接收的消息
     *
     * @return
     */
    public boolean isReceivedMessage() {
        return this.mMessage.isReceivedMessage();
    }

    @Override
    public String toString() {
        return "CubeMessageViewModel{" + "mMessage=" + mMessage + ", userNme='" + userNme + '\'' + ", userFace='" + userFace + '\'' + '}';
    }
}
