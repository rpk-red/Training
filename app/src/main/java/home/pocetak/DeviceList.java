package home.pocetak;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DeviceList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_PAIRING = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = "MainActivity";
    private static final int MESSAGE_READ = 1;
    private static final int SUCCESS_CONNECTED = 0;
    public BluetoothAdapter adapterBT;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> pairedDevices;
    private ArrayList<BluetoothDevice> devices;
    private Set<BluetoothDevice> devicesArray;
    private BroadcastReceiver mReciver;
    private IntentFilter mFilter;
    BluetoothDevice device;
    ConnectThread connect;
    BluetoothDevice selectedDevice;
     Handler handler;
    static BluetoothSocket globalSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        checkBT();
        init();
        getPairedDevices();
        discoverBT();

        MainActivity.disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connect.cancel();
                MainActivity.disconnectBtn.setVisibility(View.GONE);
                MainActivity.connectBtn.setVisibility(View.VISIBLE);
                MainActivity.CONNECTED = false;

                DeviceList.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DeviceList.this, "YOU ARE NOW DISCONNECTED", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what){
                    case SUCCESS_CONNECTED:
                        //BluetoothSocket connectedSocket = (BluetoothSocket) msg.obj;
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",SUCCESS_CONNECTED);
                        setResult(RESULT_OK, returnIntent);
                        Log.d(LOG_TAG,"SAD SAM OVDE");
                        finish();
                        break;
                }

            }
        };
    }


    private void init() {

        pairedDevices = new ArrayList<String>();
        devices = new ArrayList<BluetoothDevice>();
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String s = "";
                    for (int i = 0; i < pairedDevices.size(); i++) {
                        if (device.getName().equals(pairedDevices.get(i))) {
                            s = "(Paired)";
                            break;
                        }
                    }
                    listAdapter.add(device.getName() + " " + s + " " + "\n" + device.getAddress());
                    devices.add(device);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (adapterBT.getState() == adapterBT.STATE_OFF) {
                        turnOnBT();
                    }
                }
            }
        };
        registerReceiver(mReciver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReciver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReciver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReciver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReciver);
    }


    private void checkBT() {

        adapterBT = BluetoothAdapter.getDefaultAdapter();

        if (adapterBT == null) {
            Toast.makeText(this, "Bluetooth not found!", Toast.LENGTH_SHORT).show();
        }
        if (!adapterBT.isEnabled()) {
            turnOnBT();
        }
    }

    private void turnOnBT() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }


    private void getPairedDevices() {
        devicesArray = adapterBT.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray)
                pairedDevices.add(device.getName());
        }
    }

    private void pairDevice(BluetoothDevice selectedDevice) {

        Intent intent = new Intent(selectedDevice.ACTION_PAIRING_REQUEST);
        startActivityForResult(intent, REQUEST_ENABLE_PAIRING);

    }


    private void unpairDevice(BluetoothDevice selectedDevice) {

        //  Intent intent = new Intent(selectedDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        //  startActivity(intent);
        //  finish();
    }

    private void discoverBT() {
        adapterBT.cancelDiscovery();
        adapterBT.startDiscovery();
    }

    public void onClickHandler(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth must be enabled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (resultCode == REQUEST_ENABLE_PAIRING) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You need to pair devices to connect!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Log.d(LOG_TAG, "Kliknut");
        if (adapterBT.isDiscovering()) {
            adapterBT.cancelDiscovery();
        }
        if (listAdapter.getItem(position).contains("Paired")) {

            selectedDevice = devices.get(position);
            connect = new ConnectThread(selectedDevice);
            connect.start();

        } else {
            //selectedDevice = devices.get(position);
            Toast.makeText(this, "Device is not paired, you have to pair it 1st!", Toast.LENGTH_SHORT).show();
            //pairDevice(selectedDevice);
        }
//        String[] itemSelected =  listAdapter.getItem(position).split("\n");
//        Toast.makeText(this, "Name: " + itemSelected[0] + "\n" + "Adress:" + itemSelected[1],Toast.LENGTH_LONG).show();
//        if (adapterBT.checkBluetoothAddress(itemSelected[1])== true)
//            device = adapterBT.getRemoteDevice(itemSelected[1]);
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp_socket = null;
            mmDevice = device;

            try {
                tmp_socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.v(LOG_TAG, "GRESKA OVDE");
            }

            mmSocket = tmp_socket;

        }

        public void run() {

            try {
                mmSocket.connect();
                Log.d(LOG_TAG, "VALJA ZA SADA");
                MainActivity.CONNECTED = true;

            } catch (Exception e) {

                try {
                    mmSocket.close();
                    MainActivity.CONNECTED = false;
                } catch (IOException closeException) {
                    Log.v(LOG_TAG, "NEVALJA");
                }

            }

            DeviceList.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DeviceList.this, "YOU ARE NOW CONNECTED", Toast.LENGTH_LONG).show();

                }
            });

            handler.obtainMessage(SUCCESS_CONNECTED).sendToTarget();
            globalSocket = mmSocket;
            Log.d(LOG_TAG,"STIGAO SAM DOVDE");
            finish();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


//    public static class L{
//        public static void m(String message){
//            Log.d(LOG_TAG, message);
//        }
//        public static void s(Context context,String message){
//            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
//        }
//    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    }


