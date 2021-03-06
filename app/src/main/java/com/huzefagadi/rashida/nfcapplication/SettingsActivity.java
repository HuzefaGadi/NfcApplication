package com.huzefagadi.rashida.nfcapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    Button save;
    EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("SHARED", MODE_PRIVATE);
        edit = sharedPreferences.edit();

        location = (EditText) findViewById(R.id.location);
        String locationData = sharedPreferences.getString("LOCATION","");
        location.setText(locationData);

        save = (Button) findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"locaion cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    edit.putString("LOCATION",location.getText().toString());
                    edit.commit();
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
}
