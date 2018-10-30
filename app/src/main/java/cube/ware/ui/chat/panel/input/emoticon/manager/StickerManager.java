package cube.ware.ui.chat.panel.input.emoticon.manager;

import android.content.Context;
import android.util.Log;

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
//    private List<StickerType>        mStickerCategories = new ArrayList<>();
//    private Map<String, StickerType> mStickerTypeMap    = new HashMap<>();

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
        this.loadStickerType();
    }

    public void init() {
        Log.i(TAG, "Sticker Manager init...");
    }

    private void loadStickerType() {
        // TODO: 2018/9/3 去数据库查询收藏的表情
//        CubeEmojiRepository.getInstance().getCubeEmojiStructureLocal().compose(RxSchedulers.<List<CubeEmojiStructure>>io_main()).subscribe(new ApiSubscriber<List<CubeEmojiStructure>>(mContext) {
//            @Override
//            protected void _onNext(List<CubeEmojiStructure> cubeEmojiStructures) {
//                StickerType category;
//                for(CubeEmojiStructure emojiStructure : cubeEmojiStructures){
//                    category = new StickerType(emojiStructure);
//                    mStickerCategories.add(category);
//                    mStickerTypeMap.put(emojiStructure.name, category);
//                }
//            }
//            @Override
//            protected void _onError(String message,int code) {
//
//            }
//            @Override
//            protected void _onCompleted() {}
//        });
    }

    public void refreshStickerType(final QueryLocalEmoji queryLocalEmoji){
//        mStickerCategories.clear();
//        mStickerTypeMap.clear();
//        CubeEmojiRepository.getInstance().getCubeEmojiStructureLocal().compose(RxSchedulers.<List<CubeEmojiStructure>>io_main()).subscribe(new ApiSubscriber<List<CubeEmojiStructure>>(mContext) {
//            @Override
//            protected void _onNext(List<CubeEmojiStructure> cubeEmojiStructures) {
//                StickerType category;
//                for(CubeEmojiStructure emojiStructure : cubeEmojiStructures){
//                    category = new StickerType(emojiStructure);
//                    mStickerCategories.add(category);
//                    mStickerTypeMap.put(emojiStructure.name, category);
//                }
//                if(queryLocalEmoji != null){
//                    queryLocalEmoji.onSuccess();
//                }
//            }
//            @Override
//            protected void _onError(String message,int code) {
//
//            }
//            @Override
//            protected void _onCompleted() {}
//        });
    }

    public interface QueryLocalEmoji{
        void onSuccess();
    }

//    public synchronized List<StickerType> getCategories() {
//        LogUtil.i("贴图包数量" + mStickerCategories.size());
//        return mStickerCategories;
//    }
//
//    public synchronized StickerType getCategory(String name) {
//        return mStickerTypeMap.get(name);
//    }
}
