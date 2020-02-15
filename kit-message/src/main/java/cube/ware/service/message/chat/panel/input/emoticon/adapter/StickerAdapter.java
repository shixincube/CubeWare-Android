package cube.ware.service.message.chat.panel.input.emoticon.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.common.utils.glide.GlideUtil;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.panel.input.emoticon.model.EmoticonType;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonView;
import cube.ware.service.message.chat.panel.input.emoticon.model.StickerItem;
import cube.ware.service.message.chat.panel.input.emoticon.model.StickerType;
import java.io.File;

/**
 * 每页显示的贴图表情
 *
 * @author Wangxx
 * @date 2017/1/4
 */
public class StickerAdapter extends BaseAdapter {

    private Context     context;
    private StickerType category;
    private int         startIndex;

    public StickerAdapter(Context mContext, StickerType category, int startIndex) {
        this.context = mContext;
        this.category = category;
        this.startIndex = startIndex;
    }

    public int getCount() {//获取每一页的数量
        int count = category.getStickerList().size() - startIndex;
        count = Math.min(count, EmoticonView.CHARTLET_PER_PAGE);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return category.getStickerList().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChartletViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.cube_sticker_picker_view, null);
            viewHolder = new ChartletViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.chartlet_thumb_image);
            viewHolder.descLabel = (TextView) convertView.findViewById(R.id.chartlet_desc_label);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ChartletViewHolder) convertView.getTag();
        }
        int index = startIndex + position;
        if (index >= category.getStickerList().size()) {
            return convertView;
        }
        StickerItem chartlet = category.getStickerList().get(index);
        if (chartlet == null) {
            return convertView;
        }
        if (category.getType() == EmoticonType.COLLECT.value) {
            viewHolder.descLabel.setVisibility(View.GONE);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (position == 0 && index == 0) {
                viewHolder.imageView.setBackgroundResource(R.drawable.img_collection_increase_default);
            }
            else {
                GlideUtil.loadImage(new File(chartlet.getPath() + ".png"), context, viewHolder.imageView, R.drawable.ic_default_img_failed);
            }
        }
        else {
            viewHolder.descLabel.setVisibility(View.VISIBLE);
            viewHolder.descLabel.setText(chartlet.getName());
            GlideUtil.loadImage(new File(chartlet.getPath() + ".png"), context, viewHolder.imageView);
        }
        return convertView;
    }

    class ChartletViewHolder {
        public ImageView imageView;
        public TextView  descLabel;
    }
}