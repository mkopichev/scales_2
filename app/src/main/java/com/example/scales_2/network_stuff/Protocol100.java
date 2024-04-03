package com.example.scales_2.network_stuff;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public abstract class Protocol100 {

    Context context;

    public enum Commands {
        CMD_GET_NAME,
        CMD_GET_MASS
    }

    /**
     * @param command - command
     * @param args    - args
     * @return buf.array
     */
    public static byte[] buildTransmitMessage(Commands command, byte[] args) {
        ByteBuffer buf;
        switch (command) {
            case CMD_GET_NAME:
                buf = ByteBuffer.allocate(8)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) 0xF8)
                        .put((byte) 0x55)
                        .put((byte) 0xCE)
                        .putShort((short) 1)
                        .put((byte) 0x20)
                        .putShort(calculateCRC(new byte[]{0x20}));
                return buf.array();

            case CMD_GET_MASS:
                buf = ByteBuffer.allocate(8)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) 0xF8)
                        .put((byte) 0x55)
                        .put((byte) 0xCE)
                        .putShort((short) 1)
                        .put((byte) 0x23)
                        .putShort(calculateCRC(new byte[]{0x23}));
                return buf.array();
        }
        return null;
    }

    /**
     * @param buffer - buffer
     * @return crc
     */
    public static short calculateCRC(final byte[] buffer) {

        short crc = 0;          // initial value
        short polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)
        short a, temp;

        for (byte b : buffer) {
            a = 0;
            temp = (short) (crc & 0xFF00);
            for (int bits = 0; bits < 8; bits++) {
                if (((temp ^ a) & 0x8000) == 0x8000)
                    a = (short) ((a << 1) ^ polynomial);
                else
                    a <<= 1;
                temp <<= 1;
            }
            crc = (short) (a ^ (crc << 8) ^ (b & 0xFF));
        }
        return crc;
    }

    /**
     * @param buff - buff
     * @return scales name
     */
    public static String parseScalesName(byte[] buff) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN);
        short length = byteBuffer.getShort(0);
        short crc = byteBuffer.getShort(length + 2);
        if (crc != calculateCRC(Arrays.copyOfRange(buff, 2, length + 2))) return null;
        return new String(Arrays.copyOfRange(buff, 7, length), Charset.defaultCharset());
    }

    /**
     * @param buff - buff
     * @return mass
     */
    public static int parseMass(byte[] buff) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN);

            short length = byteBuffer.getShort(0);
            short crc = byteBuffer.getShort(length + 2);
            if (crc != calculateCRC(Arrays.copyOfRange(buff, 2, length + 2))) return -1;
            if (byteBuffer.getChar(8) == 0) return -2;
            if (byteBuffer.getChar(10) == 1) return -2;
            return byteBuffer.getInt(3);
        } catch (Exception e) {
            return -1;
        }
    }

}
