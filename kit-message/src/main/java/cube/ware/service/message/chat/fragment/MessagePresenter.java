package cube.ware.service.message.chat.fragment;

import android.content.Context;
import com.common.rx.RxSchedulers;
import cube.service.CubeEngine;
import cube.service.message.CustomMessage;
import cube.ware.core.CubeCore;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import cube.ware.service.message.manager.MessageBuilder;
import cube.ware.service.message.manager.ReceiptManager;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MessagePresenter extends MessageContract.Presenter {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public MessagePresenter(Context context, MessageContract.View view) {
        super(context, view);
    }

    @Override
    public void queryMessages(final CubeSessionType sessionType, final String chatId, int limit, long time, final boolean isSecret) {
        CubeMessageRepository.getInstance().queryMessage(chatId, time, limit).flatMap(new Func1<List<CubeMessage>, Observable<List<CubeMessageViewModel>>>() {
            @Override
            public Observable<List<CubeMessageViewModel>> call(List<CubeMessage> cubeMessages) {
                if (cubeMessages == null || cubeMessages.isEmpty()) {
                    if (isSecret) {
                        return getSecretTipMessage(chatId);
                    }
                    return Observable.empty();
                }

                cubeMessages.size();
                CubeMessage message = cubeMessages.get(0);
                //发送消息回执
                if (!message.isReceipt && message.isReceivedMessage()) {
                    //直接更新本地数据库消息回执状态，不等服务器的回执消息反馈
                    ReceiptManager.getInstance().onReceiptedAll(chatId, message.getTimestamp());
                    //发送回执消息
                    String sessionId = message.isGroupMessage() ? message.getGroupId() : message.getSenderId();
                    CubeEngine.getInstance().getMessageService().receiptMessages(sessionId, message.getTimestamp());
                }

                return Observable.from(cubeMessages).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
                    @Override
                    public Observable<CubeMessageViewModel> call(final CubeMessage cubeMessage) {
                        //自定义消息特殊处理
                        if (cubeMessage.getMessageType() == CubeMessageType.CustomTips) {
                            return buildCustom(cubeMessage);
                        }

                        if (sessionType == CubeSessionType.Group) {
                            return buildUserInfo(cubeMessage);
                        }
                        else {
                            return buildUserInfo(cubeMessage);
                        }
                    }
                }).toList();
            }
        }).compose(RxSchedulers.<List<CubeMessageViewModel>>io_main()).subscribe(new Action1<List<CubeMessageViewModel>>() {
            @Override
            public void call(List<CubeMessageViewModel> cubeMessageViewModels) {
                mView.queryMessagesSuccess(cubeMessageViewModels);
            }
        });
    }

    /**
     * 构建密聊提示消息
     *
     * @param chatId
     *
     * @return
     */
    private Observable<List<CubeMessageViewModel>> getSecretTipMessage(String chatId) {
        CustomMessage customMessage = MessageBuilder.buildCustomMessage(CubeSessionType.Secret, CubeCore.getInstance().getCubeId(), chatId, CubeCore.getContext().getResources().getString(R.string.secret_message_tip));
        customMessage.setReceived(true);
        customMessage.setReceipted(true);
        customMessage.setAnonymous(true);
        customMessage.setHeader("operate", "secret_tip");
        customMessage.setTimestamp(0);
        customMessage.setSendTimestamp(0);
        return CubeMessageRepository.getInstance().addMessage(customMessage).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
            @Override
            public Observable<CubeMessageViewModel> call(CubeMessage cubeMessage) {
                return buildCustom(cubeMessage);
            }
        }).toList();
    }

    /**
     * 构建自定义消息model
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessageViewModel> buildCustom(final CubeMessage cubeMessage) {
        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        viewModel.mMessage = cubeMessage;
        return Observable.just(viewModel);
    }

    /**
     * 构建用户信息model
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessageViewModel> buildUserInfo(final CubeMessage cubeMessage) {
        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        viewModel.userNme = cubeMessage.getSenderName();
        viewModel.userFace = CubeCore.getInstance().getAvatarUrl() + cubeMessage.getSenderId();
        viewModel.remark = cubeMessage.getSenderName();
        viewModel.mMessage = cubeMessage;
        return Observable.just(viewModel);
    }
}
