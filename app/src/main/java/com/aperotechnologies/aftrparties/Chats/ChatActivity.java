package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatActivity extends Activity  {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private TextView groupName;
    private TextView noOfParticipants;
    private EditText messageEditText;
    private ListView messagesContainer;
    private Button sendButton;
    private ProgressBar progressBar;
    private ChatAdapter adapter;


    private Chat chat;
    private QBDialog dialog;
    //private KeyboardHandleRelativeLayout keyboardHandleLayout;
    private RelativeLayout container;
    Configuration_Parameter m_config;
    QBUser opponentUser;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        opponentUser = new QBUser();

        m_config=Configuration_Parameter.getInstance();

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        groupName = (TextView) findViewById(R.id.groupName);
        noOfParticipants = (TextView) findViewById(R.id.noOfParticipants);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView companionLabel = (TextView) findViewById(R.id.companionLabel);
        Button imgInfo = (Button) findViewById(R.id.imgInfo);
        Button imgCall = (Button) findViewById(R.id.imgCall);

        // Setup opponents info
        //
        Intent intent = getIntent();
        dialog = (QBDialog) intent.getSerializableExtra(ConstsCore.EXTRA_DIALOG);
        container = (RelativeLayout) findViewById(R.id.container);
        if (dialog.getType() == QBDialogType.GROUP) {
            TextView meLabel = (TextView) findViewById(R.id.meLabel);
            container.removeView(meLabel);
            container.removeView(companionLabel);
            imgInfo.setVisibility(View.VISIBLE);
            imgCall.setVisibility(View.GONE);
            groupName.setText(dialog.getName());
            noOfParticipants.setText(dialog.getOccupants().size()+" Participants");


        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
            opponentUser = ChatService.getInstance().getDialogsUsers().get(opponentID);
            //companionLabel.setText(ChatService.getInstance().getDialogsUsers().get(opponentID).getFullName());
            TextView meLabel = (TextView) findViewById(R.id.meLabel);
            container.removeView(meLabel);
            container.removeView(companionLabel);
            imgInfo.setVisibility(View.GONE);
            imgCall.setVisibility(View.VISIBLE);
            Log.e("--- ",""+opponentID);
            if(ChatService.getInstance().getDialogsUsers() == null){

            }else{
                groupName.setText(ChatService.getInstance().getDialogsUsers().get(opponentID).getFullName());

            }
            noOfParticipants.setText("");
        }

        // Send button
        //
        sendButton = (Button) findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString().replaceAll("\\s+", " ").trim();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                sendChatMessage(messageText);

            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstsCore.EXTRA_DIALOG, dialog);
                //GroupDialogDetailsActivity.start(ChatActivity.this, bundle);
                //Log.e("bundle "," "+bundle);
            }
        });

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callToUser(opponentUser, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
            }
        });

        initChat();
        ChatService.getInstance().addConnectionListener(chatConnectionListener);    }


//    private void callToUser(QBUser user, QBRTCTypes.QBConferenceType qbConferenceType) {
//
//        List<QBUser> qbUserList = new ArrayList<>();
//        //qbUserList.add(UserFriendUtils.createQbUser(user));
//        qbUserList.add(user);
//        //CallActivity.start(ChatActivity.this, qbUserList, qbConferenceType, null);
//    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        ChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {

            try {
                chat.release();
            } catch (XMPPException e) {
                Log.e(TAG, "failed to release chat", e);
            }
            super.onBackPressed();

            Intent i = new Intent(ChatActivity.this, DialogsActivity.class);
            startActivity(i);
            finish();

    }


    private void showKeyboard() {
        ((InputMethodManager) messageEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void sendChatMessage(String messageText) {
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty(ConstsCore.PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime() / 1000);

        try {
            chat.sendMessage(chatMessage);
            //sendPushNotifications(messageText);

        } catch (XMPPException e) {
            Log.e(TAG, "failed to send a message", e);
        } catch (SmackException sme) {
            Log.e(TAG, "failed to send a message", sme);
        }

        messageEditText.setText("");

        if (dialog.getType() == QBDialogType.PRIVATE) {
            showMessage(chatMessage);
        }
    }




    private void initChat() {

        if (dialog.getType() == QBDialogType.GROUP) {
            chat = new GroupChatImpl(this);//this variable will be use for sending message
            // Join group chat
            //
            progressBar.setVisibility(View.VISIBLE);
            joinGroupChat();

        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
            chat = new PrivateChatImpl(this, opponentID);//this variable will be use for sending message
            // Load Chat history
            //
            loadChatHistory();
        }
    }

    private void joinGroupChat() {
        ((GroupChatImpl) chat).joinGroupChat(dialog, new QBEntityCallback() {

            @Override
            public void onSuccess(Object o, Bundle bundle) {
                loadChatHistory();
                Log.e("Success "," "+o);
            }

            @Override
            public void onError(QBResponseException e) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("error when join group chat: " + e.toString()).create().show();
            }


        });
    }

    public void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                adapter = new ChatAdapter(ChatActivity.this, new ArrayList<QBChatMessage>());
                messagesContainer.setAdapter(adapter);
                Log.e("messages","size "+messages.size());

                for (int i = messages.size() - 1; i >= 0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
            }


        });
    }

    public void showMessage(QBChatMessage message) {
        adapter.add(message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                //scrollDown();
            }
        });
    }

    private void scrollDown() {
        Log.e("messagesContainer.getCount()"," "+(messagesContainer.getCount() - 1));
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {
            Log.i(TAG, "authenticated");
        }


        @Override
        public void connectionClosed() {
            Log.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

            // leave active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupChatImpl) chat).leave();
                    }
                });
            }
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if (seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful");

            // Join active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat();
                    }
                });
            }
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };


    //
    // ApplicationSessionStateCallback
    //

    /*@Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    initChat();
                }
            }
        });
    }*/

    public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
        Integer opponentID = -1;
        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(m_config.chatService.getUser().getId())){
                opponentID = userID;
                Log.e("opponentId ",""+opponentID);
                break;
            }
        }
        return opponentID;
    }


    public StringifyArrayList<Integer> getOccupantidforPushNotification(QBDialog dialog){
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(m_config.chatService.getUser().getId())){
                userIds.add(userID);
                Log.e("opponentId ",""+userIds);
                break;
            }
        }
        return userIds;
    }


    private void sendPushNotifications(String messageText) {

        StringifyArrayList<Integer> useroccupantIDs = getOccupantidforPushNotification(dialog);
        Log.e("useroccupantIDs"," "+useroccupantIDs);

        QBEvent event = new QBEvent();
        event.setUserIds(useroccupantIDs);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        HashMap<String, String> data = new HashMap<String, String>();
        if (dialog.getType() == QBDialogType.GROUP) {
            data.put("data.message", m_config.chatService.getUser().getFullName() + " @ "+dialog.getName() +" " + messageText);
        }else{
            data.put("data.message", m_config.chatService.getUser().getFullName() + " : " +messageText);
        }

        data.put("data.type", "unreadmsg");
        event.setMessage(String.valueOf(data));


        QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success");
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
            }
        });

    }




}

