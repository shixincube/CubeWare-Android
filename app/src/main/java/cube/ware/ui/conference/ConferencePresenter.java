package cube.ware.ui.conference;

import android.content.Context;

import java.util.List;

import cube.data.model.reponse.ConferenceData;
import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.group.GroupType;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class ConferencePresenter extends ConferenceContract.Presenter{
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public ConferencePresenter(Context context, ConferenceContract.View view) {
        super(context, view);
    }

    /**
     * 通过cubeId获取到与自己相关的会议列表
     * @param cubeid
     * @param groupTypes
     */
    @Override
    public void getConferenceList(String cubeid, List<GroupType> groupTypes) {
        CubeEngine.getInstance().getConferenceService().queryConferenceByCubeId(cubeid, groupTypes, new CubeCallback<ConferenceData>() {
            @Override
            public void onSucceed(ConferenceData conferenceData) {
                if(conferenceData!=null){
                    mView.getConference(conferenceData.conferences);
                }
            }

            @Override
            public void onFailed(CubeError error) {
                if(error!=null){
                    mView.getConferenceFail(error);
                }
            }
        });
    }
}
