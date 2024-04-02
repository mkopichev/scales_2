package com.example.scales_2;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.scales_2.databinding.ActivityMainBinding;
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
                if (tab == binding.navView.getTabAt(0)) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.nav_host_fragment_activity_main, fragmentWork).commit();
                    scaleCommunicator.startPolling("a", "s");
                    // TODO(Polling)
                }
                if (tab == binding.navView.getTabAt(1)) {
                    // TODO(Settings)
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.nav_host_fragment_activity_main, fragmentConnect).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


                }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void showWeight(Float weight) {

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
        binding.navView.getTabAt(0).select();
    }
}