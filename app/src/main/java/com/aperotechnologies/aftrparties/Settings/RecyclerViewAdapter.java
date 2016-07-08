package com.aperotechnologies.aftrparties.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.aperotechnologies.aftrparties.Reusables.Validations.getOutputMediaFileUri;

/**
 * Created by mpatil on 05/07/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewHolder>
{// Recyclerview will extend to
    // recyclerview adapter
    private ArrayList<String> arrayList;
    private Context context;
    ImageView img;
    Configuration_Parameter m_config;
ImageView primary;
    public RecyclerViewAdapter(Context context, ArrayList<String> arrayList,ImageView primaryImage)
    {
        Log.e("Inside recycler adapter","Yes  "+arrayList.size()+"");
        this.context = context;
        this.arrayList = arrayList;
        img = primaryImage;
        m_config = Configuration_Parameter.getInstance();
        Activity act = (Activity)context;
        primary = (ImageView)act.findViewById(R.id.userimage);
    }

    @Override
    public int getItemCount()
    {
        return (null != arrayList ? arrayList.size() : 0);
    }



    @Override
    public void onBindViewHolder(SettingsRecyclerViewHolder holder, final int position)
    {
        final String model = arrayList.get(position);

        SettingsRecyclerViewHolder mainHolder = (SettingsRecyclerViewHolder) holder;// holder

        // setting image

        Picasso.with(context).load(model).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(mainHolder.imageview);


        mainHolder.imageview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              //  Toast.makeText(context,"Image at position "+position+" clicked",Toast.LENGTH_LONG).show();


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context, android.R.layout.select_dialog_item);
                arrayAdapter.add("Make Primary");
                arrayAdapter.add("Remove");

                builderSingle.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which==0)
                                {
                                    m_config.PrimaryUrl = arrayList.get(position);
                                    Picasso.with(context).load(arrayList.get(position)).fit().centerCrop()
                                            .placeholder(R.drawable.placeholder_user)
                                            .error(R.drawable.placeholder_user)
                                            .into(img);
                                    img.setTag(""+position);
                                }
                                else
                                {
                                    //Delete Image
                                    Log.e("Initialsizes ","aa   " +SettingsActivity.validPics.size() +"   " +arrayList.size());


                                    if(arrayList.size()==1)
                                    {
                                        Toast.makeText(context,"Deletion is not allowed on single image",Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        if(arrayList.get(position).contains("cloudinary"))
                                        {
                                            Log.e("Image Element",arrayList.get(position));
                                            //Call function for cloudinary delete
                                            String result = arrayList.get(position).substring(arrayList.get(position).lastIndexOf("/") + 1);
                                            System.out.println("Image name " + result);
                                            Validations.deleteImagefromCloudinary(result);
                                        }
                                        else
                                        {
                                            Log.e("Image Element",position+"");
                                        }
                                        Log.e("Image Element",position+"");
                                        Log.e("ImageView tag",img.getTag()+"   aa");

                                        if(img.getTag().equals(position+""))
                                        {
                                            Log.e("Equals both","Yes");
                                            if((position+1) == arrayList.size())
                                            {
                                                Log.e("Inside if", "yess 11");
                                                Picasso.with(context).load(arrayList.get(0)).fit().centerCrop()
                                                        .placeholder(R.drawable.placeholder_user)
                                                        .error(R.drawable.placeholder_user)
                                                        .into(img);
                                                img.setTag("0");
                                                m_config.PrimaryUrl = arrayList.get(0);
                                            }
                                            else
                                            {
                                                Log.e("Inside else", "yess 11");
                                                Log.e("arrayList.get(position+1) " + (position+1),arrayList.get(position+1)+"");
                                                Picasso.with(context).load(arrayList.get(position+1)).fit().centerCrop()
                                                        .placeholder(R.drawable.placeholder_user)
                                                        .error(R.drawable.placeholder_user)
                                                        .into(img);
                                                img.setTag((position+1)+"");
                                                m_config.PrimaryUrl = arrayList.get(position+1);
                                            }
                                        }
                                        Log.e("Position to be removed is ","aa   "+position);
                                        arrayList.remove(position);





                                        Log.e("Validpics.size","aa   " +SettingsActivity.validPics.size() +"   " +arrayList.size());
                                        Log.e("New Arraylist size",arrayList.size()+"");
                                        notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                builderSingle.show();
            }
        });
    }

    @Override
    public SettingsRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.settings_images_xml, viewGroup, false);
        SettingsRecyclerViewHolder listHolder = new SettingsRecyclerViewHolder(mainGroup);
        return listHolder;

    }
}