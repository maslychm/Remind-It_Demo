package com.maslychm.remind_it_demo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class EventListAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "EventListAdapter";

    private Context mContext;
    int mResource;
    private ArrayList<Event> events;

    public EventListAdapter(Context context, int resource, ArrayList<Event> objects) {
        super(context, resource, objects);
        //Log.i("Objects: ",objects.toString());
        mContext = context;
        mResource = resource;
        this.events = objects;
    }

    public void swapItems(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name =  getItem(position).getName();
        String description = getItem(position).getDescription();
        String dueDate = getItem(position).getDueDate().toString();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.reminderName);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.reminderDescription);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.reminderDueDate);

        tvName.setText(name);
        tvDescription.setText(description);
        tvDueDate.setText(dueDate);

        int id = 0;
        if (getItem(position).isMustBeNear())
            id = mContext.getResources().getIdentifier("drawable/" + "notif", null, mContext.getPackageName());
        else id = mContext.getResources().getIdentifier("drawable/" + "clock", null, mContext.getPackageName());

        ((ImageView) convertView.findViewById(R.id.image)).setImageResource(id);

        return convertView;
    }
}
