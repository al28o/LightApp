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
    public boolean selected = false;
    public boolean enabled = false;

    public enum DeviceType{
        LIGHT
    }

    public Device(DeviceType type, String title){
        deviceType = type;
        name = title;
        imageID= R.drawable.ic_brightness_low_white_48dp;
    }

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
