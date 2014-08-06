package com.walintukai.rubix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class UnlockFragment extends Fragment implements OnClickListener {
	
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
		TextView currentBox = (TextView) view.findViewById(R.id.current_box);
		
		currentBox.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.current_box:
			((MainActivity) getActivity()).showBluetoothDevicesDialog();
			break;
		}
	}
	
}
