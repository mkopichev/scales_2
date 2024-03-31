package com.example.scales_2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scales_2.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentWork fragmentWork = new FragmentWork();
        ScaleCommunicator scaleCommunicator = new ScaleCommunicator(fragmentWork);
        FragmentConnect fragmentConnect = new FragmentConnect();

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

}