package com.example.andrea.wifianalyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
    Button enableButton;
    Context context = this;
    final String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
            };

    private final int ALL_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        enableButton = findViewById(R.id.button1);
        enableButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i("::WIFI ANALISYS START::", "STARTED");
                BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context c, Intent intent) {
                        boolean success = intent.getBooleanExtra(
                                WifiManager.EXTRA_RESULTS_UPDATED, false);
                        if (success) {
                            scanSuccess(wifiManager);
                        } else {
                            // scan failure handling
                            scanFailure(wifiManager);
                        }
                    }
                };

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                context.registerReceiver(wifiScanReceiver, intentFilter);

                boolean success = wifiManager.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure(wifiManager);
                }
            }
        });
    }

    private void scanSuccess(WifiManager wifiManager) {
        Log.e("SCAN SUCCESS::::", "X");
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult res: results) {
            Log.i("RETE::::", res.toString());
        }
    }

    private void scanFailure(WifiManager wifiManager) {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        Log.e("SCAN FAILED::::", "X");
        List<ScanResult> results = wifiManager.getScanResults();
        Log.e("::FAILURE::", "Loaded old results.");
        for (ScanResult res: results) {
            Log.i("RETE::::", res.toString());
        }
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                Log.i("PERMISSION:::","Permission is NOT granted");
                requestPermissions(permissions, ALL_PERMISSION);

            }else{
                Log.v("PERMISSION:::","Permission already be granted");
            }
        }else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION:::","Permission is granted under SDK23.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSION:
                if (grantResults.length > 0 && permissions.length==grantResults.length) {
                    Log.e("PERMISSION OK:::::", "Permission Granted.");
                }else{
                    Log.e("PERMISSION FALSE::::", "Permission Denied.");
                    Toast.makeText(this, "Please, accept permissions!", Toast.LENGTH_SHORT).show();
                    checkPermission();
                }
                break;
        }
    }
}