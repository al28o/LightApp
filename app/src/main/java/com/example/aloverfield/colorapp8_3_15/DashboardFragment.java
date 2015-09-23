package com.example.aloverfield.colorapp8_3_15;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
    This fragment acts as our "dashboard" where we can
    view all connected devices and click them in order to
    bring up their related actions and settings in the
    ActionFragment.
 */
public class DashboardFragment extends Fragment {

    private OnDevicesSelectedChangedListener mCallback;

    /* interface to allow callbacks and handling from MainActivity */
    public interface OnDevicesSelectedChangedListener {
        /* called everytime an unselected gridview item is clicked */
        public void onDeviceAdded(int pos);
        /* called everytime a selected gridview item is clicked */
        public void onDeviceRemoved(int pos);
        /* called everytime a gridview item is longclicked */
        public void onDeviceLongClick(int pos);
    }

    private GridView gridView;

    /* array of devices to show in the grid */
    private Device[] mDevices;

    /* Constructor to allow us to receive devices array from calling activity */
    public static DashboardFragment newInstance(Device[] devices){
        DashboardFragment f = new DashboardFragment();
        Bundle args = new Bundle();
        args.putParcelableArray("devices", devices);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        mDevices = (Device[]) args.getParcelableArray("devices");

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        gridView = (GridView) view.findViewById(R.id.device_gridview);
        gridView.setAdapter(new DeviceAdapter(view.getContext()));


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDevices[i].enabled) {
                    /* let MainActivity handle long clicks */
                    mCallback.onDeviceLongClick(i);
                }else{
                    /* If this device isn't enabled, tell the user */
                    Toast.makeText(view.getContext(), mDevices[i].name + " is disabled.", Toast.LENGTH_LONG).show();
                }
               return true;
            }
        });
        /* handle when an item in the gridview is clicked */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long length) {

                Device device = mDevices[i];

                if (device.enabled){
                    /* set background based on if it is selected or unselected */
                    device.selected = !device.selected;
                    if (device.selected) {
                        /* let MainActivity handle a device being added */
                        mCallback.onDeviceAdded(i);

                    }else{
                        /* let MainActivity handle a device being removed */
                        mCallback.onDeviceRemoved(i);
                    }

                }else{
                    /* If this device isn't enabled, tell the user */
                    Toast.makeText(view.getContext(), device.name + " is disabled.", Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;

    }

    /* public method (accessible from MainActivity) to set the correct
        view resources for a selected device.
     */
    public void selectDeviceView(int pos){
        View view = gridView.getChildAt(pos);

        /* get initial view padding. Setting new background resets padding. */
        int t = view.getPaddingTop();
        int b = view.getPaddingBottom();
        int l = view.getPaddingLeft();
        int r = view.getPaddingRight();

        /* set new background */
        view.setBackgroundResource(R.drawable.selected_gridview_item);

        /* set saved padding */
        view.setPadding(l,t,r,b);
    }

    /* public method (accessible from MainActivity) to set the correct
        view resources for an uselected device.
     */
    public void deselectDeviceView(int pos){
        View view = gridView.getChildAt(pos);

        /* get initial view padding. Setting new background resets padding. */
        int t = view.getPaddingTop();
        int b = view.getPaddingBottom();
        int l = view.getPaddingLeft();
        int r = view.getPaddingRight();

         /* set new background */
        view.setBackgroundResource(R.drawable.unselected_gridview_item);

        /* set saved padding */
        view.setPadding(l,t,r,b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    /* Adapter class to handle our custom gridview. Allows
        us to set custom views for the items.
     */
    public class DeviceAdapter extends BaseAdapter{
        private Context mContext;

        public DeviceAdapter(Context context){
            mContext = context;
        }

        public int getCount() {
            return mDevices.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.device_gridview_item, null);

            TextView textView = (TextView) v.findViewById(R.id.device_gridview_name);
            textView.setText(mDevices[position].name);

            ImageView imageView = (ImageView) v.findViewById(R.id.device_gridview_image);
            imageView.setImageResource(mDevices[position].imageID);


            return v;
        }



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
            mCallback = (OnDevicesSelectedChangedListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement OnLightValueChangedListener.");
        }
    }



}
