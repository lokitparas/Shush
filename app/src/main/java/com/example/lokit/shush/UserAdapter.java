package com.example.lokit.shush;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lokit on 10-Jan-16.
 */
public class UserAdapter extends ArrayAdapter<Record> {
    public UserAdapter(Context context, ArrayList<Record> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Record user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population

        TextView eName = (TextView) convertView.findViewById(R.id.Event_name);
        TextView stime = (TextView) convertView.findViewById(R.id.Start_time);
        TextView etime = (TextView) convertView.findViewById(R.id.End_time);
        // Populate the data into the template view using the data object

        eName.setText(user.event_name);
        stime.setText(user.start_time);
        etime.setText(user.end_time);
        // Return the completed view to render on screen

        return convertView;
    }
}