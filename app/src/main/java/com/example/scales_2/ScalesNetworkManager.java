package com.example.scales_2;

public interface ScalesNetworkManager {

    public void startPolling(String ip, String port);

    public void stopPolling();

    public void testConnection(String ip, int port);

}
