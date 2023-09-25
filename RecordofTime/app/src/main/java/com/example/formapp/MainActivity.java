package com.example.formapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView dataListView;
    private static boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        loadFromDBToMemory();
        setDataAdapter();
        setOnClickListener();
    }

    private void initWidgets() {
        dataListView = findViewById(R.id.dataListView);
    }

    private void loadFromDBToMemory() {
        if(firstRun)
        {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
            sqLiteManager.populateNoteListArray();
            sqLiteManager.close();
            finish();
        }
        firstRun = false;
    }

    private void setDataAdapter() {
        DataAdapter dataAdapter = new DataAdapter(getApplicationContext(),Data.nonDeletedDatas());
        dataListView.setAdapter(dataAdapter);
    }

    private void setOnClickListener() {
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Data selectedNote = (Data) dataListView.getItemAtPosition(position);
                Intent editNoteIntent = new Intent(getApplicationContext(), DataActivity.class);
                editNoteIntent.putExtra(Data.DATA_EDIT_EXTRA, selectedNote.getId());
                startActivity(editNoteIntent);
            }
        });
    }

    public void newData(View view) {
        Intent newDataIntent = new Intent(this, DataActivity.class);
        startActivity(newDataIntent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDataAdapter();
    }
}