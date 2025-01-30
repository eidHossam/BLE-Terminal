package com.hossameid.ble_terminal.presentation.terminal;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TerminalViewModel extends AndroidViewModel {
    private final BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic bleModuleChar1;
    private Runnable readRunnable;
    private final Handler handler;

    private final MutableLiveData<Boolean> isDeviceConnected = new MutableLiveData<>();
    private final MutableLiveData<String> responseData = new MutableLiveData<>();

    public TerminalViewModel(@NonNull Application application, BluetoothDevice device) {
        super(application);
        this.device = device;
        handler = new Handler(Looper.getMainLooper()); // Runs on the main thread
        initReadRunnable();

        connectToDevice();

    }

    @SuppressLint("MissingPermission")
    public void connectToDevice() {
        Log.d("BLE_CONNECTION", "onConnectionStateChange: connecting");
        if (device != null) {
            bluetoothGatt = device.connectGatt(getApplication().getApplicationContext(), false, gattCallback);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Successfully connected
                Log.d("BLE_CONNECTION", "onConnectionStateChange: connected");
                gatt.discoverServices();

                // Inform the view of the successful connection
                isDeviceConnected.postValue(true);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Disconnected
                Log.d("BLE_CONNECTION", "onConnectionStateChange: disconnected" + status);
                gatt.close();

                // Inform the view of the connection drop
                isDeviceConnected.postValue(false);

                // Disable automatic reconnection attempts
                // reconnectToDevice();
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                // Replace with your service UUID
                BluetoothGattService service = gatt.getService(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"));
                if (service == null) {
                    Log.e("BLE_CONNECTION", "Service not found");
                    responseData.postValue("Failed to find service");
                    return;
                }
                Log.d("BLE_CONNECTION", "Service Discovered: " + service.getUuid().toString());

                // Replace with your characteristic UUID
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"));
                if (characteristic == null) {
                    Log.e("BLE_CONNECTION", "Characteristic not found");
                    responseData.postValue("Failed to find characteristic");
                    return;
                }

//                Log.d("BLE_CONNECTION", "Characteristic found: " + characteristic.getUuid().toString());
                bleModuleChar1 = characteristic;
                Log.d("BLE_CONNECTION", "Characteristic found: " + bleModuleChar1.getUuid().toString());
                listenToCharacteristic(bleModuleChar1);
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
            Log.d("BLE_CONNECTION", "onCharacteristicRead: " + Arrays.toString(value));
            extractData(characteristic);
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt
                                                    gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            Log.d("BLE_CONNECTION", "onCharacteristicChanged: " + Arrays.toString(value));
            extractData(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE_CONNECTION", "Write ACK successful: " + new String(characteristic.getValue(), StandardCharsets.UTF_8));
            } else {
                Log.e("BLE_CONNECTION", "Write failed, status: " + status);
                responseData.postValue("Write to BLE failed!");
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void listenToCharacteristic(BluetoothGattCharacteristic characteristic) {
        //Check the type of characteristic property
        int property = characteristic.getProperties();

        if (((property & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) ||
                ((property & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0)) {
            // Use this in case of notify or indicate
            setCharacteristicNotification();
        } else if (property == BluetoothGattCharacteristic.PROPERTY_READ) {
            //Use this in case of read
            startReadingCharacteristic();
        }
    }

    // Initialize the Runnable that will read the characteristic
    private void initReadRunnable() {
        readRunnable = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (bluetoothGatt != null && bleModuleChar1 != null) {
                    bluetoothGatt.readCharacteristic(bleModuleChar1);
                }
                // Schedule the next read after 1 second
                handler.postDelayed(this, 1000);
            }
        };
    }

    // Start reading the characteristic every second
    public void startReadingCharacteristic() {
        handler.post(readRunnable); // Start the first execution
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification() {
        if (bluetoothGatt == null) {
            Log.w("BLE_CONNECTION", "BluetoothGatt not initialized");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(bleModuleChar1, true);

        String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

        BluetoothGattDescriptor descriptor = bleModuleChar1.
                getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));

        int property = bleModuleChar1.getProperties();
        if (property == BluetoothGattCharacteristic.PROPERTY_NOTIFY)
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        else if (property == BluetoothGattCharacteristic.PROPERTY_INDICATE)
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);

        bluetoothGatt.writeDescriptor(descriptor);
    }


    private void extractData(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        String textData = new String(data, StandardCharsets.UTF_8);
        // Use postValue to update the MutableLiveData on the main thread
        responseData.postValue(textData);
    }

    private String convertUUID(String uuid) {
        String[] splitedString = uuid.split("-");

        return splitedString[0].replaceFirst("^0+", "");
    }

    @SuppressLint("MissingPermission")
    public void writeToCharacteristic(String data) {
        if (bluetoothGatt == null || bleModuleChar1 == null) {
            Log.e("BLE_CONNECTION", "Error in BLE connection");
            responseData.postValue("Failed to write to BLE device");
            return;
        }

        // Determine if characteristic supports write with response
        boolean supportsWriteWithResponse = (bleModuleChar1.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
        boolean supportsWriteWithoutResponse = (bleModuleChar1.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0;

        if (!supportsWriteWithResponse && !supportsWriteWithoutResponse) {
            Log.e("BLE_CONNECTION", "Characteristic does not support writing");
            responseData.postValue("Failed to write to characteristic");
            return;
        }

        // Choose appropriate write type
        int writeType = supportsWriteWithResponse ?
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT :
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
        bleModuleChar1.setWriteType(writeType);

        String commandTerminator = "\n";
        data = data + commandTerminator;
        bleModuleChar1.setValue(data.getBytes(StandardCharsets.UTF_8)); // Convert string to bytes

        boolean success = bluetoothGatt.writeCharacteristic(bleModuleChar1);
        Log.d("BLE_CONNECTION", "Write success: " + success + ", Type: " +
                (writeType == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT ? "WITH RESPONSE" : "NO RESPONSE"));
    }

    private void reconnectToDevice() {
        // Reconnect with a delay to avoid excessive connection attempts
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (device != null) {
                // Inform the view of the reconnection attempt
                connectToDevice();
            }
        }, 5000); // Reconnect after 5 seconds
    }

    @SuppressLint("MissingPermission")
    public void disconnectDevice() {
        if (bluetoothGatt != null) {
            Log.d("BLE_CONNECTION", "Disconnecting from the device...");

            // Stop reading characteristic to avoid memory leaks
            handler.removeCallbacks(readRunnable);

            // Disconnect and close the GATT connection
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;

            Log.d("BLE_CONNECTION", "Device disconnected and resources released.");
        } else {
            Log.w("BLE_CONNECTION", "BluetoothGatt is null. No active connection to disconnect.");
        }

        // Update LiveData to reflect the disconnected state
        isDeviceConnected.postValue(false);
    }

    public MutableLiveData<Boolean> getIsDeviceConnected() {
        return isDeviceConnected;
    }

    public MutableLiveData<String> getResponseData() {
        return responseData;
    }
}
