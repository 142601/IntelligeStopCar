package com.red;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    final private String TAG = "SettingActivity";

    private EditText etIpAdd,etIpPort;
    private Button btIpOk;
    private IpInfo ip;
    CharSequence cs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        etIpAdd = findViewById(R.id.ipset);
        etIpPort = findViewById(R.id.ipport);
        btIpOk = findViewById(R.id.ipok);

        ip = new IpInfo("192.168.1.0",456,SettingActivity.this);//默认ip
        etIpAdd.setText(cs = ip.getIpAdd());
        etIpPort.setText(cs = (ip.getIpPort() + ""));

        btIpOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip.setIpAdd(etIpAdd.getText().toString());
                ip.setIpPort(Integer.parseInt(etIpPort.getText().toString()));
                ip.save();
                ip.update();
                finish();
            }
        });
    }
}
