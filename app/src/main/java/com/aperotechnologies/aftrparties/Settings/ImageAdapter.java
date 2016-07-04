package com.aperotechnologies.aftrparties.Settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mpatil on 29/06/16.
 */
public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<String> Images;

    // Constructor
    public ImageAdapter(Context c, ArrayList<String> url)
    {
        mContext = c;
        Images = url;
    }

    public int getCount() {
        return Images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null)
        {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }

        Bitmap bitmap = null;
        try
        {
            URL url1 = new URL(Images.get(position));
            Log.e("url1"," "+url1);
            bitmap = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

//        Bitmap finalbitmap = null;
//        finalbitmap = Bitmap.createScaledBitmap(bitmap,500,500,true);
        imageView.setImageBitmap(bitmap);

        return imageView;
    }


}
