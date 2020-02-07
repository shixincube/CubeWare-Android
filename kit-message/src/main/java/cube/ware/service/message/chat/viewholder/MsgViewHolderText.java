package cube.ware.service.message.chat.viewholder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.ScreenUtil;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.adapter.ChatMessageAdapter;
import cube.ware.service.message.manager.MessagePopupManager;
import cube.ware.utils.ClipBoardUtil;
import cube.ware.widget.CubeEmoticonTextView;
import cube.ware.widget.bottomPopupDialog.BottomPopupDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天消息文本模块
 *
 * @author Wangxx
 * @date 2017/1/9
 */

public class MsgViewHolderText extends BaseMsgViewHolder {

    // 正则表达式：匹配手机号
    private static final String REGEX_MOBILE = "(13\\d|14[57]|15[^4,\\D]|17[13678]|18\\d)\\d{8}|170[0589]\\d{7}";

    // 正则表达式：匹配邮箱
    private static final String REGEX_EMAIL = "[a-zA-Z0-9_-]+@\\w+\\.[a-z]+(\\.[a-z]+)?";

    private static final String REGEX_URL = "(http://|ftp://|https://|www){0,1}[-A-Za-z0-9_.+]+?\\.(com|net|cn|me|tw|fr|info|xyz)[-A-Za-z0-9+&?!@#/%=~_.|]*";

    protected CubeEmoticonTextView mContentTv;

    public MsgViewHolderText(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_text;
    }

    @Override
    protected void initView() {
        this.mContentTv = findViewById(R.id.chat_message_item_text_body);
    }

