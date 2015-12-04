package home.pocetak;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final UUID MY_UUID = UUID.fromString("f00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sortiranje();
                Log.d(LOG_TAG, "OnClick");


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
//
//    public void startClickHandler(View view) {
//
//        Button dugme = (Button) view;
//        Log.d(LOG_TAG,"startClickHandler: " + dugme.getText());
//    }

    public void sortiranje() {
        EditText unesi = (EditText) findViewById(R.id.text2unesi);
        TextView ispisi = (TextView) findViewById(R.id.text3);
        String uneto = unesi.getText().toString();

        if (uneto.equalsIgnoreCase("NAPRED")) {

            ispisi.setText("IDEM NAPRED");
        } else if (uneto.equalsIgnoreCase("NAZAD")) {

            ispisi.setText("IDEM NAZAD");
        } else if (uneto.equalsIgnoreCase("LEVO")) {

            ispisi.setText("IDEM LEVO");
        } else if (uneto.equalsIgnoreCase("DESNO")) {

            ispisi.setText("IDEM DESNO");
        } else {

            ispisi.setText("NISTA OVO NE VALJA");
        }
    }


    public void btnClickHandler(View view) {
        Intent btIntent = new Intent(this, DeviceList.class);
        startActivity(btIntent);
    }
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//
//        public ConnectThread(BluetoothDevice device) {
//            // Use a temporary object that is later assigned to mmSocket,
//            // because mmSocket is final
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//
//            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) { }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it will slow down the connection
//           // mBluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect the device through the socket. This will block
//                // until it succeeds or throws an exception
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and get out
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) { }
//                return;
//            }
//
//            // Do work to manage the connection (in a separate thread)
//          //  manageConnectedSocket(mmSocket);
//        }
//
//        /** Will cancel an in-progress connection, and close the socket */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }


}