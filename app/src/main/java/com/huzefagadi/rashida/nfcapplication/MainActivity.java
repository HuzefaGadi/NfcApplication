package com.huzefagadi.rashida.nfcapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huzefagadi.rashida.nfcapplication.bean.Gourme;
import com.huzefagadi.rashida.nfcapplication.bean.ResponseFromServer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends Activity {
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private final String NAMESPACE = "http://tempuri.org/";
    ImageButton settings;
    /*private final String REGID_URL = "http://m.buzzonn.com/BuzzonFBList.asmx";
    private final String REGID_SOAP_ACTION = "http://tempuri.org/insertRegId";
	private final String REGID_METHOD_NAME = "insertRegId";*/

    private final String REGID_URL = "http://gourme.pretok.si/Service.asmx";
    private final String REGID_SOAP_ACTION = "http://tempuri.org/GetUserDetails";
    private final String REGID_METHOD_NAME = "GetUserDetails";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Date todayDate = new Date();
        try {
            Date actualDate = dateFormat.parse("28/07/2035");
            settings = (ImageButton) findViewById(R.id.settings);

            mTextView = (TextView) findViewById(R.id.information);
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (actualDate.after(todayDate)) {

                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                    }
                });


                if (mNfcAdapter == null) {
                    // Stop here, we definitely need NFC
                    Toast.makeText(this, R.string.nfc_not_supported_information, Toast.LENGTH_LONG).show();
                    finish();
                    return;

                }

                if (!mNfcAdapter.isEnabled()) {
                    mTextView.setText(R.string.nfc_disabled_information);
                } else {
                    mTextView.setText(R.string.nfc_enabled_information);
                }

                onTagDiscovered(getIntent());
            } else {

                Toast.makeText(getApplicationContext(), "DATE PASSED", Toast.LENGTH_SHORT).show();
                finish();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText(R.string.nfc_disabled_information);
        } else {
            mTextView.setText(R.string.nfc_enabled_information);
        }

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    // region NFC reader
    @SuppressLint("NewApi")
    private boolean isNfcEnabled() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null && nfc.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    private void onTagDiscovered(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mTextView.setText(R.string.processing);
            showDialog();
            new GetMemberDetailsByTagId().execute(tag);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the
         * current activity instance. Instead of creating a new activity,
         * onNewIntent will be called. For more information have a look at the
         * documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to
         * the device.
         */
        onTagDiscovered(intent);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground
     *                 dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    @SuppressLint("NewApi")
    public static void setupForegroundDispatch(final Activity activity,
                                               NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(),
                activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(
                activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters,
                techList);
    }

    /**
     * @param activity The corresponding {@link MainActivity} requesting to stop the
     *                 foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    @SuppressLint("NewApi")
    public static void stopForegroundDispatch(final Activity activity,
                                              NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @SuppressLint("NewApi")
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public class GetMemberDetailsByTagId extends AsyncTask<Tag, Void, String> {
        Cursor mCursor;

        @SuppressLint("NewApi")
        @Override
        protected String doInBackground(Tag... params) {

            Tag tag = params[0];
            String mTagId = ByteArrayToHexString(tag.getId());
            mTagId = mTagId.toUpperCase();


            return String.valueOf(mTagId);

        }

        @Override
        protected void onPostExecute(String result) {
            // mTextView.setText(result);
            new AsyncCallWS().execute(result);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
    }


    public String sendCardNumber(String cardNumber) {
        {
            //Create request
            SoapObject request = new SoapObject(NAMESPACE, REGID_METHOD_NAME);
            //Property which holds input parameters
            PropertyInfo strKoda = new PropertyInfo();
            //Set Name
            strKoda.setName("strKoda");
            //Set Value
            strKoda.setValue(cardNumber);
            //Set dataType
            strKoda.setType(String.class);
            //Add the property to request object

            request.addProperty(strKoda);

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

            String result = sendCardNumber(params[0]);
          // String result = sendCardNumber("AB096287");
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Gson gson = new Gson();

            ResponseFromServer response = gson.fromJson(result, ResponseFromServer.class);
            if (response != null && !response.getGourme().isEmpty()) {
                Gourme gourme = response.getGourme().get(0);
                if (gourme != null && gourme.getId() != null) {
                    Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                    intent.putExtra("RESPONSE", result);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.details_not_found, Toast.LENGTH_SHORT).show();
                    if (!mNfcAdapter.isEnabled()) {
                        mTextView.setText(R.string.nfc_disabled_information);
                    } else {
                        mTextView.setText(R.string.nfc_enabled_information);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.details_not_found, Toast.LENGTH_SHORT).show();
                if (!mNfcAdapter.isEnabled()) {
                    mTextView.setText(R.string.nfc_disabled_information);
                } else {
                    mTextView.setText(R.string.nfc_enabled_information);
                }
            }


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
        progressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Processing...", true);
        progressDialog.setCancelable(true);
    }
}
