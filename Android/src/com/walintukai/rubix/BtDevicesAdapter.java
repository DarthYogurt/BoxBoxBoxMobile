package com.walintukai.rubix;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BtDevicesAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Device> mDevices;
	private LayoutInflater mInflater;
	
	public BtDevicesAdapter(Context context, List<Device> devices) {
		this.mContext = context;
		this.mDevices = devices;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return mDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {
		private TextView tvDeviceName;
		private TextView tvMacAddress;
		private TextView tvSignalStrength;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
            holder = new ViewHolder();
            	
        	convertView = mInflater.inflate(R.layout.row_bt_device, null);
        	holder.tvDeviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.tvMacAddress = (TextView) convertView.findViewById(R.id.mac_address);
            holder.tvSignalStrength = (TextView) convertView.findViewById(R.id.signal_strength);
   
            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder) convertView.getTag();
        }
		
    	Device device = (Device) getItem(position);
		if (device != null) {
			if (device.getName() != null) holder.tvDeviceName.setText(device.getName());
			if (device.getAddress() != null) holder.tvMacAddress.setText(device.getAddress());
			holder.tvSignalStrength.setText(Integer.toString(device.getRssi()));
		}
		
        return convertView;
	}

}
