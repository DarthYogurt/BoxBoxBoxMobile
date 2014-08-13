package com.walintukai.rubix;

import java.util.ArrayList;
import java.util.List;

import com.walintukai.rubix.ConnectionService.LocalBinder;
import com.walintukai.rubix.ConnectionService.ServiceListener;
import com.walintukai.rubix.ViewPagerFragment.ViewPagerFragmentListener;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ConnectionEventListener, ServiceListener, ViewPagerFragmentListener {
	
	private static final long SCAN_TIME = 4000;
	private static final int RC_ENABLE_BT = 100;
	
	public ConnectionService mService;
	private BluetoothAdapter mBtAdapter;
	private List<Device> mDevices;
	private BtDevicesDialog mBtDevicesDialog;
	private Fragment mViewPagerFragment;

	ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();

			if (service != null) {
				Log.i("ConnectionService", "Connected");
				mService.setListener(MainActivity.this);
				mService.setOnServiceListener(MainActivity.this);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("ConnectionService", "Disconnected");
			mService = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDevices = new ArrayList<Device>();
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, ViewPagerFragment.newInstance(), 
					"viewPagerFragment").commit();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		startService();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		stopService();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean hasBLESupport() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.msg_ble_not_supported, Toast.LENGTH_SHORT).show();
			return false;
		}
		else return true;
	}
	
	public void enableBluetooth() {
		if (hasBLESupport()) {
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBtAdapter != null) {
				if (mBtAdapter.isEnabled()) {
					showBluetoothDevicesDialog();
				}
				else {
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, RC_ENABLE_BT);
				}
			}
			else 
				Toast.makeText(this, R.string.msg_bluetooth_not_available, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				if (mBtAdapter.isEnabled()) {
					showBluetoothDevicesDialog();
				}
			}
		}
	}
	
	public void showBluetoothDevicesDialog() {
		mBtDevicesDialog = BtDevicesDialog.newInstance();
		
		if (!mBtDevicesDialog.isVisible()) {
//			mBtDevicesDialog.setTargetFragment(this, RC_BT_DEVICES_DIALOG);
			mBtDevicesDialog.show(getSupportFragmentManager(), "btDevicesDialog");
		}		
	}
	
	private void startService() {
		Intent service = new Intent(this, ConnectionService.class);
		bindService(service, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void stopService() {
		unbindService(mConnection);
	}
	
	public void scanForDevices() {
		ViewPagerFragment viewPagerFragment = (ViewPagerFragment) getSupportFragmentManager().findFragmentByTag("viewPagerFragment");
		if (viewPagerFragment != null && viewPagerFragment.isVisible()) {
			viewPagerFragment.getCurrentFragment();
			
			if (mViewPagerFragment.getClass() == UnlockFragment.class) {
				new Thread() {
					@Override
					public void run() {
						if (mBtDevicesDialog != null) {
							mBtDevicesDialog.onSearchingStarted();
							mService.startScanDevice();
							
							try { Thread.sleep(SCAN_TIME); } 
							catch (InterruptedException e) { e.printStackTrace(); }
							
							mService.stopScanDevice();
							mBtDevicesDialog.onSearchingFinished();
						}
					}
					
				}.start();
			}
		}
	}
	
	public void connectToDevice(Device device) {
		if (mService != null) {
			mService.connectDevice(device.getAddress(), true);
		}
	}

	@Override
	public void onDeviceFound(String deviceAddress, String name, int rssi, int bondState, byte[] scanRecord, ParcelUuid[] uuids) {
		Log.v("Device Found", name + " " + deviceAddress);
		Device device = new Device(name, deviceAddress, bondState, rssi, scanRecord, uuids);
		mDevices.add(device);
		if (mBtDevicesDialog != null) {
			mBtDevicesDialog.updatedList(mDevices);
		}
	}

	@Override
	public void onDeviceRssiUpdate(String deviceAddress, int rssi, int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceConnectStateChange(String deviceAddress, int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceReadValue(int[] value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceCharacteristicFound() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectedToDevice(final BluetoothDevice device) {
		Log.e("onConnectedToDevice", "triggered");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (device != null && device.getName() != null && device.getAddress() != null) {
					Toast.makeText(MainActivity.this, "Connected to " + device.getName() + " " + 
							device.getAddress(), Toast.LENGTH_SHORT).show();
					if (mViewPagerFragment != null) {
						if (mViewPagerFragment.getClass() == UnlockFragment.class) {
							((UnlockFragment) mViewPagerFragment).setCurrentBoxText(device);
						}
					}
				}
			}
		});
	}

	@Override
	public void onSendCurrentFragment(Fragment fragment) {
		mViewPagerFragment = fragment;
	}
	
}
