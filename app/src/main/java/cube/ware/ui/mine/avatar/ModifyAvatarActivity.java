package cube.ware.ui.mine.avatar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.base.BaseActivity;
import com.common.rx.RxPermissionUtil;
import com.common.rx.RxSchedulers;
import com.common.utils.BitmapUtils;
import com.common.utils.ToastUtil;
import com.common.utils.glide.GlideUtil;
import cube.ware.AppConstants;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.utils.SpUtil;
import rx.functions.Action1;

@Route(path = AppConstants.Router.ModifyAvatarActivity)
public class ModifyAvatarActivity extends BaseActivity<ModifyAvatarPresenter> implements ModifyAvatarContract.View {

    private ImageView   mIvAvatar;
    private ImageView   mIvBack;
    private ProgressBar mProgressBar;

    private int REQUEST_CODE_FROM_GALLERY = 1001;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_change_avator;
    }

    @Override
    protected ModifyAvatarPresenter createPresenter() {
        return new ModifyAvatarPresenter(this, this);
    }

    protected void initListener() {
        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomChooseDialog instance = BottomChooseDialog.getInstance();
                instance.show(getSupportFragmentManager(), "");
                return true;
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mIvAvatar = findViewById(R.id.iv_avator);
        mProgressBar = findViewById(R.id.progressbar);
        GlideUtil.loadImage(AppManager.getAvatarUrl() + SpUtil.getCubeId(), ModifyAvatarActivity.this, mIvAvatar, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
    }

    /**
     * 从本地选择图片
     */
    public void selectImageFromLocal() {
        //权限
        RxPermissionUtil.requestStoragePermission(this).compose(RxSchedulers.<Boolean>io_main()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
                    ModifyAvatarActivity.this.startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
                }
                else {
                    ToastUtil.showToast(ModifyAvatarActivity.this, "请给予读取权限");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FROM_GALLERY && data.getData() != null) {
                Uri dataUri = data.getData();
                String dataPath = getDataColumn(this, dataUri, null, null);
                String path = BitmapUtils.compressImage(dataPath);
                mPresenter.modifyAvatar(path);
            }
        }
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void modifyAvatarSuccess(String url) {
        GlideUtil.loadImage(url, this, mIvAvatar, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
    }

    @Override
    public void onError(int code, String message) {
        ToastUtil.showToast(this, "修改头像失败");
    }
}
