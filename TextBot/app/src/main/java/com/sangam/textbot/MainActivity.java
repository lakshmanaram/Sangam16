package com.sangam.textbot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;
    OutputStream mmOutStream;
    InputStream mmInStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mDevice = device;
                    Toast.makeText(getApplicationContext(), device.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            ConnectThread mConnectThread = new ConnectThread(mDevice);
            mConnectThread.start();
            Button button = (Button) findViewById(R.id.click);
            Button button1= (Button)findViewById(R.id.click1);
            Button button2= (Button)findViewById(R.id.click3);
            Button button3= (Button)findViewById(R.id.click4);
            Button button4= (Button)findViewById(R.id.click5);
            Button button5= (Button)findViewById(R.id.click5);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendDataToPairedDevice("1", mDevice);

                }


            });
    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            sendDataToPairedDevice("2", mDevice);
        }
    });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToPairedDevice("3", mDevice);
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToPairedDevice("4", mDevice);
                }
            });
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToPairedDevice("5", mDevice);
                }
            });
            button5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToPairedDevice("6", mDevice);
                }
            });


        }


    }

    private void sendDataToPairedDevice(String message, BluetoothDevice device) {
        byte[] toSend = message.getBytes();
        try {
            mmOutStream.write(toSend);
            // Your Data is sent to  BT connected paired device ENJOY.
        } catch (IOException e) {
            e.printStackTrace();
//Log.e(TAG, "Exception during write", e);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            switch (msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        // private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
                Log.i("hey","mmoutsocket there");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ConnectedThread mConnectedThread = new ConnectedThread();
            mConnectedThread.start();

        }

        public void cancel() {

        }
    }

    private class ConnectedThread extends Thread {
        public ConnectedThread() {

        }
        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    if(mmInStream!=null) {
                        bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                        for (int i = begin; i < bytes; i++) {
                            if (buffer[i] == "#".getBytes()[0]) {
                                mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                                begin = i + 1;
                                if (i == bytes - 1) {
                                    bytes = 0;
                                    begin = 0;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void cancel() {
        }

    }

}
