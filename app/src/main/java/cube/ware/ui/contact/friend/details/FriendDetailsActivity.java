package cube.ware.ui.contact.friend.details;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.base.BaseActivity;
import com.common.eventbus.EventBusUtil;
import com.common.utils.glide.GlideUtil;
import cube.ware.AppConstants;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.api.CubeUI;
import cube.ware.common.MessageConstants;
import cube.ware.data.room.model.CubeUser;
import cube.ware.widget.bottomPopupDialog.BottomPopupDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/29.
 */
@Route(path = AppConstants.Router.FriendDetailsActivity)
public class FriendDetailsActivity extends BaseActivity<FriendDetailsContract.Presenter> implements FriendDetailsContract.View, View.OnClickListener {

    private ImageView               mTitleBack;
    private ImageView               mTitleIv;
    private ImageView               mTitleMore;
    private RelativeLayout          mToolbarLayout;
    private ImageView               mFaceIv;
    private TextView                mDisplayNameTv;
    private CollapsingToolbarLayout mCoolToolbarLayout;
    private ImageView               mImage_brackets;//昵称布局的尖括号需隐藏
    /**
     * 发消息
     */
    private TextView                mSendMessageTv;
    private AppBarLayout            mAppbarLayout;
    private TextView                mUserNumCodeTv;
    private RelativeLayout          mUserNumCodeRl;
    private TextView                mUserNameTv;
    private RelativeLayout          mUserNameRl;
    private LinearLayout            mDetailUserLayout;

    @Autowired(name = "user")
    public CubeUser mUser;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_friend_details;
    }

    @Override
    protected FriendDetailsContract.Presenter createPresenter() {
        return new FriendDetailsPresenter(this, this);
    }

    public void initView() {
        ARouter.getInstance().inject(this);
        mTitleBack = (ImageView) findViewById(R.id.title_back);
        mTitleIv = (ImageView) findViewById(R.id.title_iv);
        mTitleMore = (ImageView) findViewById(R.id.title_more);
        mToolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        mFaceIv = (ImageView) findViewById(R.id.face_iv);
        mDisplayNameTv = (TextView) findViewById(R.id.display_name_tv);
        mCoolToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cool_toolbar_layout);
        mSendMessageTv = (TextView) findViewById(R.id.send_message_tv);
        mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mUserNumCodeTv = (TextView) findViewById(R.id.user_num_code_tv);
        mUserNumCodeRl = (RelativeLayout) findViewById(R.id.user_num_code_rl);
        mUserNameTv = (TextView) findViewById(R.id.user_name_tv);
        mUserNameRl = (RelativeLayout) findViewById(R.id.user_name_rl);
        mImage_brackets = findViewById(R.id.image_arrow);
        mImage_brackets.setVisibility(View.GONE);
        mDetailUserLayout = (LinearLayout) findViewById(R.id.detail_user_layout);
    }

    @Override
    protected void initData() {
        if (mUser != null) {
            //进入用户的个人页面，刷新签名时间，所有调用loadSignatureCircleImage的方法再次从网络拉取图片
            GlideUtil.setAvatarSignature(System.currentTimeMillis());
            GlideUtil.loadSignatureCircleImage(AppManager.getAvatarUrl() + mUser.getCubeId(), mContext, mFaceIv, R.drawable.default_head_user);
            mDisplayNameTv.setText(mUser.getDisplayName());
            mUserNameTv.setText(mUser.getDisplayName());
            mUserNumCodeTv.setText(mUser.getCubeId());
            EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_CUBE_AVATAR);
        }
    }

    @Override
    protected void initListener() {
        mTitleBack.setOnClickListener(this);
        mTitleMore.setOnClickListener(this);
        mUserNameRl.setOnClickListener(this);
        mSendMessageTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.title_more:
                setMorePop();
                break;
            case R.id.user_name_rl:
                break;
            case R.id.send_message_tv:
                String chatName = TextUtils.isEmpty(mUser.getDisplayName()) ? mUser.getCubeId() : mUser.getDisplayName();
                CubeUI.getInstance().startP2PChat(this, mUser.getCubeId(), chatName);
                break;
        }
    }

    /**
     * 弹出更多对话框
     */
    private void setMorePop() {
        final BottomPopupDialog bottomPopupDialog;
        List<String> bottomDialogContents;//弹出列表的内容
        bottomDialogContents = new ArrayList<>();
        bottomDialogContents.add("删除好友");
        bottomPopupDialog = new BottomPopupDialog(this, bottomDialogContents);
        bottomPopupDialog.showCancelBtn(true);
        bottomPopupDialog.setCancelable(true);
        bottomPopupDialog.setRedPosition(0);
        bottomPopupDialog.show();
        bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        bottomPopupDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
