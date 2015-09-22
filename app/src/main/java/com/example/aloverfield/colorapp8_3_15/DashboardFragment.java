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


public class DashboardFragment extends Fragment {

    private OnDevicesSelectedChangedListener mCallback;

    public interface OnDevicesSelectedChangedListener {
        public void onDeviceAdded(Device device);
        public void onDeviceRemoved(Device device);
    }

    private GridView gridView;
    // references to our devices


    private Device[] mDevices;

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


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long length) {
                /* get initial view padding. Setting new background resets padding. */
                int t = view.getPaddingTop();
                int b = view.getPaddingBottom();
                int l = view.getPaddingLeft();
                int r = view.getPaddingRight();
                Device device = mDevices[i];

                if (device.enabled){
                    /* set background based on if it is selected or unselected */
                    device.selected = !device.selected;
                    if (device.selected) {
                        view.setBackgroundResource(R.drawable.selected_gridview_item);
                        mCallback.onDeviceAdded(device);

                    }else{
                        view.setBackgroundResource(R.drawable.unselected_gridview_item);
                        mCallback.onDeviceRemoved(device);
                    }
                    view.setPadding(l,t,r,b);
                }else{
                    Toast.makeText(view.getContext(), device.name + " is disabled.", Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

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
