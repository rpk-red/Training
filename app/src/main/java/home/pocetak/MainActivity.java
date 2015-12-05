package home.pocetak;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MESSAGE_READ = 1;
    private static final int SUCCESS_CONNECTED = 0;
    private Handler handler;
    static Intent btIntent;
    static EditText unesi;
    static TextView ispisi;
    Button connectBtn;
    Button disconnectBtn;
    private static final String LOG_TAG = "MainActivity";
    static BluetoothAdapter adapterBT;
    static BluetoothDevice selectedDevice;
    private ConnectThread connect;
    private ConnectedThread connected;
    String uneto = "LEVO";
    String vraceno = "nista";
    static int flag =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(flag == 1) {
            flag = 0;
            startConnect();
        }
        checkBT();
        Init();
        Button button = (Button) findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortiranje();
                Log.d(LOG_TAG, "OnClick");
            }
        });

        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                disconnectBtn.setVisibility(View.VISIBLE);
                connectBtn.setVisibility(View.GONE);
                    switch (msg.what) {

                        case SUCCESS_CONNECTED:
                            connected = new ConnectedThread((BluetoothSocket) msg.obj);
                            connected.start();
                            Toast.makeText(MainActivity.this, "UPISAO JE OVO: " + uneto, Toast.LENGTH_LONG).show();
                            try {
                                connected.write(uneto.getBytes());
                            } catch (Exception e) {
                                Log.d(LOG_TAG, "Connect exception" + e.getMessage());
                            }
                        break;

                        case MESSAGE_READ:
                            byte[] readBuffer = (byte[]) msg.obj;
                            vraceno = readBuffer.toString();
                            Toast.makeText(getApplicationContext(),"citas: " + vraceno, Toast.LENGTH_LONG).show();
                            break;
                    }

            }
        };


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(LOG_TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(LOG_TAG, "onOptionsItemSelected");
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
    protected void onResume() {
        super.onResume();
        startConnect();
    }

    private void startConnect() {
        if(selectedDevice != null){
            connect = new ConnectThread(selectedDevice);
            connect.start();
        }

    }

    public void sortiranje()
    {
        unesi = (EditText) findViewById(R.id.text2unesi);
        ispisi = (TextView) findViewById(R.id.text3);
        uneto = unesi.getText().toString();

        if(uneto.equalsIgnoreCase("NAPRED")) {

            ispisi.setText("IDEM NAPRED" + vraceno);

        }
        else if (uneto.equalsIgnoreCase("NAZAD")) {

            ispisi.setText("IDEM NAZAD"  + vraceno);

    }
        else if (uneto.equalsIgnoreCase("LEVO")) {

            ispisi.setText("IDEM LEVO " + vraceno);

    }
        else if (uneto.equalsIgnoreCase("DESNO")){

            ispisi.setText("IDEM DESNO"  + vraceno);

    }
        else{

            ispisi.setText("NISTA OVO NE VALJA"  + vraceno);
    }
    }


    public void btnClickHandler(View view) {

        if(view.getId() == R.id.btnConnect) {

                btIntent = new Intent(this, DeviceList.class);
                startActivity(btIntent);
        }
        if(view.getId() == R.id.btnDisconnect){
            connected.cancel();
            connect.cancel();
            connectBtn.setVisibility(View.VISIBLE);
            disconnectBtn.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "You are now disconnected!", Toast.LENGTH_SHORT).show();
        }
    }

    public void Init()
    {
        selectedDevice = null;
        connectBtn = (Button) findViewById(R.id.btnConnect);
        disconnectBtn = (Button) findViewById(R.id.btnDisconnect);
        disconnectBtn.setVisibility(View.GONE);
    }
//    public void mangeConnection(){
//
//        String k = "NAPRED";
//        if(result == 0) {
//            ConnectedThread connectedThread = new ConnectedThread(DeviceList.globalSocket);
//            connectedThread.write(k.getBytes());
//            Log.d(LOG_TAG, "POSLAO STRING");
//        }
////        String str = null;
////        if(disconnectBtn.isShown()){
////            str = unesi.getText().toString();
////        }
////            if(str != null){
////                btIntent.putExtra("str_komanda", str);
////            }
////            startActivity(btIntent);
////
////            sortiranje();
////
//       }

    private void checkBT() {

        adapterBT = BluetoothAdapter.getDefaultAdapter();

        if (adapterBT == null) {
            Toast.makeText(this, "Bluetooth not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!adapterBT.isEnabled()) {
            turnOnBT();
        }
    }

    private void turnOnBT() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth must be enabled!", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "bluetooth is enabled", Toast.LENGTH_SHORT).show();
                finishActivity(REQUEST_ENABLE_BT);
            }
        }

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

            adapterBT.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d(LOG_TAG, "VALJA ZA SADA");
            } catch (Exception e) {

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.v(LOG_TAG, "NEVALJA");
                }

            }

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "YOU ARE NOW CONNECTED", Toast.LENGTH_SHORT).show();

                }
            });

            handler.obtainMessage(SUCCESS_CONNECTED, mmSocket).sendToTarget();
            Log.d(LOG_TAG,"STIGAO SAM DOVDE");

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;


        public ConnectedThread(BluetoothSocket Socket){

            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmSocket = Socket;

            try {
                tmpIn = Socket.getInputStream();
                tmpOut = Socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer;
            int bytes;

            while (true) {

                try {
                    buffer =new byte[1024];
                    bytes = mmInStream.read(buffer);
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }

        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        connectBtn.getVisibility();
        disconnectBtn.getVisibility();



    }
}
