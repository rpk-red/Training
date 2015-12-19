package home.pocetak;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {
    private static final int REQUEST_ENABLE_BT = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MESSAGE_READ = 1;
    private static final int SUCCESS_CONNECTED = 0;
    private static final int TYPED_IN = 2;
    private Handler handler;
    static Intent btIntent;
    static EditText unesi;
    static TextView ispisi;
    Button connectBtn;
    private static final String LOG_TAG = "MainActivity";
    static BluetoothAdapter adapterBT;
    static BluetoothDevice selectedDevice;
    private ConnectThread connect;
    private ConnectedThread connected;
    String uneto;
    String vraceno;
    private int flag =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "OnCreate");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkBT();
        Init();
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                connectBtn.setText(R.string.disconnect_btn);
                switch (msg.what) {

                    case SUCCESS_CONNECTED:
                        connected = new ConnectedThread((BluetoothSocket) msg.obj);
                        connected.start();
                        break;

                    case MESSAGE_READ:
                        vraceno = new String ( (byte[]) msg.obj );
                        Toast.makeText(getApplicationContext(),"citas: " + vraceno, Toast.LENGTH_LONG).show();
                        sortiranje();
                        break;

                    case TYPED_IN:
                        String a = (String) msg.obj;
                        String b = a.concat("\r\n");
                        connected.write(b.getBytes());
                        sortiranje();
                        Toast.makeText(MainActivity.this, "UPISAO JE: " + b, Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });

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
        Log.d(LOG_TAG, "onResume");
    }

    private void startConnect() {
        if(selectedDevice != null){
            flag = 1;
            connect = new ConnectThread(selectedDevice);
            connect.start();
        }

    }

    public void sortiranje()
    {
        ispisi.setText("vraceno je: " + vraceno + " uneto je: " + uneto);
    }


    public void btnClickHandler(View view) {

        if(view.getId() == R.id.btnConnect) {

            if(connectBtn.getText().toString().equalsIgnoreCase("connect") ){
                btIntent = new Intent(this, DeviceList.class);
                startActivity(btIntent);
            }
            else if(connectBtn.getText().toString().equalsIgnoreCase("disconnect")){
                connected.cancel();
                connect.cancel();
                flag = 0;
                connectBtn.setText(R.string.connect_btn);
                selectedDevice = null;
                Toast.makeText(MainActivity.this, "You are now disconnected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Init()
    {
        selectedDevice = null;
        connectBtn = (Button) findViewById(R.id.btnConnect);
        unesi = (EditText) findViewById(R.id.text2unesi);
        ispisi = (TextView) findViewById(R.id.text3);
        uneto = unesi.getText().toString();
        unesi.setOnKeyListener(this);
    }

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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && flag == 1 && event.getAction() != KeyEvent.ACTION_DOWN){
            Log.d(LOG_TAG,keyCode + " " + event.getKeyCode());
            uneto = unesi.getText().toString();
            handler.obtainMessage(TYPED_IN, uneto).sendToTarget();
        }
        return false;
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp_socket = null;
            mmDevice = device;

            try {
                tmp_socket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.v(LOG_TAG, "GRESKA kod uspostavljanja socket-a u ConnectThread-u.");
            }

            mmSocket = tmp_socket;

        }

        public void run() {

            adapterBT.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d(LOG_TAG, "VALJA ZA SADA, odradio sam mmSocet.connect");
            } catch (Exception e) {

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.v(LOG_TAG, "NEVALJA, nisam odradio mmSocket.connect");
                }

            }

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "YOU ARE NOW CONNECTED", Toast.LENGTH_SHORT).show();

                }
            });

            handler.obtainMessage(SUCCESS_CONNECTED, mmSocket).sendToTarget();
            Log.d(LOG_TAG,"Odradio sam handler.obtainMsg u ConnectThread-u");

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Nije uspeo da zatvori mmSocet u ConnectThread-u. ");
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
                    buffer = new byte[1024];
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
               // mmOutStream.flush();
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
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("str", connectBtn.getText().toString());
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        connectBtn.setText(savedInstanceState.getString("str"));
//   }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e(LOG_TAG, "ORIENTATION_LANDSCAPE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e(LOG_TAG, "ORIENTATION_PORTRAIT");
        }
    }
}