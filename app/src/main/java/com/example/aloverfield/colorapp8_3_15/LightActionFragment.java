package com.example.aloverfield.colorapp8_3_15;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class LightActionFragment extends Fragment {

    private OnLightValueChangedListener mCallback;

    public interface OnLightValueChangedListener {
        public void onColorChanged(int color);
        public void onBrightnessChanged(int brightness);
        public void onWhiteTemperatureChanged(int whiteTemp);
    }

    private TextView titleView;
    private Switch onOffSwitch;
    private int savedBrightness = 205;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_action, container, false);

        titleView = (TextView) view.findViewById(R.id.actionDeviceTitle);

        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.colorPicker);


        colorPicker.setShowOldCenterColor(false);
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                // colors -2304 to  -2227969 ish
                mCallback.onColorChanged(color);

            }
        });

        final SeekBar brightnessBar = (SeekBar) view.findViewById(R.id.brightnessBar);
        brightnessBar.setProgress(205);
        brightnessBar.incrementProgressBy(25);
        brightnessBar.setMax(255);
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCallback.onBrightnessChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar whiteTemperatureBar = (SeekBar) view.findViewById(R.id.whiteTempBar);
        whiteTemperatureBar.setProgress(6);
        whiteTemperatureBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // values 0 to 100
                mCallback.onWhiteTemperatureChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        onOffSwitch = (Switch) view.findViewById(R.id.onOffSwitch);

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    savedBrightness = brightnessBar.getProgress();
                    brightnessBar.setProgress(5);
                }else{
                    brightnessBar.setProgress(savedBrightness);
                }
            }
        });

        return view;

    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mCallback = (OnLightValueChangedListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement OnLightValueChangedListener.");
        }
    }

    public void setFragmentTitle(String title){
        titleView.setText(title);
    }



}