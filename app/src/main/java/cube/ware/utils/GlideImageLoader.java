package cube.ware.utils;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

import cube.ware.R;

/**
 * Created by dth
 * Des: 为ImagePicker 提供图片加载器
 * Date: 2018/9/5.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {

        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_img_failed)
                .override(width, height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_img_failed)
                .override(width, height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
