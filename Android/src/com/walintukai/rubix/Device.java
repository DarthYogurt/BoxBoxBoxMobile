package com.walintukai.rubix;

import java.io.Serializable;

import android.os.ParcelUuid;

public class Device implements Serializable {

	private static final long serialVersionUID = -6308814452972761096L;

	private String name;
	private String address;
	private int bondState;
	private int rssi;
	private byte[] scanReadData;
	private ParcelUuid[] uuids;
	
	public Device(String name, String address, int bondState, int rssi, byte[] scanReadData, ParcelUuid[] uuids) {
		this.name = name;
		this.address = address;
		this.bondState = bondState;
		this.rssi = rssi;
		this.scanReadData = scanReadData;
		this.uuids = uuids;
	}
	
	public String getName() { return name; }
	
	public String getAddress() { return address; }
	
	public int getBondState() { return bondState; }
	
	public int getRssi() { return rssi; }
	
	public byte[] getScanReadData() { return scanReadData; }
	
	public ParcelUuid[] getUuids() { return uuids; }
	
}
