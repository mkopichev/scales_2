package com.example.scales_2;

import android.util.Log;

public class ScaleCommunicator implements Polling {

    Display displayer;
    Thread pollThread;

    public ScaleCommunicator(Display display) {
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
}
