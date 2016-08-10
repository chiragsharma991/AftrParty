package com.aperotechnologies.aftrparties.Settings;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.R;

/**
 * Created by mpatil on 05/07/16.
 */

public class SettingsRecyclerViewHolder extends RecyclerView.ViewHolder
{
    // View holder for gridview recycler view as we used in listview
    public ImageView close;
    public RelativeLayout relativeLayout;
    public ImageView imageview;

    public SettingsRecyclerViewHolder(View view)
    {
        super(view);
        // Find all views ids
        //this.relativeLayout = (RelativeLayout)view.findViewById(R.id.layout);
        this.imageview = (ImageView) view.findViewById(R.id.image);
    }
}