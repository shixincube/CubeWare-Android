package cube.ware.service.engine;

import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.CubeEngineListener;
import cube.service.common.CubeState;
import cube.service.common.model.CubeError;
import java.util.ArrayList;
import java.util.List;

/**
 * 引擎状态处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class CubeEngineHandle implements CubeEngineListener {

    private static CubeEngineHandle instance = new CubeEngineHandle();

    private List<CubeEngineListener> mListeners = new ArrayList<>();

    private CubeEngineHandle() {}

    public static CubeEngineHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        CubeEngine.getInstance().addCubeEngineListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().removeCubeEngineListener(this);
    }

    /**
     * 添加状态监听器
     *
     * @param listener
     */
    public void addListener(CubeEngineListener listener) {
        this.mListeners.add(listener);
    }

    /**
     * 移除状态监听器
     *
     * @param listener
     */
    public void removeListener(CubeEngineListener listener) {
        this.mListeners.remove(listener);
    }

    /**
     * 引擎启动完成
     */
    @Override
    public void onStarted() {
        LogUtil.i("onStarted:---------");
        for (CubeEngineListener engineWorkerListener : mListeners) {
            engineWorkerListener.onStarted();
        }
    }

    /**
     * 引擎状态变化
     *
     * @param state
     */
    @Override
    public void onStateChange(CubeState state) {
        LogUtil.i("onStateChange:--------- " + state);
        for (CubeEngineListener engineWorkerListener : mListeners) {
            engineWorkerListener.onStateChange(state);
        }
    }

    /**
     * 引擎停止
     */
    @Override
    public void onStopped() {
        LogUtil.i("onStopped:--------- ");
        for (CubeEngineListener engineWorkerListener : mListeners) {
            engineWorkerListener.onStopped();
        }
    }

    /**
     * 引擎错误
     *
     * @param error
     */
    @Override
    public void onFailed(CubeError error) {
        LogUtil.e("onFailed:--------- " + error);
        for (CubeEngineListener engineWorkerListener : mListeners) {
            engineWorkerListener.onFailed(error);
        }
    }
}
