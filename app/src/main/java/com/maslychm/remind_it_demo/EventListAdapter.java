package com.maslychm.remind_it_demo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

        // Get the passed element (Event event)
        String name =  getItem(position).getName();
        String description = getItem(position).getDescription();
        LocalDateTime dueDateTime = LocalDateTime.ofInstant(getItem(position).getDueDate(), ZoneOffset.UTC);
        LocalDateTime curDateTime = LocalDateTime.now(ZoneOffset.systemDefault());

        String dueDate = "" + dueDateTime.getMonth().toString()
                + " " + ((Integer)dueDateTime.getDayOfMonth()).toString()
                + " at " + ((Integer)dueDateTime.getHour()).toString()
                + ":" + ((Integer)dueDateTime.getMinute()).toString();

        if (((Integer)dueDateTime.getMinute()).toString().startsWith("0")) {
            dueDate += "0";
        }

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

        // this is today
        if (dueDateTime.toLocalDate().equals(curDateTime.toLocalDate())) {
            convertView.setBackgroundColor(Color.LTGRAY);
        // overdue
        } else if (curDateTime.toLocalDate().isAfter(dueDateTime.toLocalDate())) {
            convertView.setBackgroundColor(Color.HSVToColor(new float[] { 19, 98, 84 }));
        // plenty of time
        } else {
            convertView.setBackgroundColor(Color.HSVToColor(new float[] { 212, 91, 50 }));
        }

        return convertView;
    }
}
