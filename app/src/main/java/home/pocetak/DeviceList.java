package home.pocetak;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

import java.util.Set;
import java.util.UUID;

public class DeviceList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("f00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = "MainActivity";
   // private static boolean CONNECTED = false;
    public BluetoothAdapter adapterBT;
    private ArrayAdapter<String> listDevices;
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        checkBT();
        init();
        discoverBT();

    }


    private void init() {

       ListView listView = (ListView) findViewById(R.id.listView);
        listDevices = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(listDevices);
        listView.setOnItemClickListener(this);
        pairDevices();
       IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BroadcastReceiver mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listDevices.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        registerReceiver(mReciver, mFilter);
    }



    private void discoverBT() {
        adapterBT.cancelDiscovery();
        adapterBT.startDiscovery();
    }

    private void checkBT() {

        adapterBT = BluetoothAdapter.getDefaultAdapter();

        if(adapterBT == null)
        {
            Toast.makeText(this,"Bluetooth not found!",Toast.LENGTH_SHORT).show();
        }
        if(!adapterBT.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
    }


    private void pairDevices() {
        Set<BluetoothDevice> pairedDevices = adapterBT.getBondedDevices();
        if(pairedDevices.size()>0)
        {
            for(BluetoothDevice device : pairedDevices)
                listDevices.add(device.getName()+ "\n" + device.getAddress());
        }
    }
//    private void unpairDevices(){

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this,"Bluetooth must be enabled!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public void onItemClick (AdapterView < ? > parent, View view,int position, long id){

        Log.d(LOG_TAG, "Kliknut");
        String[] itemSelected =  listDevices.getItem(position).split("\n");
        Toast.makeText(this, "Name: " + itemSelected[0] + "\n" + "Adress:" + itemSelected[1],Toast.LENGTH_LONG).show();
        if (BluetoothAdapter.checkBluetoothAddress(itemSelected[1]))
            device = adapterBT.getRemoteDevice(itemSelected[1]);
    }


}

