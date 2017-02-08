package com.littlehouse_design.jsonparsing;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.littlehouse_design.jsonparsing.Utils.DataBase.AddPreferenceService;
import com.littlehouse_design.jsonparsing.Utils.DataBase.DatabaseContract;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    AutoCompleteTextView etIp;

    private String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        etIp = (AutoCompleteTextView) findViewById(R.id.et_settings_enter_ip);
        FloatingActionButton fabIp = (FloatingActionButton) findViewById(R.id.fab_settings_save_ip);

        fabIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etIp.getText().toString().trim();

                if(ip.equals("")) {
                   etIp.setError("Please input an IP");
                }
                else {
                    SaveIP(ip);
                }

            }
        });

        new PreferenceShower().execute();

    }
    private void SaveIP(String ip) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.TablePreferences.COL_PREF_TYPE,1);
        contentValues.put(DatabaseContract.TablePreferences.COL_PREF_VALUE,ip);

        AddPreferenceService.insertNewPref(getApplicationContext(),contentValues);
        if(getCurrentFocus() !=null) {
            Snackbar.make(getCurrentFocus(),"Added " + ip,Snackbar.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG,"current focus was null");
        }



    }
    public void ShowIP(ArrayList<String> ips) {

        ArrayAdapter<String> ipAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_item,
                ips
        );
        etIp.setThreshold(1);
        etIp.setAdapter(ipAdapter);
        if(ips.size() > 0) {
            etIp.setText(ips.get(ips.size() -1 ));
            etIp.clearFocus();
        }

    }

    private class PreferenceShower extends AsyncTask<Void,Void,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void...voidOb) {
            ArrayList ipsToSet = new ArrayList<>();
            Cursor prefCursor = getContentResolver().query(DatabaseContract.PREFERENCES_URI,
                    new String[]{DatabaseContract.TablePreferences.COL_PREF_VALUE},
                    "CAST (" + DatabaseContract.TablePreferences.COL_PREF_TYPE + " AS TEXT) = ?",
                    new String[] {"1"},
                    null
            );
            if(prefCursor != null && prefCursor.moveToFirst()) {
                do{
                    boolean addPref = true;
                    if(prefCursor.getString(0) != null) {
                        Log.d(LOG_TAG,"This is my first cursor result: " + prefCursor.getString(0));
                        if(ipsToSet.size() > 0) {
                            for(int i = 0; i < ipsToSet.size(); i++) {
                                if(ipsToSet.get(i).equals(prefCursor.getString(0))) {
                                    if(prefCursor.getCount() == (prefCursor.getPosition() + 1)) {
                                        ipsToSet.remove(i);
                                        Log.d(LOG_TAG, "Added the most recent IP as the last Pref");
                                    }
                                    else {
                                        addPref = false;
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d(LOG_TAG,"My cursor was empty");
                    }
                    if(addPref) {
                        ipsToSet.add(prefCursor.getString(0));
                        Log.d(LOG_TAG,"Added this IP to my list: " + prefCursor.getString(0));
                    }
                } while (prefCursor.moveToNext());
                prefCursor.close();
            }
            if(ipsToSet.size() == 0) {
                Log.d(LOG_TAG,"no IPS were saved");
            }
            return ipsToSet;
        }
        @Override
        protected void onPostExecute(ArrayList<String> ips) {
            ShowIP(ips);
        }
    }
}
