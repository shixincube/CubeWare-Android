package cube.ware.service.widget;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import java.lang.ref.WeakReference;

/**
 * Created by froger_mcs on 21/03/16.
 */
public class KeyboardWatcher {

    private WeakReference<Activity>                 activityRef;
    private WeakReference<View>                     rootViewRef;
    // TODO: 2017/9/6 使用虚引用会有原来的popwindow接受不到监听的风险 不使用虚引用内存会占用较高 待优化
    private WeakReference<OnKeyboardToggleListener>                onKeyboardToggleListenerRef;
    private ViewTreeObserver.OnGlobalLayoutListener viewTreeObserverListener;

    public KeyboardWatcher(Activity activity) {
        activityRef = new WeakReference<>(activity);
        initialize();
    }

    public void setListener(OnKeyboardToggleListener onKeyboardToggleListener) {
        this.onKeyboardToggleListenerRef = new WeakReference<OnKeyboardToggleListener>(onKeyboardToggleListener);
    }

    public void destroy() {
        if (rootViewRef.get() != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                rootViewRef.get().getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserverListener);
            }
            else {
                rootViewRef.get().getViewTreeObserver().removeGlobalOnLayoutListener(viewTreeObserverListener);
            }
        }
    }

    private void initialize() {
        if (hasAdjustResizeInputMode()) {
            viewTreeObserverListener = new GlobalLayoutListener();
            rootViewRef = new WeakReference<>(activityRef.get().findViewById(Window.ID_ANDROID_CONTENT));
            rootViewRef.get().getViewTreeObserver().addOnGlobalLayoutListener(viewTreeObserverListener);
        }
        else {
            throw new IllegalArgumentException(String.format("Activity %s should have windowSoftInputMode=\"adjustResize\"" + "to make KeyboardWatcher working. You can set it in AndroidManifest.xml", activityRef.get().getClass().getSimpleName()));
        }
    }

    private boolean hasAdjustResizeInputMode() {
        return (activityRef.get().getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0;
    }

    private class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int     initialValue;
        boolean hasSentInitialAction;
        boolean isKeyboardShown;

        @Override
        public void onGlobalLayout() {
            if (initialValue == 0) {
                initialValue = rootViewRef.get().getHeight();
            }
            else {
                if (initialValue > rootViewRef.get().getHeight()) {
                    try {
                        if (onKeyboardToggleListenerRef.get() != null) {
                            if (!isKeyboardShown) {
                                isKeyboardShown = true;
                                onKeyboardToggleListenerRef.get().onKeyboardShown(initialValue - rootViewRef.get().getHeight());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    isKeyboardShown = false;
                    rootViewRef.get().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (onKeyboardToggleListenerRef.get() != null) {
                                    onKeyboardToggleListenerRef.get().onKeyboardClosed();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                hasSentInitialAction = true;
            }
        }
    }

    public interface OnKeyboardToggleListener {
        void onKeyboardShown(int keyboardSize);

        void onKeyboardClosed();
    }
}
