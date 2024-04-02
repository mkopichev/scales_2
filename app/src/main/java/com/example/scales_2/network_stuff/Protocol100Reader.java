//package com.example.scales_2.network_stuff;
//
//import android.content.Context;
//
//import com.scales.interfaces.CallbacksInterface;
//
//import java.io.IOException;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class Protocol100Reader extends Protocol100 {
//
//    CallbacksInterface callbacksInterface;
//
//    /**
//     * @param context            - context
//     * @param callbacksInterface - callback interface
//     */
//    public Protocol100Reader(Context context, CallbacksInterface callbacksInterface) {
//        this.context = context;
//        this.callbacksInterface = callbacksInterface;
//    }
//
//    /**
//     * @param deviceIp - scales` ip address
//     */
//    public int requestMass(String deviceIp) {
//        AtomicInteger mass = new AtomicInteger();
//        Thread thread = new Thread(() -> {
//            byte[] response = new byte[0];
//            try {
//                response = NetworkHandler.transmitForResponse(deviceIp, buildTransmitMessage(Commands.CMD_GET_MASS, null));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mass.set(parseMass(response));
//        });
//        thread.start();
//        try {
//            thread.join(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return mass.get();
//    }
//
//    /**
//     * @param deviceIp - scales` ip address
//     * @return string
//     */
//    public String requestName(String deviceIp) {
//        AtomicReference<String> name = new AtomicReference<>();
//        Thread t = new Thread(() -> {
//            try {
//                byte[] response = NetworkHandler.transmitForResponse(deviceIp, buildTransmitMessage(Commands.CMD_GET_NAME, null));
//                if (response == null) return;
//                name.set(parseScalesName(response));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        });
//        t.start();
//        try {
//            t.join(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return name.get();
//    }
//}
