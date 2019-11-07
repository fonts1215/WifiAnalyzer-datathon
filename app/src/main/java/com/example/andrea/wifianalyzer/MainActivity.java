package com.example.andrea.wifianalyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    Button enableButton;
    ProgressBar progressBar;
    RecyclerView apRecyclerView;
    ApViewAdapter adapter;
    Context context = this;
    final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION };

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
                    if(intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false))
                        scanSuccess(wifiManager);
                    else
                        scanFailure(wifiManager);


                if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    WifiInfo myConnInfo = wifiManager.getConnectionInfo ();
                    Log.i("RECEIVED ACTION::::", "NET INFO" + myConnInfo.toString());
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        String URL = "http://192.168.137.192:8080/test/data/myRete";
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("describeContents", myConnInfo.describeContents());
                        jsonBody.put("networkID", myConnInfo.getNetworkId());
                        jsonBody.put("bbsid", myConnInfo.getBSSID());
                        jsonBody.put("frequency", myConnInfo.getFrequency());
                        jsonBody.put("hiddenSSID", myConnInfo.getHiddenSSID());
                        jsonBody.put("ssid", myConnInfo.getSSID());
                        jsonBody.put("linkspeed", myConnInfo.getLinkSpeed());
                        jsonBody.put("macAddress", myConnInfo.getMacAddress());
                        jsonBody.put("rssi", myConnInfo.getRssi());
                        final String requestBody = jsonBody.toString();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.toString());
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                    return null;
                                }
                            }

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.statusCode);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                        };
                        try {
                            Log.e("VOLLEY",stringRequest.getBody().toString());
                        } catch (AuthFailureError authFailureError) {
                            authFailureError.printStackTrace();
                        }
                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            }
        };

        IntentFilter filterScan = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        IntentFilter filterConn = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiScanReceiver, filterScan);
        registerReceiver(wifiScanReceiver, filterConn);

        progressBar = findViewById(R.id.progressBar2);
        enableButton = findViewById(R.id.button1);
        enableButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("::asdasd::", "STARTED");
                wifiManager.startScan();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void scanSuccess(WifiManager wifiManager) {
        List<ScanResult> results = wifiManager.getScanResults();
        Log.i("AndreaFonte", results.toString());
        progressBar.setVisibility(View.GONE);
        adapter = new ApViewAdapter(this, parseData(results));
        apRecyclerView.setAdapter(adapter);
        for(ScanResult sr : results){
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                Log.i("VOLLEY", sr.SSID);
                String URL = "http://192.168.137.192:8080/test/data/findReti";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("venueName", sr.venueName);
                jsonBody.put("frequency", sr.frequency);
                jsonBody.put("channelWidth", sr.channelWidth);
                jsonBody.put("level", sr.level);
                jsonBody.put("ssid", sr.SSID);
                jsonBody.put("bssid", sr.BSSID);
                jsonBody.put("centerFreq0", sr.centerFreq0);
                jsonBody.put("centerFreq1", sr.centerFreq1);
                jsonBody.put("timestamp", sr.timestamp);
                jsonBody.put("capabilities", sr.capabilities);
                jsonBody.put("operatorFriendlyName", sr.operatorFriendlyName);
                jsonBody.put("distanceFromRouter", calculateDistance(sr.level, sr.frequency));
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                try {
                    Log.e("VOLLEY",stringRequest.getBody().toString());
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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