package cube.ware.service.message.preview.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.common.base.BaseLazyFragment;
import com.common.utils.BitmapUtils;
import com.common.utils.ThreadUtil;
import com.common.utils.ToastUtil;
import com.common.utils.log.LogUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cube.ware.service.message.R;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.CubeDBFactory;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.utils.SpUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class PreviewImageFragment extends BaseLazyFragment {

    private static final String TAG = PreviewImageFragment.class.getSimpleName();
    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;
    private ImageButton imageView;
    private long mMessageSn;

    public static PreviewImageFragment newInstance(long messageSn) {
        PreviewImageFragment fragment = new PreviewImageFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("message_sn", messageSn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageSn = getArguments().getLong("message_sn");
        Log.d(TAG, "onCreate message SN:" + mMessageSn);
    }

    @Override
    protected void onFirstUserVisible() {
        loadData();
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_image_preview, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mPhotoView = (PhotoView) view.findViewById(R.id.photoView);
        imageView = (ImageButton) view.findViewById(R.id.dot_imageview);

        this.mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                getActivity().finish();
            }
        });

    }

    private void loadData() {
        CubeMessageRepository.getInstance().queryMessageBySn(mMessageSn)
                .doOnNext(new Action1<CubeMessage>() {
                    @Override
                    public void call(CubeMessage cubeMessage) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CubeMessage>() {
                    @Override
                    public void call(CubeMessage cubeMessage) {
                        if (null != cubeMessage && cubeMessage.getMessageType() == CubeMessageType.Image) {
                            LogUtil.i("预览图片url ------> " + cubeMessage.getFileUrl()+" "+cubeMessage.getMessageSN());
//                            GlideUtil.loadImage(cubeMessage.getFileUrl(), getContext(), mPhotoView, false);
                            String imagePath;
                            if (!TextUtils.isEmpty(cubeMessage.getFilePath()) && new File(cubeMessage.getFilePath()).exists()) { //加载本地文件
                                imagePath = cubeMessage.getFilePath();
                            } else {
                                imagePath = cubeMessage.getFileUrl();
                                saveImage(cubeMessage, imagePath);
                            }

                            Glide.with(getContext()).load(imagePath).into(new GlideDrawableImageViewTarget(mPhotoView) {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                    super.onResourceReady(resource, animation);
                                    mProgressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    ToastUtil.showToast( 0, "图片加载失败");
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(throwable);
                        mProgressBar.setVisibility(View.GONE);
                        ToastUtil.showToast( 0, "图片加载失败");
                    }
                });
    }

    private void saveImage(final CubeMessage cubeMessage, final String imagePath) {
        ThreadUtil.request(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap mBitmap = Glide.with(getContext()).load(imagePath)
                            .asBitmap()
                            .centerCrop()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    String saveBitmapPath = BitmapUtils.saveBitmap(mBitmap, SpUtil.getImagePath(), cubeMessage.getFileName());
                    if (saveBitmapPath != null) {
                        cubeMessage.setFilePath(saveBitmapPath);
                        CubeDBFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
                        MessageManager.getInstance().messagePersisted(cubeMessage);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
