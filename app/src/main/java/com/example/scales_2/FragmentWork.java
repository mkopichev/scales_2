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

public class FragmentWork extends Fragment {

    View view;
    TextView weightHistory1;
    TextView weightHistory2;
    TextView weightHistory3;
    TextView weightHistory4;
    TextView weightText;
    TextView pollIpText;
    TextView pollPortText;
    TextView pollStatusText;

    String ip;
    int port;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_work, container, false);

        weightHistory1 = view.findViewById(R.id.weight_history_1);
        weightHistory2 = view.findViewById(R.id.weight_history_2);
        weightHistory3 = view.findViewById(R.id.weight_history_3);
        weightHistory4 = view.findViewById(R.id.weight_history_4);
        weightText = view.findViewById(R.id.weight_text);
        pollIpText = view.findViewById(R.id.scale_selected_ip);
        pollPortText = view.findViewById(R.id.scale_selected_port);
        pollStatusText = view.findViewById(R.id.scale_poll_status);

        if(ip != null) {
            pollIpText.setText(ip);
        }
        pollPortText.setText(String.valueOf(port));

        return view;
    }

    public void updateHistory(Integer[] weights) {
        weightHistory1.setText(String.format("%d", weights[0]));
        weightHistory2.setText(String.format("%d", weights[1]));
        weightHistory3.setText(String.format("%d", weights[2]));
        weightHistory4.setText(String.format("%d", weights[3]));
    }
}
