package cube.ware.ui.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.mvp.rx.RxPermissionUtil;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.BitmapUtils;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import cube.service.CubeEngine;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.CubeAvator;
import cube.ware.eventbus.Event;
import cube.ware.eventbus.MessageEvent;
import cube.ware.ui.mine.dialog.BottomChooseDialog;
import cube.ware.utils.SpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

@Route(path = AppConstants.Router.ChangeAvatorActivity)
public class ChangeAvatorActivity extends AppCompatActivity {

    private ImageView mIvAvator;
    private int REQUEST_CODE_FROM_GALLERY=1001;
    private ImageView mIvBack;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avator);
        initView();
        initListener();
    }

    private void initListener() {
        mIvAvator.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomChooseDialog instance = BottomChooseDialog.getInstance();
                instance.show(getSupportFragmentManager(),"");
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

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mIvAvator = findViewById(R.id.iv_avator);
        mProgressBar = findViewById(R.id.progressbar);
        GlideUtil.loadImage(AppConstants.AVATAR_URL+SpUtil.getCubeId(),ChangeAvatorActivity.this,mIvAvator, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);
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
                    ChangeAvatorActivity.this.startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
                } else {
                    ToastUtil.showToast(ChangeAvatorActivity.this,"请给予读取权限");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_CODE_FROM_GALLERY && data.getData()!=null) {
                Uri dataUri = data.getData();
                String dataPath = getDataColumn(this, dataUri, null, null);
                String path = BitmapUtils.compressImage(dataPath);
                mProgressBar.setVisibility(View.VISIBLE);
                changeAvatar(path);
            }
        }
    }

    /**
     * 修改头像
     * @param dataPath
     */
    private void changeAvatar(String dataPath) {
        ApiFactory.getInstance().uploadAvatar(SpUtil.getCubeToken(), new File(dataPath), new Callback<ResultData<CubeAvator>>() {
            @Override
            public void onResponse(Call<ResultData<CubeAvator>> call, Response<ResultData<CubeAvator>> response) {
                if(response.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    if(response.body().data!=null){
                        LogUtil.i("===更换头像uploadAvatar:"+response.body().data.getUrl());
                        User user = new User();
                        user.cubeId = SpUtil.getCubeId();
                        user.displayName = SpUtil.getUserName();
                        user.avatar = response.body().data.getUrl();
                        CubeEngine.getInstance().getUserService().update(user);
                        SpUtil.setUserAvator(response.body().data.getUrl());
                        GlideUtil.loadImage(response.body().data.getUrl(),ChangeAvatorActivity.this,mIvAvator, DiskCacheStrategy.NONE,true, R.drawable.default_head_user);
                        EventBus.getDefault().post(new MessageEvent<>(Event.EVENT_REFRESH_CUBE_USER, user));
                    }else {
                        ToastUtil.showToast(ChangeAvatorActivity.this,response.body().state.desc);
                    }
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    try {
                        ToastUtil.showToast(ChangeAvatorActivity.this,"修改头像失败");
                        LogUtil.i("uploadAvatar:"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultData<CubeAvator>> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                LogUtil.i("uploadAvatar error:"+t.getMessage().toString());
            }
        });
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(projection[0]); path = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                if (cursor != null) {
                    cursor.close();
                }
            } return path;
    }
}
