package com.example.scales_2.network_stuff;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;


public class NetworkHandler {
    private final Context context;

    /**
     * @param context - context
     */
    public NetworkHandler(Context context) {
        this.context = context;
    }

    /**
     * @param ip      - ip address
     * @param request - request
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] transmitForResponse(String ip, int port, byte[] request) throws IOException {
        System.out.println("request" + Arrays.toString(request));


        Socket socket = new Socket();

        InetSocketAddress address = new InetSocketAddress(ip, port);

        try {
            socket.connect(address, 5000);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("sent");

        socket.setSoTimeout(5000);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream bufferedReader = new DataInputStream(socket.getInputStream());
        dataOutputStream.write(request);

        byte[] responseStart = new byte[5];
        bufferedReader.read(responseStart, 0, 5);
        System.out.println(Arrays.toString(responseStart));

        if ((responseStart[0] != (byte) 0xF8) || (responseStart[1] != (byte) 0x55) || (responseStart[2] != (byte) 0xCE)) {
            socket.close();
            dataOutputStream.close();
            bufferedReader.close();
            return null;
        }
        short length = ByteBuffer.wrap(responseStart).order(ByteOrder.LITTLE_ENDIAN).getShort(3);

        byte[] response = new byte[length + 4];
        response[0] = responseStart[3];
        response[1] = responseStart[4];

        bufferedReader.read(response, 2, length + 2);
        socket.close();
        dataOutputStream.close();
        bufferedReader.close();
        return response;
    }
}
