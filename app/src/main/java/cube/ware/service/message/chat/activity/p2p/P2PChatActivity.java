package cube.ware.service.message.chat.activity.p2p;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.rx.RxPermissionUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;
import java.util.List;

import cube.service.message.model.Receiver;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.service.message.chat.BaseChatActivity;
import cube.ware.service.message.chat.ChatCustomization;
import cube.ware.service.message.chat.activity.file.FileActivity;
import cube.ware.service.message.chat.message.MessageFragment;
import cube.ware.service.message.chat.panel.input.InputPanel;
import cube.ware.utils.GlideImageLoader;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

/**
 * Created by dth
 * Des: 一对一聊天页面
 * Date: 2018/8/31.
 */
@Route(path = AppConstants.Router.P2PChatActivity)
public class P2PChatActivity extends BaseChatActivity implements InputPanel.OnBottomNavigationListener {


    private ImagePicker mImagePicker;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_p2p_chat;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
//        ARouter.getInstance().inject(this);
    }

    public static void start(Context context, String chatId, String name, ChatCustomization chatCustomization, long messageSn) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.EXTRA_CHAT_ID, chatId);
        intent.putExtra(AppConstants.EXTRA_CHAT_NAME, name);
        intent.putExtra(AppConstants.EXTRA_CHAT_CUSTOMIZATION, chatCustomization);
        intent.putExtra(AppConstants.EXTRA_CHAT_MESSAGE, messageSn);
        intent.setClass(context, P2PChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initToolBar() {


    }

    @Override
    protected MessageFragment buildFragment() {
        Bundle arguments = getIntent().getExtras();
        MessageFragment fragment = MessageFragment.newInstance(CubeSessionType.P2P, arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        fragment.setBottomNavigationListener(this);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE_LOCAL_FILE: { // 发送文件
                if (resultCode == FileActivity.TAKE_FILE_CODE && null != data) {
                    ArrayList<String> filePathList = data.getStringArrayListExtra(FileActivity.TAKE_FILE_LIST);
                    if (null != filePathList && !filePathList.isEmpty()) {
                        Observable.from(filePathList).subscribe(new Action1<String>() {
                            @Override
                            public void call(String filePath) {
                                MessageManager.getInstance().sendFileMessage(mContext, CubeSessionType.P2P, new Receiver(mChatId,mChatName), filePath, isAnonymous, false);
                            }
                        });
                    }
                }
                break;
            }
            case AppConstants.REQUEST_CODE_LOCAL_IMAGE: {    // 发送本地图片
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS && null != data) {
                    final List<ImageItem> imageItemList = data.getParcelableArrayListExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    final boolean isOrigin = data.getBooleanExtra("isOrigin", false);
                    mImagePicker.clearSelectedImages();
                    if (null != imageItemList && !imageItemList.isEmpty()) {
                        Observable.from(imageItemList).subscribe(new Observer<ImageItem>() {
                            @Override
                            public void onNext(ImageItem imageItem) {
                                LogUtil.i("选中图片的路径：" + imageItem.path);
                                String imagePath = imageItem.path;
                                MessageManager.getInstance().sendFileMessage(mContext, CubeSessionType.P2P, new Receiver(mChatId,mChatName), imagePath, isAnonymous, isOrigin);
                            }

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(P2PChatActivity.this, 0, "图片无效");
                            }
                        });
                    }
                }
                break;
            }
        }
    }



    public String getChatId() {
        return this.mChatId;
    }


    @Override
    public void onCameraListener() {

    }

    @Override
    public void onSendFileListener() {
        RxPermissionUtil.requestStoragePermission(this).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(P2PChatActivity.this, FileActivity.class);
                    intent.putExtra(FileActivity.REQUEST_CODE, AppConstants.REQUEST_CODE_LOCAL_FILE);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_LOCAL_FILE);
                    overridePendingTransition(R.anim.activity_open, 0);
                }
                else {
                    ToastUtil.showToast(P2PChatActivity.this, 0, getString(R.string.request_storage_permission));
                }
            }
        });
    }

    @Override
    public void onSendImageListener() {

        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new GlideImageLoader()); // 设置图片加载器
        mImagePicker.setMultiMode(true); // 设置为多选模式
        mImagePicker.setShowCamera(true);    // 设置显示相机
        mImagePicker.setSelectLimit(9);
        mImagePicker.setCrop(false);     // 设置拍照后不裁剪

        RxPermissionUtil.requestStoragePermission(this).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(P2PChatActivity.this, ImageGridActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_LOCAL_IMAGE);
                    overridePendingTransition(R.anim.activity_open, 0);
                }
                else {
                    ToastUtil.showToast(P2PChatActivity.this, 0, getString(R.string.request_storage_permission));
                }
            }
        });
    }

    @Override
    public void onAvatarClicked(Context context, CubeMessage cubeMessage) {
//        ToastUtil.showToast(App.getContext(),"点击头像");
    }

    @Override
    public void onAvatarLongClicked(Context context, CubeMessage cubeMessage) {
//        ToastUtil.showToast(App.getContext(),"长按头像");
    }
}
