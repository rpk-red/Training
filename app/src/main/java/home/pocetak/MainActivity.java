package home.pocetak;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    static BluetoothDevice device;
    private static final String LOG_TAG = "MainActivity";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int SUCCESS_CONNECTED = 0;
    private static final int MESSAGE_READ = 1;
    private Handler handler;
    static Intent btIntent;
    static EditText unesi;
    static TextView ispisi;
    private Button connectBtn;
    private Button disconnectBtn;
   // private ConnectThread connect;
    private String poruka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Init();
        Button button = (Button) findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortiranje();
                Log.d(LOG_TAG, "OnClick");


            }
        });
//        handler = new Handler(){
//
//            //String getKomanda = getIntent().getStringExtra("str_komanda");
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                    poruka = "LEVO";
//                    switch (msg.what) {
//                        case SUCCESS_CONNECTED:
//                            ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
//                            connectedThread.write(poruka.getBytes());
//                            Log.d(LOG_TAG,"PISEM KOD");
//                            break;
//                        case MESSAGE_READ:
//                            byte[] readBuffer = (byte[]) msg.obj;
//                            Toast.makeText(MainActivity.this, readBuffer.toString(), Toast.LENGTH_LONG).show();
//                            Log.d(LOG_TAG,"CITAM KOD");
//                            break;
//                    }
//
//            }
//        };


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
//
//    public void startClickHandler(View view) {
//
//        Button dugme = (Button) view;
//        Log.d(LOG_TAG,"startClickHandler: " + dugme.getText());
//    }

    public void sortiranje()
    {
        unesi = (EditText) findViewById(R.id.text2unesi);
        ispisi = (TextView) findViewById(R.id.text3);
        String uneto = unesi.getText().toString();

        if(uneto.equalsIgnoreCase("NAPRED")) {

            ispisi.setText("IDEM NAPRED");

        }
        else if (uneto.equalsIgnoreCase("NAZAD")) {

            ispisi.setText("IDEM NAZAD");

    }
        else if (uneto.equalsIgnoreCase("LEVO")) {

            ispisi.setText("IDEM LEVO");

    }
        else if (uneto.equalsIgnoreCase("DESNO")){

            ispisi.setText("IDEM DESNO");

    }
        else{

            ispisi.setText("NISTA OVO NE VALJA");
    }
    }


    public void btnClickHandler(View view) {

        if (view.getId() == R.id.btnConnect) {
            btIntent = new Intent(this, DeviceList.class);
            startActivityForResult(btIntent, 0);
        }
        else if (view.getId() == R.id.btnDisconnect) {
         //   connect.cancel();
            connectBtn.setVisibility(View.VISIBLE);
            disconnectBtn.setVisibility(View.GONE);
        }


    }
    public void Init()
    {
        connectBtn = (Button) findViewById(R.id.btnConnect);
        disconnectBtn = (Button) findViewById(R.id.btnDisconnect);
        disconnectBtn.setVisibility(View.GONE);


    }
//    public void mangeConnection(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0){
                int i;
                i = data.getIntExtra("result",0);
            if (resultCode == RESULT_OK){
                BluetoothDevice selectedDevice = null;
                selectedDevice = device;
             ConnectThread  connect = new ConnectThread(selectedDevice);
                connect.start();
            }
            if(resultCode == RESULT_CANCELED){

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
            if(mmSocket.isConnected()){

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "CONNECTED", Toast.LENGTH_SHORT).show();
                    }
                });
                //handler.obtainMessage(SUCCESS_CONNECTED, mmSocket).sendToTarget();
                Log.d(LOG_TAG,"STIGAO SAM DOVDE USPESNO");
            }
            else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "connection failed, pls try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

//    private class ConnectedThread extends Thread {
//
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//        private final BluetoothSocket mmSocket;
//
//
//        public ConnectedThread(BluetoothSocket Socket){
//
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//            mmSocket = Socket;
//
//            try {
//                tmpIn = Socket.getInputStream();
//                tmpOut = Socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//
//            byte[] buffer;
//            int bytes;
//
//            while (true) {
//
//                try {
//                    buffer = new byte[1024];
//                    bytes = mmInStream.read(buffer);
//                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    break;
//                }
//
//            }
//
//        }
//
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//        }
//    }

}
