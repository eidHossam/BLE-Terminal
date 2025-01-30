package com.hossameid.ble_terminal.presentation.terminal;

import static com.hossameid.ble_terminal.utils.TimeUtils.getCurrentDateTime;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hossameid.ble_terminal.R;
import com.hossameid.ble_terminal.databinding.ActivityTerminalBinding;
import com.hossameid.ble_terminal.utils.TextUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.OutputStream;
import java.util.Objects;

public class TerminalActivity extends AppCompatActivity {
    private ActivityTerminalBinding binding;
    private TerminalViewModel viewModel;
    private Boolean isBackBtnPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTerminalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Enable scrolling in the text view
        binding.terminalTextView.setMovementMethod(new ScrollingMovementMethod());

        TerminalViewModelFactory factory = getTerminalViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(TerminalViewModel.class);

        subscribeToObservers();

        binding.connectDisconnectBtn.setOnClickListener(l -> onConnectDisconnectBtnClick());

        binding.backButton.setOnClickListener(l -> {
            viewModel.disconnectDevice();
            isBackBtnPressed = true;
        });

        binding.eraseTerminalBtn.setOnClickListener(l -> binding.terminalTextView.setText(""));
        binding.saveHistoryBtn.setOnClickListener(l -> saveTextViewToFile());
        binding.sendCommandBtn.setOnClickListener(l -> onSendCommandBtnClick());

        // Command Buttons
        binding.command1Btn.setOnClickListener(l -> onCommand1Click());
        binding.command2Btn.setOnClickListener(l -> onCommand2Click());
        binding.command3Btn.setOnClickListener(l -> onCommand3Click());
        binding.command4Btn.setOnClickListener(l -> onCommand4Click());
        binding.command5Btn.setOnClickListener(l -> onCommand5Click());
        binding.command6Btn.setOnClickListener(l -> onCommand6Click());
        binding.command7Btn.setOnClickListener(l -> onCommand7Click());
        binding.command8Btn.setOnClickListener(l -> onCommand8Click());
        binding.command9Btn.setOnClickListener(l -> onCommand9Click());
        binding.command10Btn.setOnClickListener(l -> onCommand10Click());
    }

    private @NonNull TerminalViewModelFactory getTerminalViewModelFactory() {
        // Get the bluetooth device from the intent
        Intent intent = getIntent();
        BluetoothDevice device;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            device = intent.getParcelableExtra("bluetooth_device", BluetoothDevice.class);
        } else {
            device = intent.getParcelableExtra("bluetooth_device");
        }

        // Create the ViewModel using the custom factory
        return new TerminalViewModelFactory(getApplication(), device);
    }


    private void subscribeToObservers() {
        viewModel.getResponseData().observe(this, responseData ->
                TextUtils.appendTextSafely(binding.terminalTextView, responseData, this));

        viewModel.getIsDeviceConnected().observe(this, isDeviceConnected -> {
            if (isDeviceConnected) {
                TextUtils.appendTextSafely(binding.terminalTextView, "Connected", this);
                binding.connectDisconnectBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.bluetooth_disabled));
                binding.connectDisconnectBtn.setContentDescription(ContextCompat.getString(this, R.string.disconnect));
            } else {
                if (isBackBtnPressed) {
                    finish();
                } else {
                    TextUtils.appendTextSafely(binding.terminalTextView, "Disconnected", this);
                    binding.connectDisconnectBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.bluetooth_connect));
                    binding.connectDisconnectBtn.setContentDescription(ContextCompat.getString(this, R.string.connect));
                }
            }

            // Hide the progress bar
            binding.progressBar.setVisibility(View.GONE);
            // Show the connect Button
            binding.connectDisconnectBtn.setVisibility(View.VISIBLE);
        });
    }

    private void onSendCommandBtnClick() {
        // Remove the error from the command textField
        binding.commandTextField.setError(null);

        String command = Objects.requireNonNull(binding.commandTextField.getText()).toString();
        if (command.isEmpty()) {
            binding.commandTextField.setError("Command Can't be empty!");
            return;
        }

        // Erase the command TextField
        binding.commandTextField.setText(null);

        // Print the command on the terminal
        TextUtils.appendTextSafely(binding.terminalTextView, command, this);

        viewModel.writeToCharacteristic(command);
    }

    private void onConnectDisconnectBtnClick() {
        if (Boolean.TRUE.equals(viewModel.getIsDeviceConnected().getValue())) {
            // If the device is connected then disconnect it
            viewModel.disconnectDevice();
        } else {
            // If the device is disconnected attempt the reconnection
            viewModel.connectToDevice();

            // Hide the connect Button
            binding.connectDisconnectBtn.setVisibility(View.GONE);
            // Show the progress bar
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void saveTextViewToFile() {
        String textToSave = binding.terminalTextView.getText().toString(); // Get text from TextView
        String fileName = getCurrentDateTime() + ".txt"; // Name of the file

        saveFileToMediaStore(fileName, textToSave); // Scoped Storage method
    }

    private void saveFileToMediaStore(String fileName, String content) {
        ContentResolver resolver = getContentResolver();

        // Define metadata for the file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the file into MediaStore
        Uri fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        if (fileUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(fileUri)) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes()); // Write content to the file
                    Toast.makeText(this, "File saved to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to create file in MediaStore.", Toast.LENGTH_LONG).show();
        }
    }

    // Command Buttons actions

    private void onCommand1Click() {
        // Code for command 1 should be inserted below
        String command = "on";
        sendCommand(command);
    }

    private void onCommand2Click() {
        // Code for command 2 should be inserted below
        String command = "download";
        sendCommand(command);
    }

    private void onCommand3Click() {
        // Code for command 3 should be inserted below
        String timestamp = getCurrentDateTime("YYMMDDHHMM");
        String command = "TIME" + timestamp;
        sendCommand(command);
    }

    private void onCommand4Click() {
        // Code for command 4 should be inserted below
        String command = "alarm";
        sendCommand(command);
    }

    private void onCommand5Click() {
        // Code for command 5 should be inserted below
        String command = "alarm2";
        sendCommand(command);
    }

    private void onCommand6Click() {
        // Code for command 6 should be inserted below
        String command = "erase";
        sendCommand(command);
    }

    private void onCommand7Click() {
        // Code for command 7 should be inserted below
        String command = "password";
        sendCommand(command);
    }

    private void onCommand8Click() {
        // Code for command 8 should be inserted below
        String command = "id";
        sendCommand(command);
    }

    private void onCommand9Click() {
        // Code for command 9 should be inserted below
        String command = "start";
        sendCommand(command);
    }

    private void onCommand10Click() {
        // Code for command 10 should be inserted below
        String command = "export";
        sendCommand(command);
    }

    private void sendCommand(String command) {
        // Print the command on the terminal
        TextUtils.appendTextSafely(binding.terminalTextView, command, this);
        viewModel.writeToCharacteristic(command);
    }
}