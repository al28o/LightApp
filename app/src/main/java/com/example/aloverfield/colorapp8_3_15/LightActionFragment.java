package com.example.aloverfield.colorapp8_3_15;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/*
    This fragment is an ActionFragment aka a view that holds
    all of the controls for a certain type of device. Eventually,
    before we add more devices, should create interface/class ActionFragment
    that LightActionFragment and other {Device}ActionFragments will implement/extend.
 */
public class LightActionFragment extends Fragment {

    private OnLightValueChangedListener mCallback;

    /* interface that allows communication and callbacks between
        this and the main activity.
     */
    public interface OnLightValueChangedListener {
        /* called every time the color wheel slider is moved */
        public void onColorChanged(int color);
        /* called every time the brightness slider is moved */
        public void onBrightnessChanged(int brightness);
        /* called every time the wnite temp slider is moved */
        public void onWhiteTemperatureChanged(int whiteTemp);
        /* called when we save a new color */
        public void onColorSaved(int color);

        /* Called when fragment view inflated so main can update it (color swatches) */
        public void onFragmentViewLoaded();

    }

    /* Title of the fragment that reflects the devices selected */
    private TextView titleView;

    /* "On/Off Switch" that just sets the brightness slider to 0 when
        turned off and to savedBrightness when turned on.
     */
    private Switch onOffSwitch;
    private Button saveColorButton;
    private ImageView savedColor1;

    private View view;



    private int savedBrightness = 205;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_light_action, container, false);

        titleView = (TextView) view.findViewById(R.id.actionDeviceTitle);

        mCallback.onFragmentViewLoaded();

        final ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.colorPicker);

        colorPicker.setShowOldCenterColor(false);
        colorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                /* Let MainActivity handle what to do */
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
                /* Let MainActivity handle what to do */
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
                /* Let MainActivity handle what to do */
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

        saveColorButton = (Button) view.findViewById(R.id.saveColorButton);
        saveColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mCallback.onColorSaved(colorPicker.getColor());

            }
        });


        for (int i = 0;i < colorImagesViews.length;i++){
            final ImageView iv = (ImageView) view.findViewById(colorImagesViews[i]);
            final int p = i;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorPicker.setColor(swatchColors[p]);
                    mCallback.onColorChanged(swatchColors[p]);
                }
            });
        }

        return view;

    }


    /*
        When the calling activity adds this fragment to its layout,
        assign the implemented callbacks from the calling activity
        so that we can call them in this fragment.
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mCallback = (OnLightValueChangedListener) activity;

        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement OnLightValueChangedListener.");
        }
    }

    /* Public method (callable from MainActivity) to set the title
        of this fragment based on devices selected
     */
    public void setFragmentTitle(String title){
        titleView.setText(title);
    }


    public int[] colorImagesViews = {
            R.id.savedColorImageView1,
            R.id.savedColorImageView2,
            R.id.savedColorImageView3,
            R.id.savedColorImageView4,
            R.id.savedColorImageView5,
            R.id.savedColorImageView6,
    };

    public int[] swatchColors = { Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};

    public void setSwatchColor(int i, int color){
        Log.d("SET", "1");
        ImageView iv = (ImageView) view.findViewById(colorImagesViews[i]);
        Log.d("SET", "2");

        Drawable bg = iv.getBackground();
        Log.d("SET", "3");

        bg.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        Log.d("SET", "4");

        iv.setBackgroundDrawable(bg);
        Log.d("SET", "5");
        swatchColors[i] = color;

    }
}
