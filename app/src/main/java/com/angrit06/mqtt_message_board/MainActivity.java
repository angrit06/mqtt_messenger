package com.angrit06.mqtt_message_board;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    private static String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        //check which button is active
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.urgent:
                if (checked)
                    topic = "/mqtt_message_board/urgent/";
                break;
            case R.id.task:
                if (checked)
                    topic = "/mqtt_message_board/task/";
                break;
            case R.id.information:
                if (checked)
                    topic = "/mqtt_message_board/information/";
                break;
        }
    }

    public static String getTopic() {
        // default topic is urgent
        if (topic == null){
            topic = "/mqtt_message_board/urgent/";
        }
        return topic;
    }
}
