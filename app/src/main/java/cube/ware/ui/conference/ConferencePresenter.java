package cube.ware.ui.conference;

import android.content.Context;
import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class ConferencePresenter extends ConferenceContract.Presenter {
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
     *
     * @param cubeid
     * @param groupTypes
     */
    @Override
    public void getConferenceList(String cubeid, List<GroupType> groupTypes) {
        //查询
        //        CubeEngine.getInstance().getConferenceService().queryConferenceByCubeId(cubeid, groupTypes, new CubeCallback<ConferenceData>() {
        //            @Override
        //            public void onSucceed(ConferenceData conferenceData) {
        //                if(conferenceData!=null){
        //                    mView.getConference(conferenceData.conferences);
        //                    LogUtil.i("q-----------------------query_ori",conferenceData.conferences.toString());
        //                }
        //            }
        //
        //            @Override
        //            public void onFailed(CubeError error) {
        //                if(error!=null){
        //                    mView.getConferenceFail(error);
        //                }
        //            }
        //        });

        CubeEngine.getInstance().getConferenceService().queryConferences(groupTypes, new CubeCallback<List<Conference>>() {
            @Override
            public void onSucceed(List<Conference> conferenceList) {
                mView.getConference(conferenceList);
            }

            @Override
            public void onFailed(CubeError error) {
                if (error != null) {
                    mView.getConferenceFail(error);
                }
            }
        });
    }
}
