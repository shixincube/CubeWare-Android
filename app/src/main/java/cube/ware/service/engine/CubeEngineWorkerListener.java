package cube.ware.service.engine;

import cube.service.common.CubeState;
import cube.service.common.model.CubeError;

/**
 * Created by dth
 * Des: 引擎工作状态
 * Date: 2018/8/29.
 */

public interface CubeEngineWorkerListener {

    void onStarted();

    void onStateChange(CubeState cubeState);

    void onStopped();

    void onFailed(CubeError cubeError);
}
