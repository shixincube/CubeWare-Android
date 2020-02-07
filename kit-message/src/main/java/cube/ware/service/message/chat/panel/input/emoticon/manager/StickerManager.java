package cube.ware.service.message.chat.panel.input.emoticon.manager;

import android.content.Context;
import android.util.Log;
import com.common.utils.utils.log.LogUtil;
import cube.ware.service.message.chat.panel.input.emoticon.model.StickerType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 贴图管理类
 */
public class StickerManager {
    private final String TAG = "StickerManager";

    private        Context        mContext;
    private static StickerManager mInstance;

    /**
     * 数据源
     */
    private List<StickerType>        mStickerCategories = new ArrayList<>();
    private Map<String, StickerType> mStickerTypeMap    = new HashMap<>();

    /**
     * ImageLoader
     */

    public static StickerManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StickerManager(context);
        }
        return mInstance;
    }

    private StickerManager(Context context) {
        this.mContext = context;
        //loadStickerType();
    }

    public void init() {
        Log.i(TAG, "Sticker Manager init...");
        loadStickerType();
    }

    private void loadStickerType() {
        //CubeEmojiRepository.getInstance().getCubeEmojiStructureLocal().compose(RxSchedulers.<List<CubeEmojiStructure>>io_main()).subscribe(new ApiSubscriber<List<CubeEmojiStructure>>(mContext) {
        //    @Override
        //    protected void _onNext(List<CubeEmojiStructure> cubeEmojiStructures) {
        //        setData(cubeEmojiStructures);
        //    }
        //    @Override
        //    protected void _onError(String message,int code) {
        //        LogUtil.e("请求失败：errorMessage:"+message+"&&&&&& errorCode:"+code);
        //    }
        //    @Override
        //    protected void _onCompleted() {}
        //});
    }

    public void clearData() {
        mStickerCategories.clear();
        mStickerTypeMap.clear();
    }

    public void refreshStickerType(final QueryLocalEmoji queryLocalEmoji) {

        //CubeEmojiRepository.getInstance().getCubeEmojiStructureLocal().compose(RxSchedulers.<List<CubeEmojiStructure>>io_main()).subscribe(new ApiSubscriber<List<CubeEmojiStructure>>(mContext) {
        //    @Override
        //    protected void _onNext(List<CubeEmojiStructure> cubeEmojiStructures) {
        //        clearData();
        //        setData(cubeEmojiStructures);
        //        if (queryLocalEmoji != null) {
        //            queryLocalEmoji.onSuccess();
        //        }
        //    }
        //
        //    @Override
        //    protected void _onError(String message, int code) {
        //        LogUtil.e("请求失败：errorMessage:"+message+"&&&&&& errorCode:"+code);
        //    }
        //
        //    @Override
        //    protected void _onCompleted() {}
        //});
    }

    //private void setData(List<CubeEmojiStructure> cubeEmojiStructures) {
    //    StickerType category;
    //    for (CubeEmojiStructure emojiStructure : cubeEmojiStructures) {
    //        category = new StickerType(emojiStructure);
    //        boolean hasCollect = false;
    //        for (int i = 0; i < mStickerCategories.size(); i++) {
    //            if (mStickerCategories.get(i).getType() == EmoticonConstant.EmoticonType.COLLECT.toValue()) {
    //                hasCollect = true;
    //                break;
    //            }
    //        }
    //        if (!hasCollect || category.getType() != EmoticonConstant.EmoticonType.COLLECT.toValue()) {
    //            mStickerCategories.add(category);
    //            mStickerTypeMap.put(emojiStructure.name, category);
    //        }
    //    }
    //}

    public interface QueryLocalEmoji {
        void onSuccess();
    }

    public synchronized List<StickerType> getCategories() {
        LogUtil.i("贴图包数量" + mStickerCategories.size());
        return mStickerCategories;
    }

    public synchronized StickerType getCategory(String name) {
        return mStickerTypeMap.get(name);
    }
}
