package legacy.com.bluetoothle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements BluetoothAdapter.LeScanCallback {
    Context context;

    private BluetoothAdapter mBluetoothAdpter;
    private static final UUID SERVICE = UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e");
    private HashMap<String, ProximityBeacon> mBeacon;
    private String deviceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdpter = bluetoothManager.getAdapter();
        mBeacon = new HashMap<String, ProximityBeacon>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdpter == null || !mBluetoothAdpter.isEnabled()){
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            finish();
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(MainActivity.this, "Service not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
            startScan();


    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdpter.stopLeScan(this);
    }
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {

            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            mBluetoothAdpter.startLeScan(MainActivity.this);
            startScan();
        }
    };


    private void startScan(){
        mBluetoothAdpter.startLeScan(new UUID[] {SERVICE}, this);
        mHandler.postDelayed(mStopRunnable, 10000);
        Toast.makeText(getApplicationContext(), "Scan started", Toast.LENGTH_SHORT).show();
    }

    private void stopScan(){
        mBluetoothAdpter.stopLeScan(this);
        mHandler.postDelayed(mStartRunnable, 300000);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        deviceID = device.getName();

        Log.i("Bkay", "Device found: " + device.getAddress() + ": " + device.getName());

        ProximityBeacon beacon = new ProximityBeacon(device.getAddress(), rssi);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));

    }
    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProximityBeacon beacon = (ProximityBeacon) msg.obj;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothAdpter.disable();
    }


}
