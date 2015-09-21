package com.example.aloverfield.colorapp8_3_15;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class Main_Screen extends Activity implements Colors.colorChangedListener{

    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID


    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private BluetoothSocket mBTSocket;
    private BluetoothSocket mBTSocket2;
    private BluetoothSocket mBTSocket3;
    private BluetoothSocket mBTSocket4;

    private boolean mIsUserInitiatedDisconnect = false;

    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice btDevice;
    private boolean bStop = false;


    private boolean mIsBluetoothConnected = false;
    private boolean mIsBluetoothConnected2 = false;
    private boolean mIsBluetoothConnected3 = false;
    private boolean mIsBluetoothConnected4 = false;
    private boolean sendToLights1 = false;
    private boolean sendToLights2 = false;
    private boolean sendToLights3 = false;
    private boolean sendToLights4 = false;
    private ProgressDialog progressDialog;

    private SeekBar brightnessBar;

    private Button redButton;
    private Button lightRedButton;
    private Button orangeButton;
    private Button greenButton;
    private Button lightGreenButton;
    private Button yellowButton;
    private Button blueButton;
    private Button lightBlueButton;
    private Button purpleButton;
    private Button blackButton;
    private Button whiteButton;
    private Button sunsetButton;
    private Button disconnectBT;
    private Button devices;
    private Button colorWheel;
    private Button lights1;
    private Button lights2;
    private Button lights3;
    private Button lights4;
    private Button brightnessDown;
    private Button brightnessUp;
    private Button whiteTemp1;
    private Button whiteTemp2;
    private Button whiteTemp3;
    private Button whiteTemp4;
    private Button whiteTemp5;
    private Button whiteTemp6;

    private int color;
    private String color2;
    private int colorWheelColorPicked;
    private String brightnessSetting;
    private int whiteTemp;
    private String whiteTempStr;
    private int brightness;
    private final int brightnessMin = 5;
    private final int brightnessMax = 255;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_screen);
        setContentView(R.layout.activity_main2);

        ActivityHelper.initialize(this);
        this.activity = this;

        brightness = brightnessMax;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_LONG).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
