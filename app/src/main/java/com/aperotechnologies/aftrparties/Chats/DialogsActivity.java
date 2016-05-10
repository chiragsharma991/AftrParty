package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.R;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.List;

public class DialogsActivity extends Activity
{

    private static final String TAG = DialogsActivity.class.getSimpleName();
    private ListView dialogsListView;
    private ProgressBar progressBar;
    TextView txtNoChat;
    Configuration_Parameter m_config;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);
        //playServicesHelper = new PlayServicesHelper(this);
        m_config=Configuration_Parameter.getInstance();

        dialogsListView = (ListView) findViewById(R.id.roomsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtNoChat = (TextView)findViewById(R.id.nochat);

        // Get dialogs
        getDialogs();

    }

    private void getDialogs(){
        progressBar.setVisibility(View.VISIBLE);


//         QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
//                    requestBuilder.sortAsc("last_message_date_sent");
//                    QBChatService.getChatDialogs(null, requestBuilder, new QBEntityCallback<ArrayList<QBDialog>>()
//                    {
//                        @Override
//                        public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args)
//                        {
//                            progressBar.setVisibility(View.GONE);
//                            Log.e("dialogs "," "+dialogs);
//
//                            buildListView(dialogs);
//                        }
//                        @Override
//                        public void onError(QBResponseException e)
//                        {
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });


        // Get dialogs
        //
        ChatService.getInstance().getDialogs(new QBEntityCallback() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {


                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>)object;
                Log.e("dialogs "," "+dialogs.size());
                // build list view
                //
                if(dialogs.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    txtNoChat.setVisibility(View.VISIBLE);
                    txtNoChat.setText("No Chat Available");
                    return;
                }else{
                    txtNoChat.setVisibility(View.GONE);
                }

                buildListView(dialogs);
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                txtNoChat.setVisibility(View.VISIBLE);
                txtNoChat.setText("Some Error Occured");


            }


        });

    }


    void buildListView(List<QBDialog> dialogs)
    {
        final DialogsAdapter adapter = new DialogsAdapter(dialogs, DialogsActivity.this);
        dialogsListView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        // choose dialog
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(m_config.notificationManager != null){
                    m_config.notificationManager.cancelAll();

                }

                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstsCore.EXTRA_DIALOG, selectedDialog);
                // Open chat activity
                //
                ChatActivity.start(DialogsActivity.this, bundle);
                finish();
            }
        });
    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // playServicesHelper.checkPlayServices();
    }



}
