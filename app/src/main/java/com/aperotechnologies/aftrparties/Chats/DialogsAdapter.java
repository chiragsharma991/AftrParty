package com.aperotechnologies.aftrparties.Chats;

/**
 * Created by igorkhomenko on 9/12/14.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.R;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DialogsAdapter extends BaseAdapter {
    private List<QBDialog> dataSource;
    private LayoutInflater inflater;
    Context cont;

    public DialogsAdapter(List<QBDialog> dataSource, Activity ctx) {
        this.dataSource = dataSource;
        this.inflater = LayoutInflater.from(ctx);
        cont = ctx;
    }

    public List<QBDialog> getDataSource() {
        return dataSource;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // initIfNeed view
        //
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_room, null);
            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.roomImage);
            holder.name = (TextView)convertView.findViewById(R.id.roomName);
            holder.lastMessage = (TextView)convertView.findViewById(R.id.lastMessage);
            holder.groupType = (TextView)convertView.findViewById(R.id.textViewGroupType);
            holder.unreadmessages = (TextView)convertView.findViewById(R.id.textunreadmessage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set data
        //
        QBDialog dialog = dataSource.get(position);
        String url = dialog.getPhoto();
        Log.e("url "," "+url);


//        if(!url.equals("") || !url.equals(null)) {
//            Picasso.with(cont).load(url).fit().centerCrop()
//                    .placeholder(R.drawable.placeholder_user)
//                    .error(R.drawable.placeholder_user)
//                    .into(holder.image);
//        }


        if(dialog.getType().equals(QBDialogType.GROUP)){
            holder.name.setText(dialog.getName());
        }else{

            holder.name.setText(dialog.getName());

            // get opponent name for private dialog
            //
            /*Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
            QBUser user = ChatService.getInstance().getDialogsUsers().get(opponentID);
            if(user != null){
                holder.name.setText(user.getLogin() == null ? user.getFullName() : user.getLogin());
            }*/
        }
//        if (dialog.getLastMessage() != null && StickersManager.isSticker(dialog.getLastMessage())) {
//            holder.lastMessage.setText("Sticker");
//        } else {
//            holder.lastMessage.setText(dialog.getLastMessage());
//        }
        holder.lastMessage.setText(dialog.getLastMessage());
        holder.groupType.setText(dialog.getType().toString());
        holder.unreadmessages.setText(String.valueOf(dialog.getUnreadMessageCount()));

        return convertView;
    }

    private static class ViewHolder{
        ImageView image;
        TextView name;
        TextView lastMessage;
        TextView groupType;
        TextView unreadmessages;
    }
}