//
//        AlertDialog.Builder adb = new AlertDialog.Builder(this);
//        Dialog d = adb.setView(new View(this)).create();
//        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(d.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        d.show();
//        d.getWindow().setAttributes(lp);


        redButton = (Button) findViewById(R.id.redButton);
        lightRedButton = (Button) findViewById(R.id.lightRedButton);
        orangeButton = (Button) findViewById(R.id.orangeButton);
        greenButton = (Button) findViewById(R.id.greenButton);
        lightGreenButton = (Button) findViewById(R.id.lightGreenButton);
        yellowButton = (Button) findViewById(R.id.yellowButton);
        blueButton = (Button) findViewById(R.id.blueButton);
        lightBlueButton = (Button) findViewById(R.id.lightBlueButton);
        purpleButton = (Button) findViewById(R.id.purpleButton);
        blackButton = (Button) findViewById(R.id.blackButton);
        whiteButton = (Button) findViewById(R.id.whiteButton);
        sunsetButton = (Button) findViewById(R.id.sunsetButton);
        disconnectBT = (Button) findViewById(R.id.disconnectBT);
        devices = (Button) findViewById(R.id.devices);
        colorWheel = (Button) findViewById(R.id.colorWheel);
        lights1 = (Button) findViewById(R.id.lights1);
        lights2 = (Button) findViewById(R.id.lights2);
        lights3 = (Button) findViewById(R.id.lights3);
        lights4 = (Button) findViewById(R.id.lights4);
        brightnessBar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brightnessUp = (Button) findViewById(R.id.brightnessUp);
        brightnessDown = (Button) findViewById(R.id.brightnessDown);
        whiteTemp1 = (Button) findViewById(R.id.whiteTemp1);
        whiteTemp2 = (Button) findViewById(R.id.whiteTemp2);
        whiteTemp3 = (Button) findViewById(R.id.whiteTemp3);
        whiteTemp4 = (Button) findViewById(R.id.whiteTemp4);
        whiteTemp5 = (Button) findViewById(R.id.whiteTemp5);
        whiteTemp6 = (Button) findViewById(R.id.whiteTemp6);

        brightnessBar.setProgress(5);
        brightnessBar.incrementProgressBy(25);
        brightnessBar.setMax(255);

        devices.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Screen.this, BluetoothDevices.class);
                startActivityForResult(intent, 1);
            }
        });

        whiteTemp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 1;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteTemp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 2;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteTemp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 3;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteTemp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 4;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteTemp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 5;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteTemp6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteTemp = 6;
                whiteTempStr = Integer.toString(whiteTemp) + "?";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(whiteTempStr.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 25;
                progress = progress * 25;
                brightnessSetting = Integer.toString(progress) + "$";
                if (sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(brightnessSetting.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(brightnessSetting.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(brightnessSetting.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(brightnessSetting.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightnessUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((brightness + 50) < brightnessMax) {

                    brightness = brightness + 50;
                    brightnessSetting = Integer.toString(brightness) + "$";
                    if (sendToLights1) {
                        try {
                            mBTSocket.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights2) {
                        try {
                            mBTSocket2.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights3) {
                        try {
                            mBTSocket3.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights4) {
                        try {
                            mBTSocket4.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        brightnessDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((brightness - 50) > brightnessMin) {

                    brightness = brightness - 50;
                    brightnessSetting = Integer.toString(brightness) + "$";
                    if (sendToLights1) {
                        try {
                            mBTSocket.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights2) {
                        try {
                            mBTSocket2.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights3) {
                        try {
                            mBTSocket3.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sendToLights4) {
                        try {
                            mBTSocket4.getOutputStream().write(brightnessSetting.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xff0000;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xffaa00;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lightRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xff5577;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0x00ff00;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lightGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xaaffaa;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xffff00;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0x0000ff;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lightBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0x00aaff;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        purpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xaa00ff;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        whiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0xffffff;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sunsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 1;
                color2 = Integer.toString(color) + "!";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = 0x000000;
                color2 = Integer.toString(color) + ")";
                if(sendToLights1) {
                    try {
                        mBTSocket.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights2) {
                    try {
                        mBTSocket2.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights3) {
                    try {
                        mBTSocket3.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sendToLights4) {
                    try {
                        mBTSocket4.getOutputStream().write(color2.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        disconnectBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DisConnectBT().execute();
            }
        });

        lights1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sendToLights1) {
                    sendToLights1 = true;
                    Toast.makeText(getApplicationContext(), "Lights1 is on", Toast.LENGTH_LONG).show();
                }
                else {
                    sendToLights1 = false;
                    Toast.makeText(getApplicationContext(), "Lights1 is off", Toast.LENGTH_LONG).show();
                }
            }
        });

        lights2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sendToLights2) {
                    sendToLights2 = true;
                    Toast.makeText(getApplicationContext(), "Lights2 is on", Toast.LENGTH_LONG).show();
                }
                else {
                    sendToLights2 = false;
                    Toast.makeText(getApplicationContext(), "Lights2 is off", Toast.LENGTH_LONG).show();
                }
            }
        });

        lights3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sendToLights3) {
                    sendToLights3 = true;
                    Toast.makeText(getApplicationContext(), "Lights3 is on", Toast.LENGTH_LONG).show();
                }
                else {
                    sendToLights3 = false;
                    Toast.makeText(getApplicationContext(), "Lights3 is off", Toast.LENGTH_LONG).show();
                }
            }
        });

        lights4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sendToLights4) {
                    sendToLights4 = true;
                    Toast.makeText(getApplicationContext(), "Lights4 is on", Toast.LENGTH_LONG).show();
                }
                else {
                    sendToLights4 = false;
                    Toast.makeText(getApplicationContext(), "Lights4 is off", Toast.LENGTH_LONG).show();
                }
            }
        });

        colorWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Colors(activity, Main_Screen.this, Color.WHITE).show();
            }
        });
    }

    public void colorWheelColor(int colorWheel) {
        colorWheelColorPicked = colorWheel;
        color2 = Integer.toString(colorWheelColorPicked) + ")";
        if(sendToLights1) {
            try {
                mBTSocket.getOutputStream().write(color2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sendToLights2) {
            try {
                mBTSocket2.getOutputStream().write(color2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sendToLights3) {
            try {
                mBTSocket3.getOutputStream().write(color2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sendToLights4) {
            try {
                mBTSocket4.getOutputStream().write(color2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                btDevice = data.getParcelableExtra("btDevice");
                //new ConnectBT().execute();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


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
            sendToLights1 = false;
            sendToLights2 = false;
            mIsBluetoothConnected3 = false;
            mIsBluetoothConnected4 = false;
            sendToLights3 = false;
            sendToLights4 = false;
            lights1.setEnabled(false);
            lights2.setEnabled(false);
            lights3.setEnabled(false);
            lights4.setEnabled(false);
            disconnectBT.setEnabled(false);
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
            new ConnectBT().execute();
        }
        else if ((mBTSocket2 == null || !mIsBluetoothConnected2) && btDevice != null) {
            new ConnectBT().execute();
        }
        else if ((mBTSocket3 == null || !mIsBluetoothConnected3) && btDevice != null) {
            new ConnectBT().execute();
        }
        else if ((mBTSocket4 == null || !mIsBluetoothConnected4) && btDevice != null) {
            new ConnectBT().execute();
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
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Main_Screen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    if(btDevice != null) {
                        mBTSocket = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        mBTSocket.connect();
                    }
                }
                else if(mBTSocket2 == null || !mIsBluetoothConnected2) {
                    if(btDevice != null) {
                        mBTSocket2 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        mBTSocket2.connect();
                    }
                }
                else if(mBTSocket3 == null || !mIsBluetoothConnected3) {
                    if (btDevice != null) {
                        mBTSocket3 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        mBTSocket3.connect();
                    }
                }
                else if(mBTSocket4 == null || !mIsBluetoothConnected4) {
                    if (btDevice != null) {
                        mBTSocket4 = btDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        mBTSocket4.connect();
                    }
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Bluetooth Disconnected", Toast.LENGTH_LONG).show();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                //finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connected to device", Toast.LENGTH_SHORT).show();
                if(mBTSocket != null) {
                    mIsBluetoothConnected = true;
                    sendToLights1 = true;
                    lights1.setEnabled(true);
                }
                if(mBTSocket2 != null) {
                    mIsBluetoothConnected2 = true;
                    sendToLights2 = true;
                    lights2.setEnabled(true);
                }
                if(mBTSocket3 != null) {
                    mIsBluetoothConnected3 = true;
                    sendToLights3 = true;
                    lights3.setEnabled(true);
                }
                if(mBTSocket4 != null) {
                    mIsBluetoothConnected4 = true;
                    sendToLights4 = true;
                    lights4.setEnabled(true);
                }
                redButton.setEnabled(true);
                lightRedButton.setEnabled(true);
                orangeButton.setEnabled(true);
                greenButton.setEnabled(true);
                lightGreenButton.setEnabled(true);
                yellowButton.setEnabled(true);
                blueButton.setEnabled(true);
                lightBlueButton.setEnabled(true);
                purpleButton.setEnabled(true);
                blackButton.setEnabled(true);
                sunsetButton.setEnabled(true);
                whiteButton.setEnabled(true);
                disconnectBT.setEnabled(true);
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

    Activity activity;

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
