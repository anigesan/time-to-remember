package com.example.formapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DataAdapter extends ArrayAdapter<Data> {

    public DataAdapter(Context context, List<Data> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Data data = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_cell,parent,false);
        TextView title = convertView.findViewById(R.id.cellTitle);
        TextView note = convertView.findViewById(R.id.cellNote);
        title.setText(data.getTitle());
        note.setText(data.getNote());

        return convertView;
    }
}
