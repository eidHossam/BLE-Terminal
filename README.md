# BLE Terminal Documentation

This documentation provides an overview of the BLE Terminal project, which allows you to connect to an HM-XX BLE module and send/receive commands from the device. The project is implemented in Android using Java and follows the MVVM (Model-View-ViewModel) architecture.

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [MainActivity](#mainactivity)
4. [BluetoothViewModel](#bluetoothviewmodel)
5. [TerminalActivity](#terminalactivity)
6. [TerminalViewModel](#terminalviewmodel)
7. [Permissions](#permissions)
8. [Usage](#usage)
9. [Conclusion](#conclusion)

---

## Introduction

The BLE Terminal project is designed to facilitate communication with HM-XX BLE modules. It allows users to:
- Scan for nearby BLE devices.
- Connect to a specific BLE device using its MAC address or name.
- Send commands to the connected BLE device.
- Receive and display responses from the BLE device.
- Save the communication history to a file.

The project is divided into several components, each responsible for a specific part of the functionality.

---

## Project Structure

The project is structured into the following components:

- **MainActivity**: The entry point of the application. It handles device scanning and connection initiation.
- **BluetoothViewModel**: Manages the BLE scanning logic and communicates with the `MainActivity`.
- **TerminalActivity**: The interface for sending commands and receiving responses from the connected BLE device.
- **TerminalViewModel**: Manages the BLE connection, data transmission, and reception.

---

## MainActivity

### Overview
`MainActivity` is the first screen users interact with. It allows users to:
- Check for BLE availability.
- Request necessary permissions.
- Scan for BLE devices.
- Connect to a selected BLE device.

### Key Methods

1. **`checkBLEAvailability()`**:
   - Checks if the device supports BLE.
   - Displays a toast message if BLE is not available.

2. **`checkAndRequestPermissions()`**:
   - Checks and requests necessary permissions for BLE scanning and connection.
   - Permissions vary based on the Android version.

3. **`subscribeToObservers()`**:
   - Observes the `scanResult` LiveData from `BluetoothViewModel`.
   - Updates the UI based on the scan results (e.g., "Scanning", "Device found", "Device not found").

4. **`onConnectBtnClick()`**:
   - Validates the entered MAC address and device name.
   - Initiates the BLE scan using `BluetoothViewModel`.

5. **`goToTerminalActivity()`**:
   - Launches `TerminalActivity` when a device is found and selected.

---

## BluetoothViewModel

### Overview
`BluetoothViewModel` handles the BLE scanning logic. It uses Android's `BluetoothLeScanner` to scan for nearby BLE devices.

### Key Methods

1. **`scanLeDevice(String macAddress, String name)`**:
   - Starts scanning for BLE devices with the specified MAC address or name.
   - Stops scanning after a predefined period (`SCAN_PERIOD`).

2. **`leScanCallback`**:
   - Callback for handling scan results.
   - Updates the `scanResult` LiveData when a device is found.

3. **`getScanResult()`**:
   - Returns the `MutableLiveData<String>` that holds the scan result status.

4. **`getDevice()`**:
   - Returns the discovered `BluetoothDevice`.

---

## TerminalActivity

### Overview
`TerminalActivity` provides the interface for interacting with the connected BLE device. Users can:
- Send commands to the device.
- View responses from the device.
- Save the communication history to a file.
- Disconnect from the device.

### Key Methods

1. **`subscribeToObservers()`**:
   - Observes the `responseData` and `isDeviceConnected` LiveData from `TerminalViewModel`.
   - Updates the UI based on the received data and connection status.

2. **`onSendCommandBtnClick()`**:
   - Sends the entered command to the BLE device via `TerminalViewModel`.

3. **`onConnectDisconnectBtnClick()`**:
   - Handles the connect/disconnect button click.
   - Connects or disconnects from the BLE device.

4. **`saveTextViewToFile()`**:
   - Saves the terminal's text content to a file in the Downloads folder.

5. **Command Buttons**:
   - Predefined buttons for sending specific commands (e.g., "on", "download", "alarm").

---

## TerminalViewModel

### Overview
`TerminalViewModel` manages the BLE connection and data transmission. It handles:
- Connecting to the BLE device.
- Discovering services and characteristics.
- Sending commands to the BLE device.
- Receiving responses from the BLE device.

### Key Methods

1. **`connectToDevice()`**:
   - Initiates a connection to the BLE device using `BluetoothGatt`.

2. **`gattCallback`**:
   - Callback for handling BLE connection state changes, service discovery, and characteristic read/write operations.

3. **`writeToCharacteristic(String data)`**:
   - Sends a command to the BLE device by writing to the characteristic.

4. **`disconnectDevice()`**:
   - Disconnects from the BLE device and releases resources.

5. **`getIsDeviceConnected()`**:
   - Returns the `MutableLiveData<Boolean>` that holds the connection status.

6. **`getResponseData()`**:
   - Returns the `MutableLiveData<String>` that holds the received data from the BLE device.

---

## Permissions

The application requires the following permissions:
- **Bluetooth**: Required for scanning and connecting to BLE devices.
- **Location**: Required for BLE scanning on Android 6.0 and above.
- **Bluetooth Scan/Connect**: Required for BLE operations on Android 12 (API 31) and above.

Permissions are requested dynamically based on the Android version.

---

## Usage

1. **Launch the App**:
   - Open the app on an Android device with BLE support.

2. **Scan for Devices**:
   - Enter the MAC address or name of the BLE device and click "Connect".

3. **Connect to a Device**:
   - Once the device is found, the app will automatically navigate to the `TerminalActivity`.

4. **Send Commands**:
   - Use the command buttons or enter custom commands in the text field.

5. **View Responses**:
   - Responses from the BLE device will be displayed in the terminal.

6. **Save History**:
   - Click "Save History" to save the terminal's content to a file.

7. **Disconnect**:
   - Click the disconnect button to disconnect from the BLE device.

---

## Conclusion

The BLE Terminal project provides a simple and effective way to communicate with HM-XX BLE modules. By following the MVVM architecture, the code is modular, maintainable, and easy to extend. Future enhancements could include support for more BLE devices, additional command options, and improved error handling.

For any issues or suggestions, please refer to the project's GitHub repository or contact the maintainers.

--- 

This concludes the documentation for the BLE Terminal project. Happy coding! 🚀