package com.jthtml.zebraDrop.android;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jthtml.zebraDrop.ZebraDropGame;
import com.jthtml.zebraDrop.DesktopInterface;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Try to disable vsync at the window level
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        
        // Performance optimizations for higher FPS
        configuration.useGL30 = false; // Better compatibility and potentially higher FPS
        configuration.numSamples = 0; // Disable anti-aliasing for better performance
        configuration.useCompass = false; // Disable compass
        configuration.useAccelerometer = false; // Disable accelerometer
        configuration.useGyroscope = false; // Disable gyroscope
        
        initialize(new ZebraDropGame(new DesktopInterface()), configuration);
    }
}