    @Override
    protected void bindView() {
        boolean isMy;

        if (isReceivedMessage()) {
            isMy = false;
            this.mContentTv.setBackgroundResource(R.drawable.selector_chat_receive_bg);
            this.mContentTv.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(8), ScreenUtil.dip2px(12), ScreenUtil.dip2px(8));
        }
        else {
            isMy = true;
            this.mContentTv.setBackgroundResource(R.drawable.selector_chat_send_bg);
            this.mContentTv.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(8), ScreenUtil.dip2px(12), ScreenUtil.dip2px(8));
        }

        this.mContentTv.setMovementMethod(LinkMovementClickMethod.getInstance());
        this.mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(mContentTv);
            }
        });
        if (isShowSecretMessage()) {
            this.mContentTv.setTextColor(mContext.getResources().getColor(R.color.grey_500));
            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_chat_secret_text_message);
            // 这一步必须要做,否则不会显示.
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            this.mContentTv.setCompoundDrawables(null, null, d, null);
            this.mContentTv.setCompoundDrawablePadding(ScreenUtil.dip2px(2));
            this.mContentTv.setText(mContext.getResources().getString(R.string.secret_text_message), TextView.BufferType.SPANNABLE, isMy);
        }
        else {
            this.mContentTv.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
            this.mContentTv.setText(getDisplayText(), TextView.BufferType.SPANNABLE, isMy);
            checkMobileOrEmail(new SpannableString(mContentTv.getText()));
        }
        if (!mData.mMessage.isAnonymous()) {
            //长安弹出菜单
            MessagePopupManager.showMessagePopup(this, mContentTv, this);
        }
    }

    /**
     * 检查手机号或者邮箱
     *
     * @param sp
     */
    private void checkMobileOrEmail(SpannableString sp) {
        Pattern pattern1 = Pattern.compile(REGEX_MOBILE);
        Matcher matcher1 = pattern1.matcher(sp);
        while (matcher1.find()) {
            String mobile = matcher1.group();
            int start = matcher1.start();
            int end = matcher1.end();
            sp.setSpan(new MobileSpan(mobile), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        Pattern pattern3 = Pattern.compile(REGEX_EMAIL);
        Matcher matcher3 = pattern3.matcher(sp);
        while (matcher3.find()) {
            String email = matcher3.group();
            int start = matcher3.start();
            int end = matcher3.end();
            sp.setSpan(new EmailSpan(email), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        Pattern pattern4 = Pattern.compile(REGEX_URL);
        Matcher matcher4 = pattern4.matcher(sp);
        while (matcher4.find()) {
            String url = matcher4.group();
            int start = matcher4.start();
            int end = matcher4.end();
            UrlSpan urlSpan = new UrlSpan(url);
            while (sp.charAt(start) == '.' && start < sp.length() - 1) {
                start++;
            }
            while (sp.charAt((end - 1)) == '.' && end > start) {
                end--;
            }
            CharSequence subSequence = sp.subSequence(start, end);
            if (!subSequence.toString().contains(".")) {
                start = 0;
                end = 0;
            }
            sp.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        mContentTv.setText(sp);
    }

    private class MobileSpan extends ClickableSpan {
        private String mMobile;

        MobileSpan(String mobile) {
            this.mMobile = mobile;
        }

        @Override
        public void onClick(View widget) {
            List<String> bottomDialogContents = new ArrayList<>();
            bottomDialogContents.add(mContext.getString(R.string.call));
            bottomDialogContents.add(mContext.getString(R.string.copy));
            final BottomPopupDialog bottomPopupDialog = new BottomPopupDialog(mContext, bottomDialogContents);
            bottomPopupDialog.setTitleText(mContext.getString(R.string.x_phone_tips, mMobile));
            bottomPopupDialog.showCancelBtn(true);
            bottomPopupDialog.show();
            bottomPopupDialog.setCancelable(true);
            bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    bottomPopupDialog.dismiss();
                    if (position == 0) {    // 呼叫
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mMobile));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                    if (position == 1) {    // 复制
                        ClipBoardUtil.copyText(mContext, mMobile);
                    }
                }
            });
            bottomPopupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private class UrlSpan extends ClickableSpan {
        private String mUrl;

        UrlSpan(String url) {
            this.mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (!mUrl.contains("://")) {
                mUrl = "http://" + mUrl;
            }
            // TODO: 2018/9/3 跳转web
            //            WebActivity.start(mContext, "", mUrl);
        }
    }

    private class EmailSpan extends ClickableSpan {
        private String mEmail;

        EmailSpan(String email) {
            this.mEmail = email;
        }

        @Override
        public void onClick(View widget) {
            List<String> bottomDialogContents = new ArrayList<>();
            //            bottomDialogContents.add("发送邮件");
            bottomDialogContents.add(mContext.getString(R.string.copy));
            final BottomPopupDialog bottomPopupDialog = new BottomPopupDialog(mContext, bottomDialogContents);
            bottomPopupDialog.setTitleText(mContext.getString(R.string.x_mail_tips, mEmail));
            bottomPopupDialog.showCancelBtn(true);
            bottomPopupDialog.show();
            bottomPopupDialog.setCancelable(true);
            bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    bottomPopupDialog.dismiss();
                    if (position == 0) {  // 发送邮件
                        //                        CubeUI.getInstance().getCubeDataProvider().starWriteMail(mContext, mEmail);
                        ClipBoardUtil.copyText(mContext, mEmail);
                    }
                    else if (position == 1) {  // 复制
                        //                        ClipBoardUtil.copyText(mContext, mEmail);
                    }
                }
            });
            bottomPopupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private String getDisplayText() {
        return super.mData.mMessage.getContent();
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    protected void onItemClick(View view) {
        if (isShowSecretMessage()) {
            this.mContentTv.setCompoundDrawables(null, null, null, null);
            this.mContentTv.setText(getDisplayText(), TextView.BufferType.NORMAL, false);
            this.mContentTv.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
            int time;

            if (getDisplayText().length() <= 60) {
                time = 30;
                this.startSecretTime(time);
                Log.d("MsgViewHolderText", "getDisplayText().length():" + getDisplayText().length());
            }
            else {
                time = Math.round(getDisplayText().length() / 2.0f);  //当文字长度大于60时间取长度的一半。4舍5入
                this.startSecretTime(time);
                Log.d("MsgViewHolderText", "getDisplayText().length():" + getDisplayText().length());
            }

            this.receipt();
            view.setEnabled(false);
        }
    }

    @Override
    protected boolean onItemLongClick() {
        return super.onItemLongClick();
    }
}
