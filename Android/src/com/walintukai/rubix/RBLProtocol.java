package com.walintukai.rubix;

import android.util.Log;

public class RBLProtocol {

	final String TAG = "RBLProtocol";

	IRBLProtocol mIrblProtocol;
	ConnectionService service;
	String address;

	public RBLProtocol(String address) {
		this.address = address;
	}

	public void setIRBLProtocol(IRBLProtocol mIrblProtocol) {
		this.mIrblProtocol = mIrblProtocol;
	}

	public void setmIRedBearService(ConnectionService service) {
		this.service = service;
	}

	public void parseData(int[] data) {
		int i = 0;
		final int length = data.length;
		while (i < length) {
			int type = data[i++];
			Log.e(TAG, "type: " + type);
			switch (type) {
			case IRBLProtocol.MESSAGE_TYPE_PROTOCOL_VERSION: // report protocol version
				if (mIrblProtocol != null) {
					mIrblProtocol.protocolDidReceiveProtocolVersion(data[i++], data[i++], data[i++]);
				}
				break;

			case IRBLProtocol.MESSAGE_TYPE_PIN_COUNT: // report total pin count of the board
				if (mIrblProtocol != null) {
					mIrblProtocol.protocolDidReceiveTotalPinCount(data[i++]);
				}
				break;

			case IRBLProtocol.MESSAGE_TYPE_PIN_CAPABILITY: // report pin capability
				if (mIrblProtocol != null) {
					mIrblProtocol.protocolDidReceivePinCapability(data[i++], data[i++]);
				}
				break;

			case IRBLProtocol.MESSAGE_TYPE_CUSTOM_DATA: // custom data
				if (mIrblProtocol != null) {
					int[] result = new int[length - 1];
					for (int index = i; index < length; index++) {
						result[index - 1] = data[index];
					}
					mIrblProtocol.protocolDidReceiveCustomData(result, length - 1);
				}
				i = length;
				break;

			case IRBLProtocol.MESSAGE_TYPE_READ_PIN_MODE: // report pin mode
				if (mIrblProtocol != null) {
					mIrblProtocol.protocolDidReceivePinMode(data[i++], data[i++]);
				}
				break;

			case IRBLProtocol.MESSAGE_TYPE_READ_PIN_DATA: // report pin data
				if (mIrblProtocol != null) {
					if (data[3] > 127 || data[3] < 0) {
						Log.e(TAG, "data[4]: " + data[4]);
					}
					mIrblProtocol.protocolDidReceivePinData(data[i++], data[i++], data[i++]);
				}
				break;
			}
		}
	}

	protected void write(char[] data) {
		if (service != null) {
			service.writeValue(address, data);
		}
	}

	public void setPinMode(int pin, int mode) {
		Log.e(TAG, "setPinMode");
		char buf[] = { 'S', (char) pin, (char) mode };
		write(buf);
	}

	public void digitalRead(int pin) {
		Log.e(TAG, "digitalRead");
		char buf[] = { 'G', (char) pin };
		write(buf);
	}

	public void digitalWrite(int pin, int value) {
		Log.e(TAG, "digitalWrite");
		char buf[] = { 'T', (char) pin, (char) value };
		write(buf);
	}

	public void queryPinAll() {
		Log.e(TAG, "queryPinAll");
		char buf[] = { 'A', 0x0d, 0x0a };
		write(buf);
	}

	public void queryProtocolVersion() {
		Log.e(TAG, "queryProtocolVersion");
		char buf[] = { 'V' };
		write(buf);
	}

	public void queryTotalPinCount() {
		Log.e(TAG, "queryTotalPinCount");
		char buf[] = { 'C' };
		write(buf);
	}

	public void queryPinCapability(int pin) {
		Log.e(TAG, "queryPinCapability");
		char buf[] = { 'P', (char) pin };
		write(buf);
	}

	public void queryPinMode(int pin) {
		Log.e(TAG, "queryPinMode");
		char buf[] = { 'M', (char) pin };
		write(buf);
	}

	public void analogWrite(int pin, int value) {
		Log.e(TAG, "analogWrite value: " + value);
		char buf[] = { 'N', (char) pin, (char) value };
		write(buf);
	}

	public void servoWrite(int pin, int value) {
		Log.e(TAG, "servoWrite value: " + value);
		char buf[] = { 'O', (char) pin, (char) value };
		write(buf);
	}

	public void sendCustomData(int[] data, int length) {
		char[] buf = new char[1 + 1 + length];
		buf[0] = 'Z';
		buf[1] = (char) length;
		int j = 0;
		for (int i = 2; i < length; i++) {
			buf[i] = (char) data[j++];
		}
		write(buf);
	}

}
