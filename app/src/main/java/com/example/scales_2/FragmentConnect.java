package com.example.scales_2;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentConnect extends Fragment {

    View view;
    EditText ipEditText;
    EditText portEditText;
    Button checkCOnnectionButton;
    TextView statusText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);

        InputFilter[] ipFilters = new InputFilter[1];
        ipFilters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart) +
                        source.subSequence(start, end) +
                        destTxt.substring(dend);
                if (!resultingTxt.matches("^\\d{1,3}(\\." +
                        "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.parseInt(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };
        ipEditText = view.findViewById(R.id.scale_ip_address);
        ipEditText.setFilters(ipFilters);

        InputFilter[] portFilters = new InputFilter[1];
        portFilters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart) +
                        source.subSequence(start, end) +
                        destTxt.substring(dend);
                if (!resultingTxt.matches("^\\d{1,5}")) {
                    return "";
                } else {
                    if (Integer.parseInt(resultingTxt) > 65535) {
                        return "";
                    }
                }
            }
            return null;
        };
        portEditText = view.findViewById(R.id.scale_port);
        portEditText.setFilters(portFilters);

        statusText = view.findViewById(R.id.scale_check_connection_status);

        checkCOnnectionButton = view.findViewById(R.id.scale_check_connection_button);
        checkCOnnectionButton.setOnClickListener(view1 -> {
            statusText.setText("Подключаемся...");
        });

        return view;
    }


}
