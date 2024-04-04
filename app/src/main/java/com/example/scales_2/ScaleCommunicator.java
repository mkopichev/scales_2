package com.example.scales_2;

import static com.example.scales_2.network_stuff.Protocol100.buildTransmitMessage;

import android.util.Log;

import com.example.scales_2.interfaces.ScalesDisplay;
import com.example.scales_2.interfaces.ScalesNetworkManager;
import com.example.scales_2.network_stuff.NetworkHandler;
import com.example.scales_2.network_stuff.Protocol100;

import java.io.IOException;

public class ScaleCommunicator implements ScalesNetworkManager {

    private ScalesDisplay displayer;
    private Thread pollThread;

    private int errorCount = 0;

    public ScaleCommunicator(ScalesDisplay display) {
        this.displayer = display;
    }

    @Override
    public void startPolling(String ip, int port) {
        Runnable poller = () -> {
            Integer weight;
            while (true) {
                try {
                    byte[] response = new byte[0];
                    try {
                        Log.e("TAG", "ip " + ip + "port " + port );
                        response = NetworkHandler.transmitForResponse(ip, port, buildTransmitMessage(Protocol100.Commands.CMD_GET_MASS, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(errorCount++ > 30) {
                            displayer.showPollingStatus("Ошибка соединения");
                            return;
                        }
                    }
                    if(response == null) {
                        displayer.showPollingStatus("Ошибка соединения");
                        return;
                    }
                    errorCount = 0;
                    displayer.showPollingStatus("Ошибок нет");
                    weight = Protocol100.parseMass(response);
                    displayer.showWeight(weight);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e("TAG", "Poll thread stopped");
                    return;
                }
            }
        };
        if (pollThread == null) {
            pollThread = new Thread(poller);
            Log.e("TAG", "startPolling");
            pollThread.start();
        }
        if (pollThread.isAlive()) {
            Log.e("TAG", "Already running");
            return;
        }

        Log.e("TAG", "startPolling");
        pollThread = new Thread(poller);
        pollThread.start();
    }

    @Override
    public void stopPolling() {
        if(pollThread != null)
            pollThread.interrupt();
    }

    @Override
    public void testConnection(String ip, int port) {
        Thread testConnectionThread = new Thread(()-> {
            byte[] response;
            try {
                response = NetworkHandler.transmitForResponse(ip, port,
                        buildTransmitMessage(Protocol100.Commands.CMD_GET_NAME, null));
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
