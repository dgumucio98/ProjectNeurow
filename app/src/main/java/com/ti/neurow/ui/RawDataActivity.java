package com.ti.neurow.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import com.ti.neurow.R;

public class RawDataActivity extends AppCompatActivity {

    // Define variables and adapter
    private BluetoothAdapter BTadapter;
    private boolean mScanning; //
    private static final long SCAN_PERIOD = 10000; // 10 sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_data);
        BTadapter = BluetoothAdapter.getDefaultAdapter();
    }
}