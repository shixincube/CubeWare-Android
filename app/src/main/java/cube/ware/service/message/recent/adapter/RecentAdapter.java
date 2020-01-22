package cube.ware.service.message.recent.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.FriendlyDateUtil;
import com.common.utils.utils.glide.GlideUtil;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.R;
import cube.ware.service.message.recent.manager.RecentSessionManager;
import cube.ware.widget.PopupHorizontalMenu;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/29.
 */

public class RecentAdapter extends BaseQuickAdapter<CubeRecentViewModel, BaseViewHolder> {

    public RecentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CubeRecentViewModel item) {

        CubeRecentSession cubeRecentSession = item.cubeRecentSession;
        ImageView headIv = helper.getView(R.id.head_iv);
        //        GlideUtil.loadCircleImage(item.userFace,mContext,headIv,R.drawable.default_head_user);
        GlideUtil.loadSignatureCircleImage(CubeCore.getInstance().getAvatarUrl() + item.cubeRecentSession.getSessionId(), mContext, headIv, R.drawable.default_head_user);
        //        helper.setText(R.id.message_name_tv, TextUtils.isEmpty(item.userName) ? cubeRecentSession.getSessionId() : item.userName)
        helper.setText(R.id.message_name_tv, cubeRecentSession.getSessionName()).setVisible(R.id.message_badge_tv, cubeRecentSession.getUnRead() > 0).setText(R.id.message_badge_tv, cubeRecentSession.getUnRead() > 99 ? "99+" : String.valueOf(cubeRecentSession.getUnRead())).setText(R.id.message_content_tv, cubeRecentSession.getContent());
        if (cubeRecentSession.getTimestamp() != -1) {
            String time = FriendlyDateUtil.recentTime(cubeRecentSession.getTimestamp());
            helper.setText(R.id.message_time_tv, time);
        }

        //        showPopWindow(helper.itemView,item);
    }

    public int findPosition(String sessionId) {
        List<CubeRecentViewModel> cubeRecentViewModels = getData();
        if (null != cubeRecentViewModels && !cubeRecentViewModels.isEmpty()) {
            for (int i = 0; i < cubeRecentViewModels.size(); i++) {
                if (TextUtils.equals(cubeRecentViewModels.get(i).cubeRecentSession.getSessionId(), sessionId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 弹出更多选择框
     */
    private void showPopWindow(View view, final CubeRecentViewModel cubeRecentViewModel) {
        List<String> popupMenuItemList = new ArrayList<>();
        //暂隐藏置顶和删除功能
        if (cubeRecentViewModel.cubeRecentSession.isTop()) {
            popupMenuItemList.add("取消置顶");
        }
        else {
            popupMenuItemList.add("置顶");
        }
        popupMenuItemList.add("删除");
        PopupHorizontalMenu popupHorizontalMenu = new PopupHorizontalMenu();
        popupHorizontalMenu.init(mContext, view, popupMenuItemList, new PopupHorizontalMenu.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, String type, int position) {
                switch (position) {
                    //                    case 0://置顶/取消置顶
                    //                        ToastUtil.showToast(mContext,"置顶");
                    //                        break;
                    case 1://删除
                        RecentSessionManager.getInstance().removeRecentSession(cubeRecentViewModel.cubeRecentSession.getSessionId());
                        break;
                }
            }
        });
        popupHorizontalMenu.setIndicatorView(popupHorizontalMenu.getDefaultIndicatorView(32, 16, 0xFF212121));
    }
}
