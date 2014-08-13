package com.walintukai.rubix;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class UnlockFragment extends Fragment implements OnClickListener {
	
	private TextView tvCurrentBox;
	private Button btnUnlock;
	
	static UnlockFragment newInstance() {
		UnlockFragment fragment = new UnlockFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unlock, container, false);
		tvCurrentBox = (TextView) view.findViewById(R.id.current_box);
		btnUnlock = (Button) view.findViewById(R.id.btn_unlock);
		
		tvCurrentBox.setText(R.string.select_box);
		
		tvCurrentBox.setOnClickListener(this);
		btnUnlock.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.current_box:
			((MainActivity) getActivity()).enableBluetooth();
			break;
			
		case R.id.btn_unlock:
			((MainActivity) getActivity()).sendPinInfo();
			break;
		}
	}
	
	public void setCurrentBoxText(BluetoothDevice device) {
		if (device != null && device.getName() != null) tvCurrentBox.setText(device.getName());
	}
	
}
