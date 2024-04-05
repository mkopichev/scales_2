package com.example.scales_2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scales_2.interfaces.ScalesOperator;
import com.google.android.material.button.MaterialButton;

public class FragmentConnect extends Fragment {

    View view;
    EditText ipEditText;
    EditText portEditText;
    MaterialButton checkConnectionButton;
    MaterialButton confirmConnectionButton;
    TextView statusText;
    ScalesOperator scalesOperator;
    String ip;
    int port;

    public FragmentConnect(ScalesOperator scalesOperator) {
        this.scalesOperator = scalesOperator;
    }

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

        ButtonListener buttonListener = new ButtonListener();

        confirmConnectionButton = view.findViewById(R.id.scale_confirm_connection_button);
        confirmConnectionButton.setOnClickListener(buttonListener);

        checkConnectionButton = view.findViewById(R.id.scale_check_connection_button);
        checkConnectionButton.setOnClickListener(buttonListener);

        if (ip != null)
            ipEditText.setText(ip);
        portEditText.setText(String.valueOf(port));

        return view;
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifi.isWifiEnabled()) {
                statusText.setText("WiFi отключен на планшете");
                return;
            }

            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm.getActiveNetwork() == null) {
                statusText.setText("Необходимо покдлючится к сети WiFi");
                return;
            }


            String ip = ipEditText.getText().toString();
            if (!ip.strip().matches("^[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}$")) {
                statusText.setText("Недопустимый ip");
                return;
            }
            String[] splits = ip.split("\\.");
            for (String split : splits) {
                if (Integer.parseInt(split) > 255) {
                    statusText.setText("Недопустимый ip");
                    return;
                }
            }
            String port = portEditText.getText().toString();
            if (!port.strip().matches("^\\d{1,5}")) {
                statusText.setText("Недопустимый порт");
                return;
            }
            try {
                if (Integer.parseInt(port) > 65535) {
                    statusText.setText("Недопустимый порт");
                    return;
                }
            } catch (NumberFormatException e) {
                statusText.setText("Недопустимый порт");
                return;
            }

            if (view.getId() == checkConnectionButton.getId()) {

                statusText.setText("Идет проверка...");

                scalesOperator.checkConnection(ip, Integer.parseInt(port));
            }
            if (view.getId() == confirmConnectionButton.getId()) {
                scalesOperator.confirmConnectionAddres(ip, Integer.parseInt(port));
            }
        }
    }

}
