package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class DialogsActivityNew extends Activity
{
    private static final String TAG = DialogsActivityNew.class.getSimpleName();
    private ListView listDialogs;
    private ProgressBar footerView;
    private ProgressBar pBar;
    TextView txtNoChat;
    Configuration_Parameter m_config;
    DialogsAdapter adapter;
    Context cont;
    private int index = 0;
    private int count = 10;
    ArrayList<QBDialog> listadpDialogs;
    ArrayList<QBUser> dialogusers;

    @Override
    public void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);
        index = 0;
        count = 10;
        listadpDialogs = new ArrayList<>();
        dialogusers = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;


        listDialogs = (ListView) findViewById(R.id.chatDialogslist);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        Log.e("-progressBar---"," "+pBar);
        pBar.setVisibility(View.VISIBLE);
        txtNoChat = (TextView) findViewById(R.id.nochat);



        footerView = (ProgressBar) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        listDialogs.addFooterView(footerView);

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
                //ChatActivity.start(DialogsActivityNew.this, bundle);

                //if there are unread mesages, on click of dialog set unread message count to zero/blank
                TextView txtUnreadMessage = (TextView) view.findViewById(R.id.textunreadmessage);
                txtUnreadMessage.setText("");
                selectedDialog.setUnreadMessageCount(0);
                listadpDialogs.remove(position);
                listadpDialogs.add(position, selectedDialog);




            }
        });

        QBRequestGetBuilder requestBuilder1 = new QBRequestGetBuilder();
        requestBuilder1.setSkip(index);
        requestBuilder1.setLimit(count);
        requestBuilder1.sortDesc("last_message_date_sent");

        QBChatService.getInstance().getChatDialogs(null, requestBuilder1, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, final Bundle args) {

                Log.e("onSuccess",index+" "+ count);

                if(dialogs.size() == 0)
                {
                    pBar.setVisibility(View.GONE);
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

                        for(int i = 0; i < users.size(); i++){
                            dialogusers.add(users.get(i));
                        }

                        ChatService.getInstance().setDialogsUsers(dialogusers);

                        try {

                            int maxLimit = count;
                            if (maxLimit > dialogs.size())
                            {
                                maxLimit = dialogs.size();
                                listDialogs.removeFooterView(footerView);
                            }
                            else
                            {
                                listDialogs.removeFooterView(footerView);
                                listDialogs.addFooterView(footerView);
                            }

                            for(int i = index; i < maxLimit; i++)
                            {
                                QBDialog dialog = dialogs.get(i);
                                Log.e("Name "," "+dialogs.get(i).getName());
                                listadpDialogs.add(dialog);

                            }

                            setDialogsListAdapter();

                            if(listadpDialogs.size() < count){
                                listDialogs.setOnScrollListener(null);
                            }else{
                                index = index + 10;
                                count = count + 10;
                                listDialogs.setOnScrollListener(onScrollListener());
                            }




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
    }





    private void getDialogs(final int index, final int count) {
        Log.e("index count ",index+"    "+ count);


        QBRequestGetBuilder requestBuilder1 = new QBRequestGetBuilder();
        requestBuilder1.setSkip(index);
        requestBuilder1.setLimit(count);
        requestBuilder1.sortDesc("last_message_date_sent");

        QBChatService.getInstance().getChatDialogs(null, requestBuilder1, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, final Bundle args) {

                Log.e("onSuccess",dialogs.size()+"----");

                //final int totalEntries = args.getInt("total_entries");
                if(dialogs.size() == 0){
                    listDialogs.removeFooterView(footerView);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(cont,"There are no more dialogs",Toast.LENGTH_SHORT).show();
                    listDialogs.setOnScrollListener(null);
                    return;

                }

                // collect all occupants ids
                //
                List<Integer> usersIDs = new ArrayList<Integer>();
                for (QBDialog dialog : dialogs) {

                    //Log.e(TAG, " " + dialog.getOccupants());
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
                        for(int i = 0; i < users.size(); i++){
                            dialogusers.add(users.get(i));
                        }
                        ChatService.getInstance().setDialogsUsers(dialogusers);

                        try {

                            int maxLimit = dialogs.size();
                            if (maxLimit > dialogs.size())
                            {
                                maxLimit = dialogs.size();
                                listDialogs.removeFooterView(footerView);
                            }
                            else
                            {
                                listDialogs.removeFooterView(footerView);
                                listDialogs.addFooterView(footerView);
                            }



                            for(int i = 0; i < maxLimit; i++)
                            {
                                QBDialog dialog = dialogs.get(i);
                                Log.e("Name "," "+dialogs.get(i).getName());
                                listadpDialogs.add(dialog);

                            }

                            adapter.notifyDataSetChanged();



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

    private void setDialogsListAdapter() {
        adapter = new DialogsAdapter(DialogsActivityNew.this, listadpDialogs);
        listDialogs.setAdapter(adapter);
        pBar.setVisibility(View.GONE);


    }

    private AbsListView.OnScrollListener onScrollListener() {


        return new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {



                if (scrollState == SCROLL_STATE_IDLE)
                {
//
                    getDialogs(index, count);
                    index = index + 10;
                    count = count + 10;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount)
            {

            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }

        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




