package cube.ware.service.message.recent;

import android.content.Context;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.mvp.rx.RxManager;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.repository.CubeSessionRepository;
import cube.ware.common.MessageConstants;
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

    RxManager mRxManager = new RxManager();

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
    public void getRecentSessionList() {
        CubeSessionRepository.getInstance().queryAllSessionsViewModel().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeRecentViewModel>>() {
            @Override
            public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
                mView.onRefreshList(cubeRecentViewModels);
                queryUnReadAll();
            }
        });
    }

    @Override
    public void subscribeChange() {
        mRxManager.on(MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_LIST, new Action1<Object>() {
            @Override
            public void call(Object o) {
                CubeSessionRepository.getInstance().queryAllSessionsViewModel().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeRecentViewModel>>() {
                    @Override
                    public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
                        mView.onRefreshList(cubeRecentViewModels);
                    }
                });

                queryUnReadAll();
            }
        });

        mRxManager.on(MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_SINGLE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                CubeRecentSession recentSession = (CubeRecentSession) o;
                CubeSessionRepository.getInstance().querySessionViewModel(recentSession).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeRecentViewModel>() {
                    @Override
                    public void call(CubeRecentViewModel cubeRecentViewModel) {
                        if (cubeRecentViewModel == null) {
                            return;
                        }
                        mView.onRefresh(cubeRecentViewModel);
                        queryUnReadAll();
                    }
                });
            }
        });

        mRxManager.on(MessageConstants.Event.EVENT_REMOVE_RECENT_SESSION_SINGLE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o == null) {
                    return;
                }
                mView.onRemoveSession((String) o);

                queryUnReadAll();
            }
        });

        mRxManager.on(MessageConstants.Event.EVENT_REFRESH_CUBE_AVATAR, new Action1<Object>() {
            @Override
            public void call(Object o) {
                mView.onRefreshListAvatar();
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

    @Override
    public void onDestroy() {
        mRxManager.clear();
        super.onDestroy();
    }
}
