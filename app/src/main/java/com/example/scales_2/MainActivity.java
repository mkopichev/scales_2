package com.example.scales_2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.scales_2.databinding.ActivityMainBinding;
import com.example.scales_2.interfaces.ScalesDisplay;
import com.example.scales_2.interfaces.ScalesOperator;
import com.google.android.material.tabs.TabLayout;

import java.nio.Buffer;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.apache.commons.collections4.queue.CircularFifoQueue;


public class MainActivity extends AppCompatActivity implements ScalesDisplay, ScalesOperator {

    private ActivityMainBinding binding;

    FragmentWork fragmentWork;

    FragmentConnect fragmentConnect;

    ScaleCommunicator scaleCommunicator;

    ActivityResultLauncher<String[]> internetPermissionLauncher;

    String ip;
    int port;

    WeightHistory weightHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentWork = new FragmentWork();
        scaleCommunicator = new ScaleCommunicator(this);
        fragmentConnect = new FragmentConnect(this);
        weightHistory = new WeightHistory();

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

        Thread thread = new Thread(() -> {
            Integer weight = 350;
            while(true) {
                try {
                    Thread.sleep(1000);

                    Integer finalWeight = weight;
                    runOnUiThread(() -> showWeight(finalWeight));
                    Thread.sleep(1000);
                    runOnUiThread(() -> showWeight(finalWeight));

                    Thread.sleep(1000);
                    runOnUiThread(() -> showWeight(0));
                weight++;
                } catch (InterruptedException e) {
                    continue;
                }
            }

        });
        thread.start();


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
        ip = sharedPrefs.getString("scaleIP", "192.168.0.1");
        port = sharedPrefs.getInt("scalePort", 5001);

        fragmentWork.ip = ip;
        fragmentWork.port = port;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(binding.navView.getTabAt(1).isSelected())
            return;

        // To force select and show first tab
        binding.navView.getTabAt(1).select();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(()-> binding.navView.getTabAt(0).select(), 100);
    }

    @Override
    protected void onPause() {
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
            fragmentWork.weightText.setText(String.format("%d гр.", weight));
            weightHistory.add(weight);
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

    private class WeightHistory {

        private CircularFifoQueue<Integer> weights = new CircularFifoQueue<>(5);
        private Integer lastWeight = 0;

        public WeightHistory(){
            weights.add(0);
            weights.add(0);
            weights.add(0);
            weights.add(0);

        }

        public void add(Integer weight) {
            if(lastWeight >= 350) {
                if(weight < 350)
                    lastWeight = 0;
                return;
            }
            lastWeight = weight;
            weights.add(weight);
            fragmentWork.updateHistory(weights.toArray(new Integer[0]));


        }


    }
}