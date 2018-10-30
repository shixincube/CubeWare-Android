package cube.ware.service.engine;

import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.CubeEngineListener;
import cube.service.common.CubeState;
import cube.service.common.model.CubeError;
import cube.ware.CubeUI;

/**
 * 引擎状态处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class CubeEngineHandle implements CubeEngineListener {

    private static CubeEngineHandle instance = new CubeEngineHandle();

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
     * 引擎启动完成
     */
    @Override
    public void onStarted() {
        LogUtil.i("onStarted:---------");
        List<CubeEngineWorkerListener> cubeEngineWorkerListener = CubeUI.getInstance().getCubeEngineWorkerListener();
        for (CubeEngineWorkerListener engineWorkerListener : cubeEngineWorkerListener) {
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
        LogUtil.i("onStateChange:--------- " +state);
        List<CubeEngineWorkerListener> cubeEngineWorkerListener = CubeUI.getInstance().getCubeEngineWorkerListener();
        for (CubeEngineWorkerListener engineWorkerListener : cubeEngineWorkerListener) {
            engineWorkerListener.onStateChange(state);
        }
    }

    /**
     * 引擎停止
     */
    @Override
    public void onStopped() {
        LogUtil.i("onStopped:--------- ");
        List<CubeEngineWorkerListener> cubeEngineWorkerListener = CubeUI.getInstance().getCubeEngineWorkerListener();
        for (CubeEngineWorkerListener engineWorkerListener : cubeEngineWorkerListener) {
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
        LogUtil.e("onFailed:--------- "+error);
        List<CubeEngineWorkerListener> cubeEngineWorkerListener = CubeUI.getInstance().getCubeEngineWorkerListener();
        for (CubeEngineWorkerListener engineWorkerListener : cubeEngineWorkerListener) {
            engineWorkerListener.onFailed(error);
        }
    }
}
