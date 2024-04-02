package com.example.scales_2;

import android.util.Log;

import com.example.scales_2.network_stuff.NetworkHandler;
import com.example.scales_2.network_stuff.Protocol100;

import java.io.IOException;

public class ScaleCommunicator implements ScalesNetworkManager {

    ScalesDisplay displayer;
    Thread pollThread;

    public ScaleCommunicator(ScalesDisplay display) {
        this.displayer = display;
    }

    @Override
    public void startPolling(String ip, String port) {
        if (pollThread == null) {
            pollThread = new Thread(() -> {
                float i = 0.0f;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    displayer.showWeight(i++);
                }
            });
        }
        if (pollThread.isAlive()) {
            Log.e("TAG", "Already running");
            return;
        }
        Log.e("TAG", "startPolling");
        pollThread.start();
    }

    @Override
    public void stopPolling() {

    }

    @Override
    public void testConnection(String ip, int port) {
        Thread testConnectionThread = new Thread(()-> {
            byte[] response = new byte[0];
            try {
                response = NetworkHandler.transmitForResponse(ip, port,
                        Protocol100.buildTransmitMessage(Protocol100.Commands.CMD_GET_NAME, null));
            } catch (IOException e) {
                e.printStackTrace();
                displayer.showStatus("Ошибка сети");

                return;
            }
            if(response == null) {
                displayer.showStatus("Нет ответа от весов");
                return;
            }
            String name = Protocol100.parseScalesName(response);
            if(name == null) {
                displayer.showStatus("Устройство не опознано");
                return;
            }
            displayer.showStatus("Найдены весы " + name);
        });
        testConnectionThread.start();
    }
}
