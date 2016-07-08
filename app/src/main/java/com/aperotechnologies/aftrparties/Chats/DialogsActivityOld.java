package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;


public class DialogsActivityOld extends Activity
{
    private static final String TAG = DialogsActivity.class.getSimpleName();
    private ListView listDialogs;
    private ProgressBar footerView;
    private ProgressBar progressBar;
    TextView txtNoChat;
    Configuration_Parameter m_config;
    DialogsAdapter adapter;
    Context cont = this;
    private int index = 0;
    private int count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);
        index = 0;
        count = 10;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        m_config = Configuration_Parameter.getInstance();

        listDialogs = (ListView) findViewById(R.id.chatDialogslist);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtNoChat = (TextView) findViewById(R.id.nochat);
        progressBar.setVisibility(View.VISIBLE);

        footerView = (ProgressBar) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        listDialogs.addFooterView(footerView);

        getDialogs();

        listDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(m_config.notificationManager != null)
                {
                    m_config.notificationManager.cancelAll();
                }

                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstsCore.EXTRA_DIALOG, selectedDialog);


                // Open chat activity
                //ChatActivity.start(DialogsActivityOld.this, bundle);
                //  finish();
            }
        });
    }


    private void getDialogs() {
        Log.e("InSide getDialogs", count + " aa");

        QBRequestGetBuilder requestBuilder1 = new QBRequestGetBuilder();
        requestBuilder1.setSkip(index);
        requestBuilder1.setLimit(count);
        requestBuilder1.sortAsc("last_message_date_sent");

        QBChatService.getInstance().getChatDialogs(null, requestBuilder1, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, final Bundle args) {
                final int totalEntries = args.getInt("total_entries");


                if(dialogs.size() == 0)
                {
                    progressBar.setVisibility(View.GONE);
                    txtNoChat.setVisibility(View.VISIBLE);
                    txtNoChat.setText("No Chat Available");
                    return;
                }
                else
                {
                    txtNoChat.setVisibility(View.GONE);
                }


                // collect all occupants ids
                //
                List<Integer> usersIDs = new ArrayList<Integer>();
                for (QBDialog dialog : dialogs) {

                    Log.e(TAG, " " + dialog.getOccupants());
                    usersIDs.addAll(dialog.getOccupants());
                }

                // Get all occupants info
                //
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(usersIDs.size());
                //
                QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                        // Save users
                        //
                        Log.e(":ChatService users", " " + users);
                        ChatService.getInstance().setDialogsUsers(users);

                        try {
                            Log.e("Total Entries", "" + totalEntries + "   " + args.size() + "");

                            adapter = new DialogsAdapter(DialogsActivityOld.this, dialogs);
                            listDialogs.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);

                        } catch (Exception e) {
                            Log.e("Outer Exception e", "Yes");
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        e.printStackTrace();
                    }


                });
            }

            @Override
            public void onError(QBResponseException errors) {
                errors.printStackTrace();
            }
        });

//


    }

}




