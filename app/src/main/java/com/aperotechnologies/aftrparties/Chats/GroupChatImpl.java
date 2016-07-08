package com.aperotechnologies.aftrparties.Chats;

import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

public class GroupChatImpl extends QBMessageListenerImpl<QBGroupChat> implements Chat {
    private static final String TAG = GroupChatImpl.class.getSimpleName();

    private ChatActivity chatActivity;

    private QBGroupChatManager groupChatManager;
    private QBGroupChat groupChat;
    Configuration_Parameter m_config;

    public GroupChatImpl(ChatActivity chatActivity) {
        m_config=Configuration_Parameter.getInstance();
        this.chatActivity = chatActivity;
    }

    public void joinGroupChat(QBDialog dialog, QBEntityCallback callback){
        initManagerIfNeed();

        if(groupChat == null) {
            groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
        }
        join(groupChat, callback);
    }

    private void initManagerIfNeed(){
        if(groupChatManager == null){

            groupChatManager = QBChatService.getInstance().getGroupChatManager();


        }
    }

    private void join(final QBGroupChat groupChat, final QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        Log.i("","Joining room...");

        groupChat.join(history, new QBEntityCallback() {
            @Override
            public void onSuccess(final Object o, final Bundle bundle) {
                groupChat.addMessageListener(GroupChatImpl.this);

                chatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //callback.onSuccess();
                        callback.onSuccess(o,bundle);
                        Log.i("","Join successful");

                    }
                });
                Log.w("Chat", "Join successful");
            }

            @Override
            public void onError(final QBResponseException e) {
                chatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(e);
                        e.printStackTrace();
                    }
                });


                Log.w("Couldnot joinchat, err:", e.toString());
            }
//
        });
    }

    public void leave(){
        try {
            groupChat.leave();
        } catch (SmackException.NotConnectedException nce) {
            nce.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() throws XMPPException {
        if (groupChat != null) {
            leave();

            groupChat.removeMessageListener(this);
        }
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        if (groupChat != null) {
            try {
                groupChat.sendMessage(message);
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
                Log.i("","Can't send a message, You are not connected to chat");
                //Toast.makeText(chatActivity, "Can't send a message, You are not connected to chat", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e){
                e.printStackTrace();
                Log.i("","You are still joining a group chat, please wait a bit");
                //Toast.makeText(chatActivity, "You are still joining a group chat, please wait a bit", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(chatActivity, "Join unsuccessful", Toast.LENGTH_LONG).show();
            Log.i("","Join unsuccessful");
        }
    }

    @Override
    public void processMessage(QBGroupChat groupChat, QBChatMessage chatMessage) {
        // Show message
        Log.w(TAG, "new incoming message: " + chatMessage);
        chatActivity.showMessage(chatMessage);
        // sets last message at the time of chat
        m_config.lastMessge = chatMessage.getBody();
    }

    @Override
    public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage){

    }
}
