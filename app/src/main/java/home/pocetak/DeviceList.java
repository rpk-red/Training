package home.pocetak;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_PAIRING = 2;
    private static final String LOG_TAG = "MainActivity";
    private static final int SUCCESS_CONNECTED = 0;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> pairedDevices;
    private ArrayList<BluetoothDevice> devices;
    private Set<BluetoothDevice> devicesArray;
    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    BluetoothDevice device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        init();
        getPairedDevices();
        discoverBT();
    }


    private void init() {

        pairedDevices = new ArrayList<>();
        devices = new ArrayList<BluetoothDevice>();
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mReceiver = new BroadcastReceiver() {
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
                    if (MainActivity.adapterBT.getState() == MainActivity.adapterBT.STATE_OFF) {
                        Toast.makeText(DeviceList.this, "Your bluetooth is disabled, you have to enable it!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        };
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void getPairedDevices() {
        devicesArray = MainActivity.adapterBT.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray)
                pairedDevices.add(device.getName());
        }
    }

    private void pairDevice(BluetoothDevice selectedDevice) {

        Intent intent = new Intent(selectedDevice.ACTION_PAIRING_REQUEST);
        //startActivityForResult(intent, REQUEST_ENABLE_PAIRING);
        startActivity(intent);

    }


//    private void unpairDevice(BluetoothDevice selectedDevice) {
//
//        //  Intent intent = new Intent(selectedDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//        //  startActivity(intent);
//        //  finish();
//    }

    private void discoverBT() {
        MainActivity.adapterBT.cancelDiscovery();
        MainActivity.adapterBT.startDiscovery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Log.d(LOG_TAG, "Kliknut");
//        if (MainActivity.adapterBT.isDiscovering()) {
//            MainActivity.adapterBT.cancelDiscovery();
//        }
        if (listAdapter.getItem(position).contains("Paired")) {

            MainActivity.selectedDevice = devices.get(position);
            finish();

        } else {
            //selectedDevice = devices.get(position);
            Toast.makeText(this, "Device is not paired, you have to pair it 1st!", Toast.LENGTH_SHORT).show();
            //pairDevice(selectedDevice);
        }
    }


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
