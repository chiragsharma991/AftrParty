package com.aperotechnologies.aftrparties.Chats;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.utils.DateUtils;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import java.util.List;

//import com.quickblox.sample.chat.utils.TimeUtils;

//import vc908.stickerfactory.StickersManager;

public class ChatAdapter extends BaseAdapter {

    private Activity context;
    private final List<QBChatMessage> chatMessages;
    private QBDialogType dialog_type;
    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;

    private enum ChatItemType {
        Message,
        Sticker
    }

    public ChatAdapter(Activity context, List<QBChatMessage> chatMessages, QBDialogType dialog_type) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.dialog_type = dialog_type;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public QBChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ChatItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return ChatItemType.Message.ordinal();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final QBChatMessage chatMessage = getItem(position);
        Log.e("chatMessage" ," "+chatMessage);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (getItemViewType(position) == ChatItemType.Sticker.ordinal()) {
                //convertView = vi.inflate(R.layout.list_item_sticker, parent, false);
            } else {
                convertView = vi.inflate(R.layout.list_item_message, parent, false);
            }
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        QBUser currentUser = ChatService.getInstance().getCurrentUser();
        boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());
        setAlignment(holder, isOutgoing);
        if (holder.txtMessage != null) {
            holder.txtMessage.setText(chatMessage.getBody());
        }

        if (chatMessage.getSenderId() != null)
        {

            if(dialog_type == QBDialogType.GROUP)
            {

                if(chatMessage.getSenderId().equals(currentUser.getId()))
                {
                    holder.txtInfo.setText(DateUtils.longToMessageDate(chatMessage.getDateSent()));

                }
                else
                {

                    Log.e("----"," "+chatMessage.getSenderId()+"------"+ChatService.getInstance().getDialogsUsers().get(chatMessage.getSenderId()));
                    holder.txtInfo.setText(ChatService.getInstance().getDialogsUsers().get(chatMessage.getSenderId()).getFullName() + ": " + DateUtils.longToMessageDate(chatMessage.getDateSent()));
                }
            }
            else
            {
                holder.txtInfo.setText(DateUtils.longToMessageDate(chatMessage.getDateSent()));
            }
        }
        else
        {
            holder.txtInfo.setText(DateUtils.longToMessageDate(chatMessage.getDateSent()));
        }

        return convertView;
    }

    public void add(QBChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<QBChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (!isOutgoing) {


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.incoming_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        } else {


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.outgoing_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        //holder.stickerView = (ImageView) v.findViewById(R.id.sticker_image);
        return holder;
    }



    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView stickerView;
    }
}
