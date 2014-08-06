package com.walintukai.rubix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ConnectionService extends Service {

	static final String TAG = ConnectionService.class.getName();

	public static final UUID RBL_SERVICE = UUID.fromString("713D0000-503E-4C75-BA94-3148F18D941E");
	public static final UUID RBL_DEVICE_RX_UUID = UUID.fromString("713D0002-503E-4C75-BA94-3148F18D941E");
	public static final UUID RBL_DEVICE_TX_UUID = UUID.fromString("713D0003-503E-4C75-BA94-3148F18D941E");
	public static final UUID CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID SERIAL_NUMBER_STRING = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");

	private final IBinder mBinder = new LocalBinder();
	private ServiceListener mServiceListener;
	private ConnectionEventListener connectionEventListener;
	private BluetoothAdapter mBtAdapter = null;
	public BluetoothGatt mBluetoothGatt = null;
	HashMap<String, BluetoothDevice> mDevices = null;
	private BluetoothGattCharacteristic txCharc = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public ConnectionService getService() {
			return ConnectionService.this;
		}
	}
	
	public interface ServiceListener {
		public void onConnectedToDevice(BluetoothDevice device);
	}
	
	public void setOnServiceListener(ServiceListener serviceListener){
	    mServiceListener = serviceListener;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBtAdapter = bluetoothManager.getAdapter();

		if (mBtAdapter == null) 
			return;

		if (mDevices == null) 
			mDevices = new HashMap<String, BluetoothDevice>();
	}
	
	@Override
	public void onDestroy() {
		if (mBluetoothGatt == null)
			return;

		mBluetoothGatt.close();
		mBluetoothGatt = null;

		super.onDestroy();
	}
	
	public void setListener(ConnectionEventListener mListener) {
		connectionEventListener = mListener;
	}

	public void startScanDevice() {
		if (mDevices != null) mDevices.clear();
		else mDevices = new HashMap<String, BluetoothDevice>();

		startScanDevices();
	}
	
	private void startScanDevices() {
		if (mBtAdapter == null)
			return;

		mBtAdapter.startLeScan(mLeScanCallback);
	}

	public void stopScanDevice() {
		stopScanDevices();
	}
	
	protected void stopScanDevices() {
		if (mBtAdapter == null)
			return;

		mBtAdapter.stopLeScan(mLeScanCallback);
	}

	public boolean isBLEDevice(String address) {
		BluetoothDevice mBluetoothDevice = mDevices.get(address);
		if (mBluetoothDevice != null) {
			return isBLEDevice(address);
		}
		return false;
	}
	
	public boolean isBLEDevice(BluetoothDevice device) {
		if (mBluetoothGatt != null) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	void addDevice(BluetoothDevice mDevice) {
		String address = mDevice.getAddress();
		mDevices.put(address, mDevice);
	}

	public void connectDevice(String address, boolean autoconnect) {
		BluetoothDevice mBluetoothDevice = mDevices.get(address);
		if (mBluetoothDevice != null) {
			connect(mBluetoothDevice, autoconnect);
		}
	}
	
	protected void connect(BluetoothDevice device, boolean autoconnect) {
		mBluetoothGatt = device.connectGatt(this, autoconnect, mGattCallback);
	}

	public void disconnectDevice(String address) {
		BluetoothDevice mBluetoothDevice = mDevices.get(address);
		if (mBluetoothDevice != null) {
			disconnect(mBluetoothDevice);
		}
	}
	
	protected void disconnect(BluetoothDevice device) {
		mBluetoothGatt.disconnect();
		mBluetoothGatt.close();
	}

	public void readRssi(String deviceAddress) {
		readDeviceRssi(deviceAddress);
	}
	
	protected void readDeviceRssi(String address) {
		BluetoothDevice mDevice = mDevices.get(address);
		if (mDevice != null) {
			readDeviceRssi(mDevice);
		}
	}

	protected void readDeviceRssi(BluetoothDevice device) {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.readRemoteRssi();
		}
	}

	public void writeValue(String deviceAddress, char[] data) {
		if (txCharc != null) {
			String value = new String(data);

			if (txCharc.setValue(value)) {
				if (!mBluetoothGatt.writeCharacteristic(txCharc)) {
					Log.e(TAG, "Error: writeCharacteristic!");
				}
			} 
			else {
				Log.e(TAG, "Error: setValue!");
			}
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			Log.d(TAG, "onScanResult (device : " + device.getName() + ")");

			if (connectionEventListener != null) {
				Log.d(TAG, "mIScanDeviceListener (device : " + device.getName() + ")");
				addDevice(device);
				connectionEventListener.onDeviceFound(device.getAddress(), device.getName(), rssi, 
						device.getBondState(), scanRecord, device.getUuids());
			}
		}
	};

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

			Log.d(TAG, "onCharacteristicChanged ( characteristic : " + characteristic + ")");
			int i = 0;
			Integer temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, i++);
			ArrayList<Integer> values = new ArrayList<Integer>();
			while (temp != null) {
				Log.e(TAG, "temp: " + temp);
				values.add(temp);
				temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, i++);
			}

			int[] received = new int[i];
			i = 0;
			for (Integer integer : values) {
				received[i++] = integer.intValue();
			}

			if (connectionEventListener != null) {
				connectionEventListener.onDeviceReadValue(received);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG, "onCharacteristicRead ( characteristic :" + characteristic + " ,status, : " + status + ")");
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG, "onCharacteristicWrite ( characteristic :" + characteristic + " ,status : " + status + ")");
			}
		};

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			BluetoothDevice device = gatt.getDevice();

			Log.d(TAG, "onConnectionStateChange (device : " + device + ", status : " + status + " , newState :  " + newState
					+ ")");

			if (connectionEventListener != null) {
				connectionEventListener.onDeviceConnectStateChange(device.getAddress(), newState);
			}

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (mServiceListener != null) mServiceListener.onConnectedToDevice(device);
				mBluetoothGatt.discoverServices();
				readDeviceRssi(device);
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor device, int status) {
			Log.d(TAG, "onDescriptorRead (device : " + device + " , status :  " + status + ")");
			super.onDescriptorRead(gatt, device, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor arg0, int status) {
			Log.d(TAG, "onDescriptorWrite (arg0 : " + arg0 + " , status :  " + status + ")");
			super.onDescriptorWrite(gatt, arg0, status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			Log.d(TAG, "onReliableWriteCompleted (gatt : " + status + " , status :  " + status + ")");
			super.onReliableWriteCompleted(gatt, status);
		}

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			BluetoothDevice device = gatt.getDevice();

			Log.d(TAG, "onReadRemoteRssi (device : " + device + " , rssi :  " + rssi + " , status :  " + status + ")");

			if (connectionEventListener != null) {
				connectionEventListener.onDeviceRssiUpdate(device.getAddress(), rssi, status);
			}

		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			BluetoothGattService rblService = mBluetoothGatt.getService(RBL_SERVICE);

			if (rblService == null) {
				Log.e(TAG, "RBL service not found!");
				return;
			}

			List<BluetoothGattCharacteristic> Characteristic = rblService.getCharacteristics();

			for (BluetoothGattCharacteristic a : Characteristic) {
				Log.e(TAG, " a =  uuid : " + a.getUuid() + "");
			}

			BluetoothGattCharacteristic rxCharc = rblService.getCharacteristic(RBL_DEVICE_RX_UUID);
			if (rxCharc == null) {
				Log.e(TAG, "RBL RX Characteristic not found!");
				return;
			}

			txCharc = rblService.getCharacteristic(RBL_DEVICE_TX_UUID);
			if (txCharc == null) {
				Log.e(TAG, "RBL RX Characteristic not found!");
				return;
			}

			enableNotification(true, rxCharc);

			if (connectionEventListener != null)
				connectionEventListener.onDeviceCharacteristicFound();
		}
	};

	public boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt == null) {
			return false;
		}
		
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
			return false;
		}

		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(CCC);
		if (clientConfig == null) {
			return false;
		}

		if (enable) {
			Log.i(TAG, "enable notification");
			clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} 
		else {
			Log.i(TAG, "disable notification");
			clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}

		return mBluetoothGatt.writeDescriptor(clientConfig);
	}
	
}
