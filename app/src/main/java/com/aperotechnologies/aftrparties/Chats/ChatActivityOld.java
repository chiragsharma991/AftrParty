package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class ChatActivityOld extends Activity {

    private static final String TAG = ChatActivityOld.class.getSimpleName();
    Configuration_Parameter m_config;
    SharedPreferences sharedpreferences;
    private Chat chat;
    private QBDialog dialog;
    private TextView groupName;
    private ProgressBar progressBar;
    private ListView messagesContainer;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter adapter;
    Context cont;
    //QBUser opponentUser;


    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivityOld.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //opponentUser = new QBUser();

        m_config=Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        groupName = (TextView) findViewById(R.id.groupName);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cont = this;

        // Setup opponents info
        Intent intent = getIntent();
        dialog = (QBDialog) intent.getSerializableExtra(ConstsCore.EXTRA_DIALOG);

        if (dialog.getType() == QBDialogType.GROUP)
        {
            groupName.setText(dialog.getName());

        }
        else if (dialog.getType() == QBDialogType.PRIVATE)
        {
            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
            //opponentUser = ChatService.getInstance().getDialogsUsers().get(opponentID);
            if(ChatService.getInstance().getDialogsUsers() == null)
            {
            }
            else
            {
                groupName.setText(ChatService.getInstance().getDialogsUsers().get(opponentID).getFullName());

            }
        }

        // Send button
        sendButton = (Button) findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString().replaceAll("\\s+", " ").trim();
                if (TextUtils.isEmpty(messageText))
                {
                    return;
                }
                sendChatMessage(messageText);

            }
        });


        initChat();
        ChatService.getInstance().addConnectionListener(chatConnectionListener);
    }


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


    }


    private void showKeyboard() {
        ((InputMethodManager) messageEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
    }


    //function for sending chat message
    private void sendChatMessage(String messageText) {
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty(ConstsCore.PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime() / 1000);

        try {
            chat.sendMessage(chatMessage);

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
            chat = new GroupChatImpl((ChatActivity) cont);//this variable will be use for sending message
            progressBar.setVisibility(View.VISIBLE);
            // Join group chat
            //
            joinGroupChat();

        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
            chat = new PrivateChatImpl((ChatActivity)cont, opponentID);//this variable will be use for sending message
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
                Log.e("Success "," joinGroupChat");
            }

            @Override
            public void onError(QBResponseException e) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivityOld.this);
                dialog.setMessage("error when join group chat: " + e.toString()).create().show();
                progressBar.setVisibility(View.GONE);
            }


        });
    }

    public void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setSkip(0);
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                adapter = new ChatAdapter(ChatActivityOld.this, new ArrayList<QBChatMessage>(), dialog.getType());
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
        if(adapter != null){
            adapter.add(message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    scrollDown();
                }
            });
        }

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
                ChatActivityOld.this.runOnUiThread(new Runnable() {
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
                ChatActivityOld.this.runOnUiThread(new Runnable() {
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



    public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
        Integer opponentID = -1;
        //Log.e("----"," "+dialog.getOccupants());

        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(Integer.valueOf(sharedpreferences.getString(m_config.QuickBloxID,"")))){
                opponentID = userID;
                Log.e("opponentId ",""+opponentID);
                break;
            }
        }
        return opponentID;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }



}

