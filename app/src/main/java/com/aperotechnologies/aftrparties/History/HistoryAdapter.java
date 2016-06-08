package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.R;

import java.util.List;

/**
 * Created by hasai on 03/05/16.
 */
public class HistoryAdapter extends BaseAdapter {

    private View.OnClickListener onclick;
    private List<PartiesClass> PartiesList;
    Context context;


    //    SQLiteDatabase sdb;
    public HistoryAdapter(Context context, List<PartiesClass> PartiesList)
    {
        this.context = context;
        this.PartiesList = PartiesList;

        onclick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onItemClick(v, (PartiesClass) v.getTag());
            }
        };

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
    public int getCount() {
        return PartiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return PartiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_history, parent, false);
            holder.partyName =  (TextView) convertView.findViewById(R.id.partyname);
            holder.partyStatus =  (TextView) convertView.findViewById(R.id.partystatus);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        PartiesClass Parties = new PartiesClass();
        Parties = (PartiesClass) PartiesList.get(position);
        Log.e("", " " + Parties.getPartyName());
        Log.e("", " " + Parties.getPartyStatus());

        holder.partyName.setText(Parties.getPartyName());
        holder.partyStatus.setText(Parties.getPartyStatus());

        convertView.setOnClickListener(onclick);
        convertView.setTag(Parties);
        return convertView;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    protected void onItemClick(View v, PartiesClass partyIdstatus) {
        Log.e("PartyId "," "+partyIdstatus.getPartyId());
        Log.e("PartyStatus "," "+partyIdstatus.getPartyStatus());
//        Intent i = new Intent(context,RequestantsList.class);
//        i.putExtra("PartyId",partyIdstatus.getPartyId());
//        context.startActivity(i);

    }

    static class ViewHolder
    {

        private TextView partyName;
        private TextView partyStatus;


    }
}
