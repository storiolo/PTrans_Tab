package com.horanet.BarbeBLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.horanet.BarbeBLE.R;
import static com.horanet.BarbeBLE.Constants.PARCEL_HORANET_UUID;

public class CentralRoleActivity extends AppCompatActivity implements DevicesAdapter.DevicesAdapterListener {

    private RecyclerView mDevicesRecycler;
    private DevicesAdapter mDevicesAdapter;

    private ScanCallback mScanCallback;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_role);

        mDevicesRecycler = (RecyclerView) findViewById(R.id.devices_recycler_view);
        mDevicesRecycler.setHasFixedSize(true);
        mDevicesRecycler.setLayoutManager(new LinearLayoutManager(this));

        mDevicesAdapter = new DevicesAdapter(this);
        mDevicesRecycler.setAdapter(mDevicesAdapter);

        mHandler = new Handler(Looper.getMainLooper());

        startBLEScan();

    }




    protected BluetoothAdapter getBluetoothAdapter() {

        BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothService = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));

        if (bluetoothService != null) {

            bluetoothAdapter = bluetoothService.getAdapter();

            if (bluetoothAdapter != null) {

                if (bluetoothAdapter.isEnabled()) {
                    return bluetoothAdapter;
                }
            }
        }

        return null;
    }



    private void startBLEScan() {

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();

        if (bluetoothAdapter != null) {

            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothLeScanner != null) {

                if (mScanCallback == null) {

                    // Kick off a new scan.
                    mScanCallback = new SampleScanCallback();
                    bluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);


                } else {
                }

                return;
            }
        }
    }

    private List<ScanFilter> buildScanFilters() {

        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();

        builder.setServiceUuid(PARCEL_HORANET_UUID);


        scanFilters.add(builder.build());

        return scanFilters;
    }


    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        return builder.build();
    }


    public void stopScanning() {

        Log.d(MainActivity.TAG, "Stopping Scanning");
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();

        if (bluetoothAdapter != null) {

            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothLeScanner != null) {

                // Stop the scan, wipe the callback.
                bluetoothLeScanner.stopScan(mScanCallback);
                mScanCallback = null;

                // Even if no new results, update 'last seen' times.
                mDevicesAdapter.notifyDataSetChanged();

                return;
            }
        }

    }


    @Override
    public void onDeviceItemClick(String deviceName, String deviceAddress) {

        //stopScanning();

        Intent intent = new Intent(this, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_NAME, deviceName);
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        startActivity(intent);
    }



    private class SampleScanCallback extends ScanCallback {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            mDevicesAdapter.add(results);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mDevicesAdapter.add(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

    }


}
