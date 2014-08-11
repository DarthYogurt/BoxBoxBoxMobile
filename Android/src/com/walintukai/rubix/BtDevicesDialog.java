package com.walintukai.rubix;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BtDevicesDialog extends DialogFragment {
	
	private List<Device> mDevices;
	private BtDevicesDialogListener mBtDevicesDialogListener;
	private BtDevicesAdapter mAdapter;
	private ProgressBar spinner;
	private TextView tvBtnText;
	
	static BtDevicesDialog newInstance() {
		BtDevicesDialog fragment = new BtDevicesDialog();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtDevicesDialogListener = (BtDevicesDialogListener) getTargetFragment();
		mDevices = new ArrayList<Device>();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
		dialog.setContentView(R.layout.dialog_bt_devices);
		
		ListView listView = (ListView) dialog.findViewById(R.id.list_devices);
		RelativeLayout btnSearchDevices = (RelativeLayout) dialog.findViewById(R.id.btn_search_devices);
		spinner = (ProgressBar) dialog.findViewById(R.id.spinner);
		tvBtnText = (TextView) dialog.findViewById(R.id.btn_text);
		
		mAdapter = new BtDevicesAdapter(getActivity(), mDevices);
		listView.setAdapter(mAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismiss();
				((MainActivity) getActivity()).connectToDevice(mDevices.get(position));
			}
		});
		
		btnSearchDevices.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDevices.clear();
				((MainActivity) getActivity()).scanForDevices();
			}
		});
		
		((MainActivity) getActivity()).scanForDevices();
		
		return dialog;
	}
	
	public interface BtDevicesDialogListener {
		public void connectToDevice(Device device);
	}
	
	public void onSearchingStarted() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				spinner.setVisibility(View.VISIBLE);
				tvBtnText.setText(R.string.searching);
			}
		});
	}
	
	public void onSearchingFinished() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				spinner.setVisibility(View.GONE);
				tvBtnText.setText(R.string.search_for_devices);
			}
		});
	}
	
	public void updatedList(final List<Device> devices) {
		mDevices = devices;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mAdapter.updateList(devices);
			}
		});	
	}
	
}
