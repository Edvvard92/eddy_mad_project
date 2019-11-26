package com.example.fuellog;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int EXTRA_REPLY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewLogActivity.class);
                startActivityForResult(intent, EXTRA_REPLY);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final LogListAdapter adapter = new LogListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLogViewModel = ViewModelProviders.of(this).get(LogViewModel.class);
        mLogViewModel.getAllLogs().observe(this, new Observer<List<Log>>() {
            @Override
            public void onChanged(@Nullable final List<Log> logs) {
                adapter.setLogs(logs);
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Log myLog = adapter.getLogAtPosition(position);
                        Toast.makeText(MainActivity.this, "Deleting " +
                                myLog.getLog(), Toast.LENGTH_LONG).show();

                        // Delete the log
                        mLogViewModel.deleteLog(myLog);
                    }
                });

        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear_data) {
            // Add a toast just for confirmation
            Toast.makeText(this, "Clearing the data...",
                    Toast.LENGTH_SHORT).show();

            // Delete the existing data
            mLogViewModel.deleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    private LogViewModel mLogViewModel;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        android.util.Log.d(TAG, "onActivityResult: requestCode: " + requestCode + " resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        android.util.Log.d(TAG, "onActivityResult: value1:" + data.getStringExtra("value1"));
        android.util.Log.d(TAG, "onActivityResult: value2:" + data.getStringExtra("value2"));
        android.util.Log.d(TAG, "onActivityResult: value3:" + data.getStringExtra("value3"));
        if (requestCode == EXTRA_REPLY) {
            android.util.Log.d(TAG, "onActivityResult: replyCode " + EXTRA_REPLY + " resultCode: " + RESULT_OK);
            Log log = new Log(data.getStringExtra("value1"), data.getStringExtra("value2"), data.getStringExtra("value3"));
            mLogViewModel.insert(log);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}
