package com.aperotechnologies.aftrparties.Chats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;

import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mpatil on 23/06/16.
 */


/**
 * Created by mpatil on 17/07/15.
 */

//Line List Grid View Adapter . User a bridge between gridview and data to be displayed
// Also click event on shortlist button is handled here
public class DialogsAdapter extends BaseAdapter
{

    private Context cont;
    ArrayList<QBDialog> dialogs;

    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config=Configuration_Parameter.getInstance();
    String url = null;


    public DialogsAdapter(Context cont, ArrayList<QBDialog> dialogs)
    {
        super();
        this.dialogs = dialogs;
        this.cont = cont;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        Log.i("Inside Adapter","Yes");


    }


    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return dialogs.size();//dialognames.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dialogs.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    public static class ViewHolder
    {
        TextView roomName, lastMessage, textunreadmessage;
        CircularImageView grpChatImage;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub

        View participentView = convertView;
        final  ViewHolder view;

        if (participentView == null)
        {
            view = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(cont.LAYOUT_INFLATER_SERVICE);
            participentView = inflater.inflate(R.layout.dapter_list_diagone, null);
            view.roomName = (TextView) participentView.findViewById(R.id.roomName);
            view.lastMessage = (TextView) participentView.findViewById(R.id.lastMessage);
            view.textunreadmessage = (TextView) participentView.findViewById(R.id.textunreadmessage);
            view.grpChatImage = (CircularImageView) participentView.findViewById(R.id.grpChatImage);

            participentView.setTag(view);
        }
        else
        {
            view = (ViewHolder) participentView.getTag();
        }


        QBDialog dialog = dialogs.get(position);


        if(dialog.getType().equals(QBDialogType.GROUP))
        {

            url = dialog.getPhoto();
            Log.e("came in group", "");
            if (url == null || url.equals("") || url.length() == 0)
            {
                view.grpChatImage.setImageResource(R.drawable.placeholder_group);
                view.grpChatImage.setTag(position);
            }
            else
            {

                Picasso.with(cont)
                        .load(url)
                        .fit()
                        .into(view.grpChatImage);
                view.grpChatImage.setTag(position);
            }
        }
        else if(dialog.getType().equals(QBDialogType.PRIVATE))
        {
            Integer opponentID = getPrivateChatoppId(dialogs.get(position));
            QBUser user = ChatService.getInstance().getDialogsUsers().get(opponentID);
            url = user.getCustomData();
            Log.e("came in private","");
            if(url == null || url.equals("") || url.length() == 0)
            {
                view.grpChatImage.setImageResource(R.drawable.placeholder_user);
                view.grpChatImage.setTag(position);
            }
            else
            {
                Picasso.with(cont)
                        .load(url)
                        .fit()
                        .into(view.grpChatImage);
                view.grpChatImage.setTag(position);
            }

        }

        view.roomName.setText(dialog.getName());

        if(dialog.getLastMessage() != null)
        {
            String lastmsg = "";
            if(dialog.getLastMessage().contains("&euro;") || dialog.getLastMessage().contains("&amp;") || dialog.getLastMessage().contains("&gt;") || dialog.getLastMessage().contains("&lt;") || dialog.getLastMessage().contains("&quot;") || dialog.getLastMessage().contains("&apos;"))
            {
                lastmsg = Validations.escapeXml(dialog.getLastMessage());
                view.lastMessage.setText(lastmsg);
            }
            else
            {
                lastmsg = dialog.getLastMessage();
                view.lastMessage.setText(lastmsg);
            }



        }
        else
        {
            view.lastMessage.setText("");
        }

        if(dialog.getUnreadMessageCount() != 0)
        {
            view.textunreadmessage.setText(String.valueOf(dialog.getUnreadMessageCount()));
        }
        else {
            view.textunreadmessage.setText("");
        }




        return participentView;
    }


    public Integer getPrivateChatoppId(QBDialog dialog){

        Integer opponentID = -1;
        //Log.e("----"," "+dialog.getOccupants());

        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(Integer.valueOf(sharedPreferences.getString(m_config.QuickBloxID,"")))){
                opponentID = userID;
                //Log.e("opponentId ",""+opponentID);
                break;
            }
        }

        return opponentID;


    }
}


