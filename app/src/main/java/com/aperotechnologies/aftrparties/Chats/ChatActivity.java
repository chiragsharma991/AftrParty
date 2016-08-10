package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBPushNotifications;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class ChatActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    Configuration_Parameter m_config;
    SharedPreferences sharedpreferences;
    private Chat chat;
    private QBDialog dialog;
    private TextView groupName;
    private ProgressBar progressBar;
    //private ListView messagesContainer;
    private EditText edt_message;
    private Button sendButton;
    private ChatAdapter adapter;
    //QBUser opponentUser;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ArrayList<QBChatMessage> listadptchatMessages;
    private int index = 0;
    private int selCount = 0;
    String lastMessage = "";
    private int totalMessagesCount = 0;
    int unreadmsgcount = 0;


//    public static void start(Context context, Bundle bundle) {
//        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtras(bundle);
//        context.startActivity(intent);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        index = 0;
        totalMessagesCount = 0;

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // Setup opponents info
        Intent intent = getIntent();
        dialog = (QBDialog) intent.getSerializableExtra(ConstsCore.EXTRA_DIALOG);
        m_config.lastMessge = dialog.getLastMessage();
        Log.e("ChatActivity onCreate  m_config.lastMessge"," "+ m_config.lastMessge);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) findViewById(R.id.messagesContainer);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);

        listadptchatMessages = new ArrayList<>();
        adapter = new ChatAdapter(ChatActivity.this, listadptchatMessages, dialog.getType());
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        groupName = (TextView) findViewById(R.id.groupName);
        edt_message = (EditText) findViewById(R.id.messageEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        unreadmsgcount = dialog.getUnreadMessageCount();
        Log.e("here ",""+unreadmsgcount);


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
        sendButton.setVisibility(View.INVISIBLE);
        edt_message.setVisibility(View.INVISIBLE);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = edt_message.getText().toString().replaceAll("\\s+", " ").trim();
                if (TextUtils.isEmpty(messageText))
                {
                    return;
                }
                sendChatMessage(messageText);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });


        initChat();
        ChatService.getInstance().addConnectionListener(chatConnectionListener);
    }


    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        index = index + 20;
        loadChatHistory(index, "pulling page");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {

        QBDialog selectedDialog = (QBDialog) getIntent().getSerializableExtra(ConstsCore.EXTRA_DIALOG);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstsCore.EXTRA_DIALOG, selectedDialog);
        // Open chat activity
        //ChatActivity.start(DialogsActivity.this, bundle);

        Log.e("onBacKpressed m_config.lastMessge"," "+m_config.lastMessge);

        Intent resultIntent = new Intent();
        resultIntent.putExtras(bundle);
        if(m_config.lastMessge == null || m_config.lastMessge.equals(null)){
            resultIntent.putExtra("lastmessage", "");
        }else{
            resultIntent.putExtra("lastmessage", m_config.lastMessge);
        }

        resultIntent.putExtra("position", getIntent().getExtras().getString("position"));
        resultIntent.putExtra("unreadmsg", String.valueOf(unreadmsgcount));

        setResult(Activity.RESULT_OK, resultIntent);
            try {
                chat.release();
            } catch (XMPPException e) {
                Log.e(TAG, "failed to release chat", e);
            }
            super.onBackPressed();




    }


    private void showKeyboard() {
        ((InputMethodManager) edt_message.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(edt_message, InputMethodManager.SHOW_IMPLICIT);
    }


    //function for sending chat message
    private void sendChatMessage(String messageText) {

        QBUser currentUser = ChatService.getInstance().getCurrentUser();

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty(ConstsCore.PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setProperty("dialog_name",currentUser.getFullName());
        chatMessage.setDialogId(dialog.getDialogId());


        try {
            chat.sendMessage(chatMessage);
            m_config.lastMessge = edt_message.getText().toString().replaceAll("\\s+", " ").trim();

            //Notification for 1-1 Chat msg
//            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
//
//            if (dialog.getType() == QBDialogType.PRIVATE) {
//                QBPushNotifications.sendPrivateChatmessagePN(ChatActivity.this, opponentID, currentUser.getFullName(), dialog.getDialogId(), edt_message.getText().toString().replaceAll("\\s+", " ").trim());
//            }


        } catch (XMPPException e) {
            Log.e(TAG, "failed to send a message", e);
            GenerikFunctions.showToast(ChatActivity.this,"Failed to send a message");

        } catch (SmackException sme) {
            Log.e(TAG, "failed to send a message", sme);
            GenerikFunctions.showToast(ChatActivity.this,"Can't send a message, You are not connected to chat");

        }

        edt_message.setText("");

        if (dialog.getType() == QBDialogType.PRIVATE) {

            showMessage(chatMessage);
        }
    }




    private void initChat() {


        if (dialog.getType() == QBDialogType.GROUP) {
            chat = new GroupChatImpl(this);//this variable will be use for sending message

            // Join group chat
            joinGroupChat();

        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            //swipeRefreshLayout.setRefreshing(true);

            Integer opponentID = getOpponentIDForPrivateDialog(dialog);
            chat = new PrivateChatImpl(this, opponentID);//this variable will be use for sending message
            // Load Chat history
            loadChatHistory(index, "loading page");
        }
    }

    private void joinGroupChat() {
        ((GroupChatImpl) chat).joinGroupChat(dialog, new QBEntityCallback() {

            @Override
            public void onSuccess(Object o, Bundle bundle) {
                loadChatHistory(index, "loading page");
                Log.e("Success "," joinGroupChat");
            }

            @Override
            public void onError(QBResponseException e) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("error:  No response received within reply timeout.Please try after some time." + e.toString()).create().show();
                progressBar.setVisibility(View.GONE);
            }


        });
    }

    public void loadChatHistory(final int index, final String check)
    {
        //swipeRefreshLayout.setRefreshing(true);
        Log.e("index "," count "+index);

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setSkip(index);
        customObjectRequestBuilder.setLimit(20);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>()
        {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args)
            {

                //m_config.peerchatDialogId = dialog.getDialogId();


                totalMessagesCount = totalMessagesCount + messages.size();
                Log.e("messages","size "+messages.size());
                selCount += messages.size();
                int selVal = selCount - index;
                Log.e("selVal"," "+selVal);
                unreadmsgcount = 0;

                if(messages.size() > 0)
                {



                    for (int i = 0; i < messages.size(); i++)
                    {

                        QBChatMessage msg = messages.get(i);
                        listadptchatMessages.add(0, msg);


                    }
                    adapter.notifyDataSetChanged();
                    m_config.lastMessge = listadptchatMessages.get(totalMessagesCount - 1).getBody();
                    Log.e("m_config.lastMessge"," "+m_config.lastMessge);
                    sendButton.setVisibility(View.VISIBLE);
                    edt_message.setVisibility(View.VISIBLE);


                    if(check == "loading page")
                    {
                        scrollDown();

                    }
                    else
                    {

                        listView.setSelection(selVal - 1);
                    }

                }else{
                    if(totalMessagesCount == 0)
                    {
                        m_config.lastMessge = "";
                        Log.e("totalMessagesCount=0 m_config.lastMessge"," "+m_config.lastMessge);
                    }
                    sendButton.setVisibility(View.VISIBLE);
                    edt_message.setVisibility(View.VISIBLE);

                }
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("error: " + e.toString()).create().show();
                progressBar.setVisibility(View.GONE);
            }


        });
    }

    public void showMessage(QBChatMessage message)
    {
        if(adapter != null)
        {
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

    private void scrollDown()
    {
       // Log.e("messagesContainer.getCount()"," "+(listView.getCount() - 1));
        listView.setSelection(listView.getCount() - 1);
    }



    ConnectionListener chatConnectionListener = new ConnectionListener()
    {
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
        public void connectionClosedOnError(final Exception e)
        {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

            // leave active room
            //
            if (dialog.getType() == QBDialogType.GROUP)
            {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupChatImpl) chat).leave();
                    }
                });
            }
        }

        @Override
        public void reconnectingIn(final int seconds)
        {
            if (seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful()
        {
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
        public void reconnectionFailed(final Exception error)
        {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };



    public Integer getOpponentIDForPrivateDialog(QBDialog dialog)
    {
        Integer opponentID = -1;
        //Log.e("----"," "+dialog.getOccupants());

        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(Integer.valueOf(sharedpreferences.getString(m_config.QuickBloxID,"")))){
                opponentID = userID;
                //Log.e("opponentId ",""+opponentID);
                break;
            }
        }
        return opponentID;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }



}

