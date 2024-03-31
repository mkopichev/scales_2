package com.example.scales_2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.zip.Inflater;

public class FragmentWork extends Fragment implements Display{

    View view;
    TextView weightText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_work, container, false);

        weightText = view.findViewById(R.id.weight_text);

        return view;
    }

    @Override
    public void showWeight(Float weight) {
        weightText.setText(String.valueOf(weight));
    }
}
