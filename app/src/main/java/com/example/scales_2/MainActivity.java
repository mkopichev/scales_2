package com.example.scales_2;

import android.Manifest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.scales_2.databinding.ActivityMainBinding;
import com.example.scales_2.interfaces.ScalesDisplay;
import com.example.scales_2.interfaces.ScalesOperator;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity implements ScalesDisplay, ScalesOperator {

    private ActivityMainBinding binding;

    FragmentWork fragmentWork;

    FragmentConnect fragmentConnect;

    ScaleCommunicator scaleCommunicator;

    ActivityResultLauncher<String[]> internetPermissionLauncher;

    String ip;
    int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentWork = new FragmentWork();
        scaleCommunicator = new ScaleCommunicator(this);
        fragmentConnect = new FragmentConnect(this);


        internetPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean areAllGranted = true;
                    for (Boolean b : result.values()) {
                        areAllGranted = areAllGranted && b;
                    }

                    if (areAllGranted) {

                    } else {
                        Toast toast = new Toast(getApplicationContext());
                        toast.setText("Нет разрешений на работу с сетью");
                    }
                }
        );


        binding.navView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Polling Tab
                if (tab == binding.navView.getTabAt(0)) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.nav_host_fragment_activity_main, fragmentWork).commit();
                    scaleCommunicator.startPolling(ip, port);
                }
                if (tab == binding.navView.getTabAt(1)) {
                // Settings Tab
                    fragmentConnect.ip = ip;
                    fragmentConnect.port = port;
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.nav_host_fragment_activity_main, fragmentConnect).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                    // Polling Tab
                    if (tab == binding.navView.getTabAt(0)) {
                        scaleCommunicator.stopPolling();
                    }

                }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        ip = sharedPrefs.getString("scaleIP", "/192.168.0.1");
        port = sharedPrefs.getInt("scalePort", 5001);

        fragmentWork.ip = ip;
        fragmentWork.port = port;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // To force select and show first tab
        binding.navView.getTabAt(1).select();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(()-> binding.navView.getTabAt(0).select(), 100);
    }

    @Override
    protected void onPause() {
        Log.e("TAG", "onPause");
        scaleCommunicator.stopPolling();
        super.onPause();
    }

    @Override
    public void showWeight(Integer weight) {
        if(fragmentWork == null)
            return;
        runOnUiThread(()-> {
            if(weight == null)
                return;
            fragmentWork.weightText.setText(String.valueOf(weight) + " г");
        });
    }

    @Override
    public void showStatus(String message) {
        if(fragmentConnect == null) {
            return;
        }
        runOnUiThread(() -> {
            fragmentConnect.statusText.setText(message);
        });
    }

    @Override
    public void showPollingStatus(String message) {
        if(fragmentWork == null) {
            return;
        }
        runOnUiThread(() -> {
            fragmentWork.pollStatusText.setText(message);
        });
    }

    @Override
    public void checkConnection(String ip, int port) {
        int internetPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            fragmentConnect.statusText.setText("Нет разрешений");
             return;
        }
        scaleCommunicator.testConnection(ip, port);
    }

    @Override
    public void confirmConnectionAddres(String ip, int port) {
        this.ip = ip;
        this.port = port;
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        sharedPrefs.edit().putInt("scalePort", port).putString("scaleIP", ip).apply();
        fragmentWork.ip = ip;
        fragmentWork.port = port;
        binding.navView.getTabAt(0).select();
        runOnUiThread(() -> {
            fragmentWork.pollIpText.setText(ip);
            fragmentWork.pollPortText.setText(String.valueOf(port));
        });
    }
}