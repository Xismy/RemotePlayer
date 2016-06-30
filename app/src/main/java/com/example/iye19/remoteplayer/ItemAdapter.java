package com.example.iye19.remoteplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by iye19 on 27/03/2016.
 * Utilizado como plantilla el código de:
 * http://www.androidinterview.com/android-custom-listview-with-image-and-text-using-arrayadapter/
 */
public class ItemAdapter extends ArrayAdapter{

    private final Activity context;
    private final String[] itemname;
    private final boolean[] isDir;

    public ItemAdapter(Activity context, String[] itemname, boolean[] isDir) {
        super(context, R.layout.file, itemname);
        this.context=context;
        this.itemname=itemname;
        this.isDir=isDir;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.file, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.file_name);
        if(isDir[position])
            ((ImageView)rowView.findViewById(R.id.file_image)).setImageResource(R.drawable.folder);

        txtTitle.setText(itemname[position]);
        return rowView;

    };
}