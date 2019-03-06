package cube.ware.service.core;

/**
 * 各服务监听的共有接口
 *
 * @author LiuFeng
 * @data 2019/1/22 20:42
 */
public interface CubeHandle {
    /**
     * 启动
     */
    public void start();

    /**
     * 停止
     */
    public void stop();
}
