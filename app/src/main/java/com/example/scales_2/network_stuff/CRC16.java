package com.example.scales_2.network_stuff;

import android.util.Log;

public class CRC16 {


    public static void test() {
        short crc = 0;          // initial value
        short polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)
        byte[] testBytes = {33, 21, 45, 0, 0, 80, 85, 45, 68, 67, 13, 10};

        short a, temp;

        for(byte b : testBytes) {
            a = 0;
            temp = (short) (crc & 0xFF00);
            for(int bits = 0; bits < 8; bits++) {
                if(((temp ^ a) & 0x8000) == 0x8000)
                    a = (short)((a << 1) ^ polynomial);
                else
                    a <<= 1;
                temp <<= 1;
            }
            crc = (short) (a ^ (crc << 8) ^ (b & 0xFF));
            Log.e("TAG", "steps: " + crc);

        }


        Log.e("TAG", "test: " + crc);
    }
}
