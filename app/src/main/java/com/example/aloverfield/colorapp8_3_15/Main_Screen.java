package com.example.aloverfield.colorapp8_3_15;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class Main_Screen extends ActionBarActivity implements
        LightActionFragment.OnLightValueChangedListener, DashboardFragment.OnDevicesSelectedChangedListener{

    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID


    private static final String TAG = "ColorApp";
    private int mMaxChars = 50000;//Default
    private BluetoothSocket mBTSocket;
    private BluetoothSocket mBTSocket2;
    private BluetoothSocket mBTSocket3;
    private BluetoothSocket mBTSocket4;

    private boolean mIsUserInitiatedDisconnect = false;

    /* Request codes for things like enabling BT and connecting to a BT device */
    private int REQUEST_ENABLE_BT = 1;
    private int REQUEST_BT_DEVICE = 9;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice btDevice;

    private boolean mIsBluetoothConnected = false;
    private boolean mIsBluetoothConnected2 = false;
    private boolean mIsBluetoothConnected3 = false;
    private boolean mIsBluetoothConnected4 = false;

    private ProgressDialog progressDialog;

    /* For now this is where we hard code our devices
        mDevices[0] is "connected" with mIsBluetoothConnected
        mDevices[1] is "connected" with mIsBluetoothConnected2
        ...

        See Device.java for attributes and methods
     */
    private Device[] mDevices = {
            new Device(Device.DeviceType.LIGHT, "Light 1"),
            new Device(Device.DeviceType.LIGHT, "Light 2"),
            new Device(Device.DeviceType.LIGHT, "Light 3"),
            new Device(Device.DeviceType.LIGHT, "Light 4")
    };

    /* Set to true on long click of one device to signal that we
        want to select more than one device
     */
    private boolean selectingMultiple = false;

    /* Used to avoid crashes on emulator or just check out the UI.
        Currently there should be checks in place to avoid crashes
        making this obsolete
     */
    private static boolean DEBUG = true;

    /* Save references to our fragments so we can call their class methods
        such as selecting a device or selecting a new color
     */
    private LightActionFragment actionFragment;
    private DashboardFragment dashboardFragment;

    /* Collection of devices that are currently selected */
    private List<Device> selectedDevices;

    /* Preferences for storing saved colors */
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        /* Add dashboard to the left fragment container */
        if (findViewById(R.id.left_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
             dashboardFragment = DashboardFragment.newInstance(mDevices);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.left_fragment_container, dashboardFragment).commit();
        }

        /* Add light action fragment to right container by default (master) */
        if (findViewById(R.id.right_fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
             actionFragment = new LightActionFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.right_fragment_container, actionFragment).commit();

        }

        /* Load color swatches */
        prefs = getSharedPreferences("prefs.lightapp", Context.MODE_PRIVATE);
        Log.d(TAG, "Got prefs");
        prefsEditor = prefs.edit();
        Log.d(TAG, "Got editor");


        /* Not sure if this is needed/wanted. I think it helps choose portrait vs landscape */
        ActivityHelper.initialize(this);



        /* Used for testing on the emulator. In product, set static variable DEBUG above to false */
        if (!DEBUG) {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_LONG).show();
            }else {
                /* if we do have BT support, try to enable it if not already. */
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }else{
                    /* Bluetooth is enabled so we want to select our device
                        TODO: Make sure this is actually what we want to do
                     */
                    Intent intent = new Intent(Main_Screen.this, BluetoothDevices.class);
                    startActivityForResult(intent, REQUEST_BT_DEVICE);
                }
            }
        }

        /* initiate a new list to track our selected devices */
        selectedDevices = new LinkedList<>();

        /* TODO: Check if we want/need this button. Might go in a settings page
        devices.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Screen.this, BluetoothDevices.class);
                startActivityForResult(intent, 1);
            }
        });*/



        /* TODO: Check if we need this. why would we need to disconnect?
        disconnectBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisConnectBT().execute();
            }
        });*/
    }


    @Override
    public void onFragmentViewLoaded(){
        loadSavedColors();
    }

    public void loadSavedColors(){

        int color;
        Log.d(TAG, "Loading colors");
        for (int i = 0; i < 6; i++) {


            if (prefs.contains("saved_color_" + Integer.toString(i))){
               color = prefs.getInt("saved_color_" + Integer.toString(i), Color.WHITE);
            }else{
                prefsEditor.putInt("saved_color_" + Integer.toString(i), Color.WHITE);
                color = Color.WHITE;
            }


            actionFragment.setSwatchColor(i,color);


        }

        prefsEditor.commit();

    }

    @Override
    public void onColorSaved(int color){
        int savedColor;
        for (int i = 0; i < 5; i++) {
            if (prefs.contains("saved_color_" + Integer.toString(i))){
                savedColor = prefs.getInt("saved_color_" + Integer.toString(i), Color.WHITE);
                prefsEditor.putInt("saved_color_" + Integer.toString(i+1), savedColor);
            }else{
                prefsEditor.putInt("saved_color_" + Integer.toString(i), Color.WHITE);
            }

        }
        prefsEditor.putInt("saved_color_0", color);
        prefsEditor.commit();

        loadSavedColors();

    }

    /*
        Method that receives a result code when activities that were launched from this
         activity finish with a result code. These activities are launched with
         startActivityForResult(...). Make sure to use static int devlared at top
         for request codes.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            /* If we just returned from enabling BT */
            if(resultCode == RESULT_OK){
                /* Everything was okay, we need to pick a device now */
                Intent intent = new Intent(Main_Screen.this, BluetoothDevices.class);
                startActivityForResult(intent, REQUEST_BT_DEVICE);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }else if (requestCode == REQUEST_BT_DEVICE){
            /* Just got back from picking a device */
            Log.d(TAG, "Picked device");
            if (resultCode == RESULT_OK){
                Log.d(TAG, "Result ok");
                btDevice = data.getParcelableExtra("btDevice");
                if (btDevice == null){
                    Log.d(TAG, "BTDevice null");
                }
                Log.d(TAG, "Launching connect");
                new ConnectBT().execute();
            }else{
                disableAllDevices();
            }
        }
    }

    /* method to disable communication to all devices because we couldnt get BT working */
    public void disableAllDevices(){
        for (int i = 0; i < mDevices.length; i++){
            mDevices[i].enabled = false;
        }
    }

    /* TODO: Decide if this is needed or not */
    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                if(mBTSocket != null) {
                    mBTSocket.close();
                }
                if(mBTSocket2 != null) {
                    mBTSocket2.close();
                }
                if(mBTSocket3 != null) {
                    mBTSocket3.close();
                }
                if(mBTSocket4 != null) {
                    mBTSocket4.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            mIsBluetoothConnected2 = false;
            mIsBluetoothConnected3 = false;
            mIsBluetoothConnected4 = false;

            mDevices[0].enabled = false;
            mDevices[1].enabled = false;
            mDevices[2].enabled = false;
            mDevices[3].enabled = false;
            /*
            lights1.setEnabled(false);
            lights2.setEnabled(false);
            lights3.setEnabled(false);
            lights4.setEnabled(false);*/
            //disconnectBT.setEnabled(false);
        }

    }

    @Override
    protected void onPause() {
        //if (mBTSocket != null && mIsBluetoothConnected) {
            //new DisConnectBT().execute();
        //}
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if ((mBTSocket == null || !mIsBluetoothConnected) && btDevice != null) {
            //new ConnectBT().execute();
        }
        else if ((mBTSocket2 == null || !mIsBluetoothConnected2) && btDevice != null) {
            //new ConnectBT().execute();
        }
        else if ((mBTSocket3 == null || !mIsBluetoothConnected3) && btDevice != null) {
           // new ConnectBT().execute();
        }
        else if ((mBTSocket4 == null || !mIsBluetoothConnected4) && btDevice != null) {
            //new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = false;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Main_Screen.this, "Hold on...", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    Log.d(TAG, "Connecting to 1");
                    if(btDevice != null) {
                        Log.d(TAG, "BTDevice not null");

                        mBTSocket = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        if (mBTSocket != null){
                            Log.d(TAG, "Connected to socket");
                            mConnectSuccessful = true;
                            mBTSocket.connect();
                        }else{
                            Log.d(TAG, "Connection unsuccesful");

                        }

                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    }
                }
                else if(mBTSocket2 == null || !mIsBluetoothConnected2) {
                    if(btDevice != null) {
                        Log.d(TAG, "Connecting to 2");

                        mBTSocket2 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        if (mBTSocket2 != null){
                            Log.d(TAG, "Connected to socket");
                            mConnectSuccessful = true;
                            mBTSocket2.connect();
                        }else{
                            Log.d(TAG, "Connection unsuccesful");

                        }
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    }
                }
                else if(mBTSocket3 == null || !mIsBluetoothConnected3) {
                    if (btDevice != null) {
                        Log.d(TAG, "Connecting to 3");

                        mBTSocket3 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        if (mBTSocket3 != null){
                            Log.d(TAG, "Connected to socket");
                            mConnectSuccessful = true;
                            mBTSocket3.connect();
                        }else{
                            Log.d(TAG, "Connection unsuccesful");

                        }
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    }
                }
                else if(mBTSocket4 == null || !mIsBluetoothConnected4) {
                    if (btDevice != null) {
                        Log.d(TAG, "Connecting to 4");

                        mBTSocket4 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        if (mBTSocket4 != null){
                            Log.d(TAG, "Connected to socket");
                            mConnectSuccessful = true;
                            mBTSocket4.connect();
                        }else{
                            Log.d(TAG, "Connection unsuccesful");

                        }
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    }
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Bluetooth Disconnected", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                Log.d(TAG, "PostExecute connection unsuccesful");
                progressDialog.cancel();
                progressDialog.dismiss();
                //finish();
            } else {
                Log.d(TAG, "Connected to device");

                Toast.makeText(getApplicationContext(), "Connected to device", Toast.LENGTH_SHORT).show();
                if(mBTSocket != null) {
                    mIsBluetoothConnected = true;
                    mDevices[0].enabled = true;
                }
                if(mBTSocket2 != null) {
                    mIsBluetoothConnected2 = true;
                    mDevices[1].enabled = true;
                }
                if(mBTSocket3 != null) {
                    mIsBluetoothConnected3 = true;
                    mDevices[2].enabled = true;
                }
                if(mBTSocket4 != null) {
                    mIsBluetoothConnected4 = true;
                    mDevices[3].enabled = true;
                }

               // disconnectBT.setEnabled(true);
            }

            progressDialog.dismiss();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }


    /* This is where we'll launch a settings activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect) {
            Intent intent = new Intent(Main_Screen.this, BluetoothDevices.class);
            startActivityForResult(intent, REQUEST_BT_DEVICE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Implemented method for LightActionFragment interface OnLightValueChangedListener
        param color - ARGB int value returned from the color wheel.
        affects - sends color to selected devices
     */
    @Override
    public void onColorChanged(int color){
        /* Colors received from colorpicker in ARGB so remove alpha */
        Log.d(TAG, "Color changed to: " + Integer.toString(color));
        color = (color & 0xFFFFFF);
        String sColor = Integer.toString(color) + ")";
        sendSignaltoDevices(sColor);

    }


    /* Implemented method for LightActionFragment interface OnLightValueChangedListener
            param brightness - int value from brightness slider (5-255)
            affects - sends brightness to selected devices
         */
    @Override
    public void onBrightnessChanged(int brightness){
        //Toast.makeText(this, "brightness: " + Integer.toString(brightness), Toast.LENGTH_SHORT).show();
        brightness = brightness / 25;
        brightness = brightness * 25;
            String sBrightness = Integer.toString(brightness) + "$";
            sendSignaltoDevices(sBrightness);

    }

    /* TODO: Mike isn't sure exactly what white temperature means...
        Implemented method for LightActionFragment interface OnLightValueChangedListener
            param whiteTemp -  int value returned from white temp slider (1-6).
            affects - sends white temp to selected devices
         */
    @Override
    public void onWhiteTemperatureChanged(int whiteTemp){
        //Toast.makeText(this, "whiteTemp: " + Integer.toString(whiteTemp), Toast.LENGTH_SHORT).show();

        /* Validation to make sure we dont send crazy numbers */
        if (whiteTemp > 6){
            whiteTemp = 6;
        }else if (whiteTemp < 1){
            whiteTemp = 1;
        }
        String sWhiteTemp = Integer.toString(whiteTemp) + "?";
        sendSignaltoDevices(sWhiteTemp);
    }


    /* generic method to send a formatted sting to all currently selected devices
        param signal - string signal to send to devices. pre formatted
        affects - sends bytes to attached sockets
     */
    public void sendSignaltoDevices(String signal){
        /* to prevent crashes on emulator... */
        if (!DEBUG) {
        /* better way to do this but I don't want to mess up the sockets */
            if (mDevices[0].selected && mDevices[0].enabled) {
                try {
                    mBTSocket.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mDevices[1].selected && mDevices[1].enabled) {
                try {
                    mBTSocket2.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mDevices[2].selected && mDevices[2].enabled) {
                try {
                    mBTSocket3.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mDevices[3].selected && mDevices[3].enabled) {
                try {
                    mBTSocket4.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            if (mDevices[0].selected && mDevices[0].enabled) {
                Log.d("Signal", "Sending: " + signal + " to " + mDevices[0].name);
            }
            if (mDevices[1].selected && mDevices[1].enabled) {
                Log.d("Signal", "Sending: " + signal + " to " + mDevices[1].name);
            }
            if (mDevices[2].selected && mDevices[2].enabled) {
                Log.d("Signal", "Sending: " + signal + " to " + mDevices[2].name);
            }
            if (mDevices[3].selected && mDevices[3].enabled) {
                Log.d("Signal", "Sending: " + signal + " to " + mDevices[3].name);
            }
        }
    }
    /* Implemented method for DashboardFragment interface OnDevicesSelectedChangedListener
            param pos - device position in gridview. used to select device from mDevices
            updates - dashboard fragment title, selection in gridview
         */
    @Override
    public void onDeviceAdded(int pos){
        if (selectingMultiple){
            selectDevice(pos);
            actionFragment.setFragmentTitle(Integer.toString(selectedDevices.size()) + " Devices Selected");
        }else{
            deselectAllDevices();
            selectDevice(pos);
            actionFragment.setFragmentTitle(mDevices[pos].name + " Selected");
        }

        //Toast.makeText(this, "Device added: " + mDevices[pos].name +" - Selected: " + Integer.toString(selectedDevices.size()), Toast.LENGTH_SHORT).show();

    }

    /* Implemented method for DashboardFragment interface OnDevicesSelectedChangedListener
            param pos - device position in gridview. used to select device from mDevices
            updates - dashboard fragment title, selection in gridview
         */
    @Override
    public void onDeviceRemoved(int pos){
        deselectDevice(pos);
        if (selectedDevices.size()==0){
           selectingMultiple = false;
           actionFragment.setFragmentTitle("No Devices Selected");
        }else{
            actionFragment.setFragmentTitle(Integer.toString(selectedDevices.size()) + " Devices Selected");
        }
        //Toast.makeText(this, "Device removed: " + mDevices[pos].name+" - Selected: " + Integer.toString(selectedDevices.size()), Toast.LENGTH_SHORT).show();

    }

    /* Implemented method for DashboardFragment interface OnDevicesSelectedChangedListener
            param pos - device position in gridview. used to select device from mDevices
            updates - if device is long clicked, we want to select multiple
         */
    @Override
    public void onDeviceLongClick(int pos){
        //Toast.makeText(this, "Device long click: " + mDevices[pos].name, Toast.LENGTH_SHORT).show();
        deselectAllDevices();
        selectDevice(pos);
        selectingMultiple = true;
        actionFragment.setFragmentTitle("1 Device Selected");

    }

    /* Method to handle all things involved with selecting a device including updating
        its selected status, adding it to selectedDevices, and updating the item background
        in the gridview
            param pos - device position in gridview. used to select device from mDevices
            updates - selection in gridview, selectedDevices
         */
    public void selectDevice(int pos){
        mDevices[pos].selected = true;
        selectedDevices.add(mDevices[pos]);
        dashboardFragment.selectDeviceView(pos);
    }

    /* Method to handle all things involved with deselecting a device including updating
        its selected status, adding it to selectedDevices, and updating the item background
        in the gridview
            param pos - device position in gridview. used to select device from mDevices
            updates - selection in gridview, selectedDevices
         */
    public void deselectDevice(int pos){
        mDevices[pos].selected = false;
        selectedDevices.remove(mDevices[pos]);
        dashboardFragment.deselectDeviceView(pos);
    }

    /* Method to deselect all devices. */
    public void deselectAllDevices(){
        for (int i = 0; i < mDevices.length; i++){
            if (mDevices[i].selected){
                deselectDevice(i);
            }
        }
    }
}
