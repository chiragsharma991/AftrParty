package com.aperotechnologies.aftrparties.GateCrasher;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.model.FBFriendsList;
import com.aperotechnologies.aftrparties.model.FriendsSelectedList;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 27/08/16.
 */
public class FBFriendsListAdapter extends BaseAdapter {

    List fbFriendsSelected;
    List<FBFriendsList> friendsList;
    Context cont;
    String hostfbid;
    List FbFriendsId;

    public FBFriendsListAdapter(Context cont, List<FBFriendsList> friendsList, String hostfbid) {
        this.cont = cont;
        this. friendsList = friendsList;
        this.hostfbid = hostfbid;
        fbFriendsSelected = new ArrayList();
        FbFriendsId = new ArrayList();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount()
    {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return friendsList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View participentView = convertView;
        final  ViewHolder view;

        if (participentView == null)
        {
            view = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(cont.LAYOUT_INFLATER_SERVICE);
            participentView = inflater.inflate(R.layout.friends_row, null);
            view.txtfbFriendname = (TextView) participentView.findViewById(R.id.rowTextView);
            view.chckBox = (CheckBox) participentView.findViewById(R.id.CheckBox01);

            participentView.setTag(view);
        }
        else
        {
            view = (ViewHolder) participentView.getTag();
        }


        view.txtfbFriendname.setText(friendsList.get(position).getName());
        view.chckBox.setChecked(false);
        view.txtfbFriendname.setTag(position);


        participentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("view "," "+v);
                CheckBox cb = (CheckBox) v.findViewById(R.id.CheckBox01);
                TextView txt = (TextView) v.findViewById(R.id.rowTextView);
                Log.e("position "," =--- "+txt.getTag());
                if(!cb.isChecked() && fbFriendsSelected.size() < 6)
                {
                    if(hostfbid.equals(friendsList.get((Integer) txt.getTag()).getId()))
                    {
                        GenerikFunctions.showToast(cont, txt.getText() +" is host of this party.");

                    }
                    else
                    {
                        cb.setChecked(true);
                        FriendsSelectedList selList = new FriendsSelectedList(friendsList.get((Integer) txt.getTag()).getId(), txt.getText().toString(),"friend", true);
                        Log.e("selList"," "+selList);
                        fbFriendsSelected.add(selList);
                        FbFriendsId.add(friendsList.get((Integer) txt.getTag()).getId());

                    }

                }
                else
                {
                    cb.setChecked(false);
                    FriendsSelectedList selList = new FriendsSelectedList(friendsList.get((Integer) txt.getTag()).getId(), txt.getText().toString(),"friend", false);
                    Log.e("selList"," "+selList);
                    if(FbFriendsId.contains(friendsList.get((Integer) txt.getTag()).getId()))
                    {
                        int val = FbFriendsId.indexOf(friendsList.get((Integer) txt.getTag()).getId());
                        fbFriendsSelected.remove(val);
                        FbFriendsId.remove(val);

                    }

                }

            }
        });

        return participentView;
    }


    public static class ViewHolder
    {
        TextView txtfbFriendname;
        CheckBox chckBox;


    }
}
