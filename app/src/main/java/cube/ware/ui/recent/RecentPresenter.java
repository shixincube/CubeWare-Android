package cube.ware.ui.recent;

import android.content.Context;

import com.common.mvp.rx.RxBus;
import com.common.mvp.rx.RxManager;
import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.repository.CubeRecentSessionRepository;
import cube.ware.eventbus.CubeEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class RecentPresenter extends RecentContract.Presenter{


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
        Subscription subscribe = CubeRecentSessionRepository.getInstance().queryAllUnReadCubeRecentSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CubeRecentViewModel>>() {
                    @Override
                    public void call(List<CubeRecentViewModel> cubeRecentViewModels) {

                        mView.onRefreshList(cubeRecentViewModels);
                        queryUnReadAll();
                    }
                });

        addSubscribe(subscribe);
    }

    @Override
    public void subscribeChange() {
        mRxManager.on(CubeEvent.EVENT_REFRESH_RECENT_SESSION_LIST, new Action1<Object>() {
            @Override
            public void call(Object o) {
                CubeRecentSessionRepository.getInstance().queryAllUnReadCubeRecentSession()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<CubeRecentViewModel>>() {
                            @Override
                            public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
                                mView.onRefreshList(cubeRecentViewModels);
                            }
                        });

                queryUnReadAll();
            }
        });

        mRxManager.on(CubeEvent.EVENT_REFRESH_RECENT_SESSION_SINGLE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                CubeRecentSessionRepository.getInstance().queryUnReadCubeRecentSession((String) o)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<CubeRecentViewModel>() {
                            @Override
                            public void call(CubeRecentViewModel cubeRecentViewModel) {
                                if(cubeRecentViewModel == null)return;
                                mView.onRefresh(cubeRecentViewModel);
                                queryUnReadAll();
                            }
                        });
            }
        });

        mRxManager.on(CubeEvent.EVENT_REMOVE_RECENT_SESSION_SINGLE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if(o == null)return;
               mView.onRemoveSession((String) o);

                queryUnReadAll();
            }
        });

    }

    public void queryUnReadAll() {
        CubeRecentSessionRepository.getInstance().queryUnAllReadCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        LogUtil.i("queryUnredadAllCout: "+integer);
                        RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM,integer);
                    }
                });
    }

    @Override
    public void onDestroy() {
        mRxManager.clear();
        super.onDestroy();
    }
}
