package com.common.utils.utils.glide;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.common.utils.R;

import java.io.File;

/**
 * glide工具类
 *
 * @author PengZhenjin
 * @date 2016/8/2
 */
public class GlideUtil {

    private static long mAvatarSignature = System.currentTimeMillis();//用于加载头像,改变此值loadSignatureCircleImage方法会强制去网络获取头像

    public static void setAvatarSignature(long signature) {
        mAvatarSignature = signature;
    }

    /**
     * 加载图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadVideo(Uri uri, Context context, ImageView imageView, int defResourceId) {
        Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    public static void loadVideo(Uri uri, Context context, ImageView imageView, int w, int h, int defResourceId) {
        try {
            Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).override(w, h).into(imageView);
        } catch (Exception e) {

        }
    }

    public static void loadVideo(String uri, Context context, ImageView imageView, int w, int h, int defResourceId) {
        try {
            Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).override(w, h).centerCrop().into(imageView);
        } catch (Exception e) {

        }
    }

    /**
     * 加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     * @param isGif         是否是gif图片
     */
    public static void loadImage(String url, Context context, ImageView imageView, int defResourceId, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
        else {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
    }

    /**
     * 加载二维码图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     * @param isGif         是否是gif图片
     */
    public static void loadQRImage(String url, Context context, ImageView imageView, int defResourceId, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_qr).into(imageView);
        }
        else {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defResourceId).error(R.drawable.ic_default_qr).into(imageView);
        }
    }

    /**
     * 加载图片
     *
     * @param resourceId
     * @param context
     * @param imageView
     * @param defResourceId
     * @param isGif         是否是gif图片
     */
    public static void loadImage(Integer resourceId, Context context, ImageView imageView, int defResourceId, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(resourceId).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
        else {
            Glide.with(context).load(resourceId).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
    }

    /**
     * 加载图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param defResourceId
     * @param isGif         是否是gif图片
     */
    public static void loadImage(Uri uri, Context context, ImageView imageView, int defResourceId, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
        else {
            Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
    }

    /**
     * 加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param isGif     是否是gif图片
     */
    public static void loadImage(String url, Context context, ImageView imageView, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_default_img_failed).into(imageView);
        }
        else {
            Glide.with(context).load(url).error(R.drawable.ic_default_img_failed).into(imageView);
        }
    }

    /**
     * 加载图片
     *
     * @param file
     * @param context
     * @param imageView
     * @param defResourceId
     * @param isGif         是否是gif图片
     */
    public static void loadImage(File file, Context context, ImageView imageView, int defResourceId, boolean isGif) {
        if (isGif) {
            Glide.with(context).load(file).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
        else {
            Glide.with(context).load(file).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
        }
    }

    public static void loadImage(File file, Context context, ImageView imageView) {
        Glide.with(context).load(file).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    public static void loadImage(File file, Context context, ImageView imageView, int defResourceId) {
        Glide.with(context).load(file).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    public static void loadImage(String url, Context context, int w, int h, ImageView imageView){
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(w, h).into(imageView);
    }

    /**
     * 是否禁止磁盘缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param type          缓存的类型
     *                      <li>磁盘缓存全部 DiskCacheStrategy.ALL</li>
     *                      <li>磁盘禁止缓存DiskCacheStrategy.NONE</li>
     * @param defResourceId
     */
    public static void loadImage(String url, Context context, ImageView imageView, DiskCacheStrategy type, int defResourceId) {
        Glide.with(context).load(url).diskCacheStrategy(type).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    /**
     * 是否禁止内存缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param skipMemoryCache 禁止内存缓存 true为禁止
     * @param defResourceId
     */
    public static void loadImage(String url, Context context, ImageView imageView, boolean skipMemoryCache, int defResourceId) {
        Glide.with(context).load(url).skipMemoryCache(skipMemoryCache).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    /**
     * 是否禁止内存/磁盘缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param type            缓存的类型
     *                        <li>磁盘缓存全部 DiskCacheStrategy.ALL</li>
     *                        <li>磁盘禁止缓存DiskCacheStrategy.NONE</li>
     * @param skipMemoryCache 禁止内存缓存 true为禁止
     * @param defResourceId
     */
    public static void loadImage(String url, Context context, ImageView imageView, DiskCacheStrategy type, boolean skipMemoryCache, int defResourceId) {
        Glide.with(context).load(url).skipMemoryCache(skipMemoryCache).diskCacheStrategy(type).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(String url, final Context context, final ImageView imageView, int defResourceId) {
        try {
            Glide.with(context).load(url).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载圆形图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(String url, final Context context, final ImageView imageView, int defResourceId, boolean isCache) {
        try {
            if(isCache){
                Glide.with(context).load(url).placeholder(defResourceId).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
            }
            else{
                Glide.with(context).load(url).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
            }
        } catch (Exception e) {}
    }

    /**
     * 加载圆形图片
     *
     * @param resourceId
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(Integer resourceId, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(resourceId).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载圆形图片,跳过缓存
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(String url, final Context context, final ImageView imageView, DiskCacheStrategy diskCacheStrategy,boolean skipMemoryCache,int defResourceId) {
        Glide.with(context).load(url).skipMemoryCache(skipMemoryCache).diskCacheStrategy(diskCacheStrategy).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载圆形图片,设置签名
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadSignatureCircleImage(String url,Context context,ImageView imageView,int defResourceId) {
        Glide.with(context).load(url).signature(new StringSignature(mAvatarSignature + "")).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(Uri uri, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(uri).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param file
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadCircleImage(File file, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(file).placeholder(defResourceId).error(defResourceId).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param roundRadius
     * @param defResourceId
     */
    public static void loadRoundImage(String url, final Context context, final ImageView imageView, int roundRadius, int defResourceId) {
        if (roundRadius < 0) {
            Glide.with(context).load(url).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context)).into(imageView);
        }
        else {
            Glide.with(context).load(url).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context, roundRadius)).into(imageView);
        }
    }

    /**
     * 加载圆角图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param roundRadius
     * @param defResourceId
     */
    public static void loadRoundImage(Uri uri, final Context context, final ImageView imageView, int roundRadius, int defResourceId) {
        if (roundRadius < 0) {
            Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context)).into(imageView);
        }
        else {
            Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context, roundRadius)).into(imageView);
        }
    }

    /**
     * 加载圆角图片
     *
     * @param file
     * @param context
     * @param imageView
     * @param roundRadius
     * @param defResourceId
     */
    public static void loadRoundImage(File file, final Context context, final ImageView imageView, int roundRadius, int defResourceId) {
        if (roundRadius < 0) {
            Glide.with(context).load(file).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context)).into(imageView);
        }
        else {
            Glide.with(context).load(file).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRoundTransform(context, roundRadius)).into(imageView);
        }
    }

    /**
     * 加载模糊图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadBlurImage(String url, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(url).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideBlurTransformation(context)).into(imageView);
    }

    /**
     * 加载模糊图片
     *
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadBlurImage(final Context context, final View imageView, int defResourceId) {
        Glide.with(context).load(defResourceId).bitmapTransform(new GlideBlurTransformation(context)).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                imageView.setBackground(resource);
            }
        });
    }

    /**
     * 加载模糊图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadBlurImage(Uri uri, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideBlurTransformation(context)).into(imageView);
    }

    /**
     * 加载模糊图片
     *
     * @param file
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadBlurImage(File file, final Context context, final ImageView imageView, int defResourceId) {
        Glide.with(context).load(file).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideBlurTransformation(context)).into(imageView);
    }

    /**
     * 加载旋转图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param rotateAngle
     * @param defResourceId
     */
    public static void loadRotateImage(String url, final Context context, final ImageView imageView, float rotateAngle, int defResourceId) {
        Glide.with(context).load(url).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRotateTransformation(context, rotateAngle)).into(imageView);
    }

    /**
     * 加载旋转图片
     *
     * @param uri
     * @param context
     * @param imageView
     * @param rotateAngle
     * @param defResourceId
     */
    public static void loadRotateImage(Uri uri, final Context context, final ImageView imageView, float rotateAngle, int defResourceId) {
        Glide.with(context).load(uri).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRotateTransformation(context, rotateAngle)).into(imageView);
    }

    /**
     * 加载旋转图片
     *
     * @param file
     * @param context
     * @param imageView
     * @param rotateAngle
     * @param defResourceId
     */
    public static void loadRotateImage(File file, final Context context, final ImageView imageView, float rotateAngle, int defResourceId) {
        Glide.with(context).load(file).placeholder(defResourceId).error(R.drawable.ic_default_img_failed).bitmapTransform(new GlideRotateTransformation(context, rotateAngle)).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param defResourceId
     */
    public static void loadHandImage(String url, Context context, ImageView imageView, int defResourceId) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defResourceId).error(defResourceId).into(imageView);
    }

    /**
     * 清除内存中的缓存 必须在UI线程中调用
     *
     * @param context
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清除磁盘中的缓存 必须在后台线程中调用，建议同时clearMemory()
     *
     * @param context
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
}
