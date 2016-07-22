package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.QBSessionClass;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
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


public class DialogsActivity extends Activity implements AbsListView.OnScrollListener {
    private static final String TAG = DialogsActivity.class.getSimpleName();
    Configuration_Parameter m_config;
    Context cont;
    private ListView listDialogs;
    private ProgressBar footerView;
    DialogsAdapter adapter;
    private ProgressBar pBar;
    TextView txtNoChat;
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


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;

        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        pBar.setVisibility(View.VISIBLE);
        txtNoChat = (TextView) findViewById(R.id.nochat);

        listDialogs = (ListView) findViewById(R.id.chatDialogslist);
        listadpDialogs = new ArrayList<>();
        dialogusers = new ArrayList<>();

        //add the footer before adding the adapter, else the footer will not load!
        footerView = (ProgressBar) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);

        if (ChatService.getInstance().getCurrentUser() == null)
        {
            String accessToken = LoginValidations.getFBAccessToken().getToken();

            QBSessionClass.getInstance().getQBSession(new QBEntityCallback() {

                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("inside handler","---");
                            getDialogs(index, count, "loading page");
                        }
                    });
                }

                @Override
                public void onError(QBResponseException e) {

                    pBar.setVisibility(View.GONE);
                    GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");

                }

            }, accessToken, pBar, cont);


        }
        else
        {

            getDialogs(index, count, "loading page");
        }


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
                //ChatActivity.start(DialogsActivity.this, bundle);

                Intent i = new Intent(DialogsActivity.this, ChatActivity.class);
                i.putExtras(bundle);
                i.putExtra("position",String.valueOf(position));
                startActivityForResult(i,1);

//                //if there are unread mesages, on click of dialog set unread message count to zero/blank
//                TextView txtUnreadMessage = (TextView) view.findViewById(R.id.textunreadmessage);
//                txtUnreadMessage.setText("");
//                selectedDialog.setUnreadMessageCount(0);
//                listadpDialogs.remove(position);
//                listadpDialogs.add(position, selectedDialog);


            }
        });


    }

    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    QBDialog selectedDialog = (QBDialog) data.getSerializableExtra(ConstsCore.EXTRA_DIALOG);
                    String newText = data.getStringExtra("lastmessage");
                    String position = data.getStringExtra("position");
                    Log.e("newText", " "+newText+" ");
                    if(!newText.equals(null) || newText != null)
                    {
                        if(newText.length() != 0) {
                            selectedDialog.setLastMessage(newText);
                            selectedDialog.setUnreadMessageCount(0);
                            listadpDialogs.set(Integer.parseInt(position), selectedDialog);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    // TODO Update your TextView.
                }
                break;
            }
        }
    }


    public void getDialogs(final int index, final int count, final String check) {
        Log.e("index count ",index+"    "+ count);

        QBRequestGetBuilder requestBuilder1 = new QBRequestGetBuilder();
        requestBuilder1.setSkip(index);
        requestBuilder1.setLimit(10);
        requestBuilder1.sortDesc("last_message_date_sent");

        QBChatService.getInstance().getChatDialogs(null, requestBuilder1, new QBEntityCallback<ArrayList<QBDialog>>()
        {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, final Bundle args)
            {

                Log.e("onSuccess","");

                //final int totalEntries = args.getInt("total_entries");
                if(dialogs.size() > 0)
                {
                    // collect all occupants ids
                    //
                    List<Integer> usersIDs = new ArrayList<Integer>();
                    for (QBDialog dialog : dialogs)
                    {

                        //Log.e(TAG, " " + dialog.getOccupants());
                        usersIDs.addAll(dialog.getOccupants());
                    }

                    // Get all occupants info
                    //
                    QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                    requestBuilder.setPage(1);
                    requestBuilder.setPerPage(usersIDs.size());
                    //
                    QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallback<ArrayList<QBUser>>()
                    {
                        @Override
                        public void onSuccess(ArrayList<QBUser> users, Bundle params)
                        {

                            // Save users
                            //
//                            Log.e(":dialogusers ", " " + dialogusers.size());
                            for(int i = 0; i < users.size(); i++)
                            {
                                if(!dialogusers.contains(users.get(i))){
                                    dialogusers.add(users.get(i));
                                }

                            }
                            ChatService.getInstance().setDialogsUsers(dialogusers);
                            Log.e(":dialogusers ", " " + dialogusers.size());
//

                            try
                            {


                                listDialogs.removeFooterView(footerView);

                                for(int i = 0; i < dialogs.size(); i++)
                                {
                                    QBDialog dialog = dialogs.get(i);
                                    Log.e("Name "," "+dialogs.get(i).getName());
                                    listadpDialogs.add(dialog);

                                }

                                if(check == "loading page")
                                {
                                    setDialogsListAdapter();
                                }
                                else
                                {
                                    adapter.notifyDataSetChanged();
                                }

                                if(listadpDialogs.size() < count)
                                {
                                    listDialogs.setOnScrollListener(null);
                                }
                                else
                                {

                                    listDialogs.addFooterView(footerView);
                                    listDialogs.setOnScrollListener(DialogsActivity.this);
                                }



                            }
                            catch (Exception e)
                            {
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
                else
                {

                    if(listadpDialogs.size() == 0)
                    {
                        pBar.setVisibility(View.GONE);
                        txtNoChat.setVisibility(View.VISIBLE);
                        txtNoChat.setText("No Chat Available");
                    }else{

                        listDialogs.removeFooterView(footerView);
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(cont,"There are no more dialogs",Toast.LENGTH_SHORT).show();
                        listDialogs.setOnScrollListener(null);
                    }

                    return;

                }

            }



            @Override
            public void onError(QBResponseException errors) {
                errors.printStackTrace();
            }
        });




    }

    private void setDialogsListAdapter() {
        adapter = new DialogsAdapter(DialogsActivity.this, listadpDialogs);
        listDialogs.setAdapter(adapter);
        pBar.setVisibility(View.GONE);


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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE)
        {
            index = index + 10;
            count = count + 10;
            getDialogs(index, count, "load more");

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}




