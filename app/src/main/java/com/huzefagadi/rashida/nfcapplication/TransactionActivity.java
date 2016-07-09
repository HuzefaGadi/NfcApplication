package com.huzefagadi.rashida.nfcapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huzefagadi.rashida.nfcapplication.bean.Gourme;
import com.huzefagadi.rashida.nfcapplication.bean.ResponseForTransaction;
import com.huzefagadi.rashida.nfcapplication.bean.ResponseFromServer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class TransactionActivity extends Activity {

    Gourme gourme;
    TextView name, information;
    EditText amount;
    Button confirm;
    Double amountDouble;
    boolean result;
    private final String TAG = "nfc";
    private final String NAMESPACE = "http://tempuri.org/";
    /*private final String REGID_URL = "http://m.buzzonn.com/BuzzonFBList.asmx";
    private final String REGID_SOAP_ACTION = "http://tempuri.org/insertRegId";
	private final String REGID_METHOD_NAME = "insertRegId";*/

    private final String REGID_URL = "http://gourme.pretok.si/Service.asmx";
    private final String REGID_SOAP_ACTION = "http://tempuri.org/InsertTransactionDetails";
    private final String REGID_METHOD_NAME = "InsertTransactionDetails";
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        result = false;
        sharedPreferences = getSharedPreferences("SHARED", MODE_PRIVATE);
        name = (TextView) findViewById(R.id.name);
        amount = (EditText) findViewById(R.id.amount);
        confirm = (Button) findViewById(R.id.confirm);
        information = (TextView) findViewById(R.id.information);

        Gson gson = new Gson();

        ResponseFromServer response = gson.fromJson(getIntent().getStringExtra("RESPONSE"), ResponseFromServer.class);
        gourme = response.getGourme().get(0);

        if (gourme != null) {
            name.setText(gourme.getIme() + " " + gourme.getPriimek());
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (amount.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.amount_empty_message, Toast.LENGTH_SHORT).show();
                    } else {
                        information.setText(R.string.processing);
                        amountDouble = Double.parseDouble(amount.getText().toString());
                        String limit = gourme.getLimit();
                        if (limit != null) {
                            limit = limit.replace(",", ".");
                        }
                        Double amountLimit = Double.parseDouble(limit);
                        if (gourme.getAktiven()) {
                            if (amountDouble > amountLimit && !gourme.getPrioriteta()) {
                                result = false;
                            } else {
                                result = true;
                            }
                        } else {
                            result = false;
                        }
                        if (result) {

                            String locationData = sharedPreferences.getString("LOCATION", "");
                            showDialog();
                            new AsyncCallWS().execute(String.valueOf(amountDouble), gourme.getId(), locationData);

                        } else {
                            Intent intent = new Intent(getApplicationContext(), TransactionResult.class);
                            intent.putExtra("RESULT", result);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });

        } else {

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
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

    public String sendTransactionDetails(String amount, String cardNumber, String location) {
        {
            //Create request
            SoapObject request = new SoapObject(NAMESPACE, REGID_METHOD_NAME);
            //Property which holds input parameters
            PropertyInfo strAmount = new PropertyInfo();
            //Set Name
            strAmount.setName("strAmount");
            //Set Value
            strAmount.setValue(amount);
            //Set dataType
            strAmount.setType(String.class);
            //Add the property to request object

            request.addProperty(strAmount);

            PropertyInfo strCardNumber = new PropertyInfo();
            //Set Name
            strCardNumber.setName("strCardNumber");
            //Set Value
            strCardNumber.setValue(cardNumber);
            //Set dataType
            strCardNumber.setType(String.class);
            //Add the property to request object

            request.addProperty(strCardNumber);

            PropertyInfo strLocation = new PropertyInfo();
            //Set Name
            strLocation.setName("strLocation");
            //Set Value
            strLocation.setValue(location);
            //Set dataType
            strLocation.setType(String.class);
            //Add the property to request object


            request.addProperty(strLocation);

            //Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            //Set output SOAP object
            envelope.setOutputSoapObject(request);


            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(REGID_URL);

            try {
                //Invole web service
                androidHttpTransport.call(REGID_SOAP_ACTION, envelope);
                //Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                //Assign it to fahren static variable
                String responseFromService = response.toString();
                System.out.println("Response " + responseFromService);
                return responseFromService;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");

            String result = sendTransactionDetails(params[0], params[1], params[2]);
            return result;

        }

        @Override
        protected void onPostExecute(String responseString) {
            Log.i(TAG, "onPostExecute");
            if(progressDialog!=null)
            {
                progressDialog.dismiss();
            }
            // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            ResponseForTransaction response = gson.fromJson(responseString, ResponseForTransaction.class);
            if (response.getRowsInserted() == 0) {
                result = false;
            }
            Intent intent = new Intent(getApplicationContext(), TransactionResult.class);
            intent.putExtra("RESULT", result);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }


    }

    public void showDialog() {
        progressDialog = ProgressDialog.show(TransactionActivity.this, "Please wait ...", "Processing...", true);
        progressDialog.setCancelable(true);
    }
}
