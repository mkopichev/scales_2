package com.example.scales_2.interfaces;

public interface ScalesNetworkManager {

    public void startPolling(String ip, int port);

    public void stopPolling();

    public void testConnection(String ip, int port);

}
