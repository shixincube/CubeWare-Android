package cube.ware.service.message.chat.panel.input.function;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.service.conference.model.Conference;
import cube.ware.R;
import cube.ware.service.message.chat.BaseChatActivity;
import cube.ware.service.message.chat.panel.input.function.adapter.FunctionPagerAdapter;

/**
 * 更多功能组件操作面板
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public class FunctionPanel {

    // 初始化更多布局adapter
    public static void init(BaseChatActivity activity, List<BaseFunction> actions) {
        LogUtil.i("功能视图个数====>" + actions.size());
        final ViewPager viewPager = (ViewPager) activity.findViewById(R.id.function_viewPager);
        final ViewGroup indicator = (ViewGroup) activity.findViewById(R.id.function_page_indicator);
        FunctionPagerAdapter adapter = new FunctionPagerAdapter(activity, actions);
        viewPager.setAdapter(adapter);
        initPageListener(indicator, adapter.getCount(), viewPager);
    }

    // 初始化更多布局PageListener
    private static void initPageListener(final ViewGroup indicator, final int count, final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setIndicator(indicator, count, position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setIndicator(indicator, count, 0);
    }

    /**
     * 设置页码
     */
    private static void setIndicator(ViewGroup indicator, int total, int current) {
        if (total <= 1) {
            indicator.removeAllViews();
        }
        else {
            indicator.removeAllViews();
            for (int i = 0; i < total; i++) {
                ImageView imageView = new ImageView(indicator.getContext());
                imageView.setId(i);
                // 判断当前页码来更新
                if (i == current) {
                    imageView.setBackgroundResource(R.drawable.ic_page_selected);
                }
                else {
                    imageView.setBackgroundResource(R.drawable.ic_page_unselected);
                }
                indicator.addView(imageView);
            }
        }
    }

    /**
     * 陌生人入会
     *
     * @param context
     * @param conference
     */
    public static void showJoinTip(final Context context, final String chatId, final Conference conference) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView msg = new TextView(context);
        msg.setText(context.getString(R.string.group_call_is_talking));
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, ScreenUtil.dip2px(15), 0, 0);
        msg.setTextSize(18);
        builder.setView(msg);
        builder.setPositiveButton("加入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //CubeEngine.getInstance().getConferenceService().applyJoin(conferenceId, false);
//                GroupCallActivity.start(context, chatId, conference, conference.getFounder(), CallStatus.GROUP_CALL_JOIN);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
