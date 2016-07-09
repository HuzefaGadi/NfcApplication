package com.huzefagadi.rashida.nfcapplication;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


public class TransactionResult extends Activity {
    boolean result;
    RelativeLayout relativeLayoutSuccess,relativeLayoutFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_result);
        relativeLayoutSuccess = (RelativeLayout) findViewById(R.id.success);
        relativeLayoutFail = (RelativeLayout) findViewById(R.id.fail);
        result=getIntent().getBooleanExtra("RESULT",false);
        if(result)
        {
            relativeLayoutSuccess.setVisibility(View.VISIBLE);
            relativeLayoutFail.setVisibility(View.GONE);
        }
        else {
            relativeLayoutSuccess.setVisibility(View.GONE);
            relativeLayoutFail.setVisibility(View.VISIBLE);
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction_result, menu);
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
