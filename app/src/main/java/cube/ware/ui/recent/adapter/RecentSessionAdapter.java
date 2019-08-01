package cube.ware.ui.recent.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.FriendlyDateUtil;
import com.common.utils.utils.ToastUtil;

import cube.service.recent.RecentSession;
import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.ware.R;
import cube.ware.widget.PopupHorizontalMenu;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/28.
 */

public class RecentSessionAdapter extends BaseQuickAdapter<RecentSession,BaseViewHolder> {

    public RecentSessionAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecentSession item) {
        ImageView headIv = helper.getView(R.id.head_iv);
//        GlideUtil.loadCircleImage(item.userFace,mContext,headIv,R.drawable.default_head_user);
        helper.setText(R.id.message_name_tv, item.displayName)
                .setVisible(R.id.message_top_label_iv,item.topTime > 0)
                .setVisible(R.id.message_badge_tv, item.unreadCount > 0)
                .setText(R.id.message_badge_tv,item.unreadCount > 99 ? "99+" : String.valueOf(item.unreadCount))
                .setText(R.id.message_content_tv, item.content);
        if (item.time != -1) {
            String time = FriendlyDateUtil.recentTime(item.time);
            helper.setText(R.id.message_time_tv, time);
        }

        showPopWindow(helper.itemView,item);
    }

    public int findPosition(String sessionId) {
        List<RecentSession> recentSessions = getData();
        if (null != recentSessions && !recentSessions.isEmpty()) {
            for (int i = 0; i < recentSessions.size(); i++) {
                if (TextUtils.equals(recentSessions.get(i).sessionId,sessionId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 弹出更多选择框
     */
    private void showPopWindow(View view, final RecentSession recentSession) {
        List<String> popupMenuItemList = new ArrayList<>();
        if (recentSession.topTime != 0) {
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
                    case 0://置顶/取消置顶
                        ToastUtil.showToast(mContext,"置顶");
                        break;
                    case 1://删除
                        CubeEngine.getInstance().getRecentSessionService().deleteRecentSession(recentSession);
                        break;
                }
            }
        });
        popupHorizontalMenu.setIndicatorView(popupHorizontalMenu.getDefaultIndicatorView(32, 16, 0xFF212121));
    }
}
