package com.example.scales_2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout topNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        topNavigationView = findViewById(R.id.top_navigation_view);

        viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(viewPagerAdapter);

    }

}