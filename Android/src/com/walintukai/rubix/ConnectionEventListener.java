package com.walintukai.rubix;

import android.os.ParcelUuid;

public interface ConnectionEventListener {
	
	void onDeviceFound(String deviceAddress, String name, int rssi, int bondState, byte[] scanRecord, ParcelUuid[] uuids);

	void onDeviceRssiUpdate(String deviceAddress, int rssi, int state);

	void onDeviceConnectStateChange(String deviceAddress, int state);

	void onDeviceReadValue(int[] value);
	
	void onDeviceCharacteristicFound();
	
}