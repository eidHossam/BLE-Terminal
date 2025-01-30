package com.hossameid.ble_terminal.presentation.terminal;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TerminalViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final BluetoothDevice device;

    public TerminalViewModelFactory(Application application, BluetoothDevice device) {
        this.application = application;
        this.device = device;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TerminalViewModel.class)) {
            return (T) new TerminalViewModel(application, device);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

