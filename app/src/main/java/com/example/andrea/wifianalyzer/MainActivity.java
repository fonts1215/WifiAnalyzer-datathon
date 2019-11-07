package com.example.andrea.wifianalyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    Button enableButton;
    ProgressBar progressBar;
    RecyclerView apRecyclerView;
    ApViewAdapter adapter;
    Context context = this;
    final String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
            };

    private final int ALL_PERMISSION = 1;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);

        apRecyclerView = findViewById(R.id.apView);
        apRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
        final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

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

        progressBar = findViewById(R.id.progressBar2);
        enableButton = findViewById(R.id.button1);
        enableButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("::WIFI ANALISYS START::", "STARTED");
                wifiManager.startScan();
                progressStatus = 0;
                // Visible the progress bar
                progressBar.setVisibility(View.VISIBLE);

            }
        });
    }

    private void scanSuccess(WifiManager wifiManager) {
        Log.e("SCAN SUCCESS::::", "X");
        List<ScanResult> results = wifiManager.getScanResults();
        progressBar.setVisibility(View.GONE);
        adapter = new ApViewAdapter(this, parseData(results));
        apRecyclerView.setAdapter(adapter);

    }

    private void scanFailure(WifiManager wifiManager) {
        Log.e("SCAN FAILED::::", "X");
        List<ScanResult> results = wifiManager.getScanResults();
        progressBar.setVisibility(View.GONE);
        Log.e("::FAILURE::", "Loaded old results.");
        adapter = new ApViewAdapter(this, parseData(results));
        apRecyclerView.setAdapter(adapter);
    }

    private double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    public List<AccessPoint> parseData(List<ScanResult> wifi_list){
        List<AccessPoint> accessPointList = new ArrayList<>();
        for(ScanResult res: wifi_list){
            AccessPoint ap = new AccessPoint();
            ap.setSSID(res.SSID);
            ap.setBSSID(res.BSSID);
            ap.setTimestamp(res.timestamp);
            ap.setFrequency(res.frequency);
            ap.setChannel(res.channelWidth);
            ap.setSignalStrenght(res.level);
            ap.setDistance(calculateDistance(res.level, res.frequency));
            accessPointList.add(ap);
        }
        AccessPoint.ID++;
        return accessPointList;
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