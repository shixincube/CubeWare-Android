package com.common.mvp.rx;

import android.support.annotation.NonNull;
import com.common.utils.utils.log.LogUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 用RxJava实现的EventBus
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class RxBus {
    private static final String TAG = RxBus.class.getSimpleName();
    private static RxBus mInstance = new RxBus();

    private ConcurrentHashMap<Object, List<Subject>> mSubjectMap = new ConcurrentHashMap<>();

    private RxBus() {
    }

    public static synchronized RxBus getInstance() {
        return mInstance;
    }

    /**
     * 订阅事件源-主线程
     * <p/>
     * 用法：
     * Observable<?> observable = RxBus.getInstance().register(eventName);
     * RxBus.getInstance().OnEventMainThread(observable，Action1);
     *
     * @param observable
     * @param action1
     *
     * @return
     */
    public RxBus OnEventMainThread(Observable<?> observable, Action1<Object> action1) {
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action1, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return getInstance();
    }

    /**
     * 注册事件源
     *
     * @param tag
     *
     * @return
     */
    public Observable<Object> register(@NonNull Object tag) {
        LogUtil.i("register tag=" + tag);
        List<Subject> subjectList = this.mSubjectMap.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            this.mSubjectMap.put(tag, subjectList);
        }
        Subject<Object, Object> subject = new SerializedSubject<>(PublishSubject.create());
        subjectList.add(subject);
        return subject;
    }

    /**
     * 判断事件是否已注册
     *
     * @param tag
     *
     * @return
     */
    public boolean registered(@NonNull Object tag) {
        List<Subject> subjectList = this.mSubjectMap.get(tag);
        if (subjectList != null && !subjectList.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 取消注册
     *
     * @param tag
     */
    public void unregister(@NonNull Object tag) {
        List<Subject> subjects = this.mSubjectMap.get(tag);
        if (null != subjects) {
            this.mSubjectMap.remove(tag);
        }
    }

    /**
     * 取消注册
     *
     * @param tag
     * @param observable
     *
     * @return
     */
    public RxBus unregister(@NonNull Object tag, @NonNull Observable<Object> observable) {
        LogUtil.i("unregister tag=" + tag + " current thread=" + Thread.currentThread() + " content=" + observable);
        List<Subject> subjects = this.mSubjectMap.get(tag);
        if (null != subjects) {
            subjects.remove(observable);
            if (isEmpty(subjects)) {
                this.mSubjectMap.remove(tag);
            }
        }
        return getInstance();
    }

    /**
     * 发送事件
     *
     * @param content
     */
    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    /**
     * 发送事件
     *
     * @param tag
     * @param content
     */
    public void post(@NonNull Object tag, @NonNull Object content) {
        LogUtil.i("post tag=" + tag + " current thread=" + Thread.currentThread() + " content=" + content);
        List<Subject> subjectList = this.mSubjectMap.get(tag);
        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

    private static boolean isEmpty(Collection<Subject> collection) {
        return null == collection || collection.isEmpty();
    }
}
