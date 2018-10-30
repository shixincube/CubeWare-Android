package cube.ware.ui.setting;

import android.content.Context;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/17.
 */

public class SettingPresenter extends SettingContract.Presenter{
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public SettingPresenter(Context context, SettingContract.View view) {
        super(context, view);
    }
}
