package cube.ware.ui.splash;

import android.content.Context;

import com.common.utils.utils.log.LogUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class SplashPresenter extends SplashContract.Presenter {
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public SplashPresenter(Context context, SplashContract.View view) {
        super(context, view);
    }

    /**
     * 自动登录
     */
    public void autoLogin(boolean isAuto) {

        countDown(3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d("onCompleted: ");
                        if (isAuto) {
                            mView.enterMain();
                        } else {
                            mView.enterLogin();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        LogUtil.d("integer: "+integer);
                        mView.timeCountDown(integer);
                    }
                });


    }

    /**
     * 倒计时
     *
     * @param time
     * @return
     */
    public Observable<Integer> countDown(int time) {
        if (time < 0)
            time = 0;

        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(countTime + 1)
                .map(increaseTime -> countTime - increaseTime.intValue());


    }

}
