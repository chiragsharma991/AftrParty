package com.aperotechnologies.aftrparties.Chats;

import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;

/**
 * Created by hasai on 17/05/16.
 */
public class CreateChatDialog {
    Configuration_Parameter m_config;
    QBPrivateChatManager privateChatManager;

    public void createPrivateChat(int occupantId){//ArrayList<Integer> occupantId){

        m_config = Configuration_Parameter.getInstance();
        privateChatManager = m_config.chatService.getPrivateChatManager();

        //ArrayList opponentId = new ArrayList();
        //opponentId = occupantId;
//        Integer oppId = occupantId;//occupantId.get(0);
//
//        privateChatManager.createDialog(oppId, new QBEntityCallback<QBDialog>() {
//            @Override
//            public void onSuccess(QBDialog dialog, Bundle args) {
//                Log.e("onSuccess", " " + privateChatManager);
////                Bundle bundle = new Bundle();
////                bundle.putSerializable(ConstsCore.EXTRA_DIALOG, dialog);
////                Intent i = new Intent(context,ChatActivity.class);
////                i.putExtras(bundle);
////                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(i);
//
//
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//                Log.e("onError", " " + privateChatManager);
//            }
//
//
//
//
//        });
    }


}
