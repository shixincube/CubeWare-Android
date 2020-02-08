package cube.ware.service.message.recent;

import android.content.Context;
import com.common.mvp.eventbus.Event;
import com.common.mvp.eventbus.EventBusUtil;
import cube.ware.common.MessageConstants;
import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.repository.CubeSessionRepository;
import cube.ware.data.room.model.CubeRecentSession;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class RecentPresenter extends RecentContract.Presenter {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public RecentPresenter(Context context, RecentContract.View view) {
        super(context, view);
    }

    @Override
    public void refreshRecentSessions() {
        CubeSessionRepository.getInstance().queryAllSessionViewModel().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeRecentViewModel>>() {
            @Override
            public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
                mView.onRefreshList(cubeRecentViewModels);
                queryUnReadAll();
            }
        });
    }

    @Override
    public <T> void handleReceiveEvent(Event<T> event) {
        switch (event.eventName) {
            case MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_LIST:
                List<CubeRecentSession> recentSessions = (List<CubeRecentSession>) event.data;
                refreshRecentSessions(recentSessions);
                break;

            case MessageConstants.Event.EVENT_REMOVE_RECENT_SESSION_SINGLE:
                mView.onRemoveSession((String) event.data);
                queryUnReadAll();
                break;

            case MessageConstants.Event.EVENT_REFRESH_CUBE_AVATAR:
                mView.onRefreshListAvatar();
                break;

            default:
                break;
        }
    }

    private void refreshRecentSessions(List<CubeRecentSession> recentSessions) {
        CubeSessionRepository.getInstance().querySessionViewModel(recentSessions).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeRecentViewModel>>() {
            @Override
            public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
                mView.onRefreshList(cubeRecentViewModels);
                queryUnReadAll();
            }
        });
    }

    public void queryUnReadAll() {
        CubeSessionRepository.getInstance().queryAllSessionsUnReadCount().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                EventBusUtil.post(MessageConstants.Event.EVENT_UNREAD_MESSAGE_SUM, integer);
            }
        });
    }
}
