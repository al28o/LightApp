package com.example.aloverfield.colorapp8_3_15;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by michaelzoller on 9/21/15.
 */
public class Device implements Parcelable {

    public DeviceType deviceType;
    public String name;
    public int imageID;

    /* whether or not this device is selected in the dashboard */
    public boolean selected = false;

    /* For debugging on an emulator, set to true */
    public boolean enabled = true;

    /* will be relevant later when we have multiple devices */
    public enum DeviceType{
        LIGHT
    }

    /* Constructor. Will need to change imageID when we have
        multiple devices. maybe use a switch case based on
        deviceType
     */
    public Device(DeviceType type, String title){
        deviceType = type;
        name = title;
        imageID= R.drawable.ic_brightness_low_white_48dp;
    }

    /*
        From here down: Methods to implement parcelable so we can send our
        array of devices from our activity to our fragment.
     */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(deviceType.toString());
        out.writeString(name);
        out.writeInt(imageID);
        if (selected){
            out.writeInt(1);
        }else{
            out.writeInt(-1);
        }
        if (enabled){
            out.writeInt(1);
        }else{
            out.writeInt(-1);
        }

    }

    public static final Parcelable.Creator<Device> CREATOR
            = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    private Device(Parcel in) {
        switch(in.readString()){
            case "LIGHT":
                deviceType = DeviceType.LIGHT;
                break;
            default:
                deviceType = DeviceType.LIGHT;
        }
        name = in.readString();
        imageID = in.readInt();

        if (in.readInt() > 0){
            selected = true;
        }else{
            selected = false;
        }

        if (in.readInt() > 0){
            enabled = true;
        }else{
            enabled = false;
        }
    }
}
