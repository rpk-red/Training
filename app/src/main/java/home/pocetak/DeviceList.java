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

    private static final int REQUEST_ENABLE_BT = 1;
   // private static final int REQUEST_ENABLE_PAIRING = 2;
    private static final String LOG_TAG = "MainActivity";
    public BluetoothAdapter adapterBT;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> pairedDevices;
    private ArrayList<BluetoothDevice> devices;
    private BroadcastReceiver mReceiver;
    BluetoothDevice device;
    BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        checkBT();
        init();
        discoverBT();
        getPairedDevices();

    }



    private void init() {

        pairedDevices = new ArrayList<>();
        devices = new ArrayList<>();
       ListView listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
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

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (adapterBT.getState() == BluetoothAdapter.STATE_OFF) {
                        turnOnBT();
                    }
                }
            }
        };
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
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
        startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
    }


    private void getPairedDevices() {
        Set<BluetoothDevice> devicesArray = adapterBT.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray)
                pairedDevices.add(device.getName());
        }
    }

//    private void pairDevice(BluetoothDevice selectedDevice) {
//
//        Intent intent = new Intent(selectedDevice.ACTION_PAIRING_REQUEST);
//        startActivityForResult(intent, REQUEST_ENABLE_PAIRING);
//
//    }


//    private void unpairDevice(BluetoothDevice selectedDevice) {
//
//        //  Intent intent = new Intent(selectedDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//        //  startActivity(intent);
//        //  finish();
//    }

    private void discoverBT() {
        adapterBT.cancelDiscovery();
        adapterBT.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth must be enabled!", Toast.LENGTH_SHORT).show();
                finishActivity(REQUEST_ENABLE_BT);
                finish();
            }
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"Bluetooth is turned on", Toast.LENGTH_SHORT).show();
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

            MainActivity.device = devices.get(position);
            selectedDevice = devices.get(position);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",selectedDevice);
            setResult(RESULT_OK, returnIntent);
            finish();


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


