package com.example.andrea.wifianalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubmitRoom extends AppCompatActivity implements View.OnClickListener {
    Button button;
    EditText casellaStanza;
    String nomeStanza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_room);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        casellaStanza = findViewById(R.id.nomestanza);
        casellaStanza.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button:
                if(casellaStanza.getText().toString() != null){
                    nomeStanza = casellaStanza.getText().toString();
                }




                break;
        }
    }
}
