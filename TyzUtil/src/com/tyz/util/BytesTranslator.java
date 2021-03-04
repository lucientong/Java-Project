package com.tyz.util;

/**
 * 字节流转换器，将其他类型的数据转成二进制数组
 * 将二进制数组转换成普通数据
 *
 * @author tyz
 */
public class BytesTranslator {
    private static final int SHORT_BYTE_COUNT = 2;
    private static final int INT_BYTE_COUNT = 4;
    private static final int LONG_BYTE_COUNT = 8;

    public BytesTranslator() {
    }

    /**
     * 将int型数据转换成二进制数组 (4 * 8b)
     *
     * @param value 需要转换的int类型数据
     * @return 数据转换的二进制数组
     */
    public static byte[] toBytes(int value) {
        byte[] res = new byte[INT_BYTE_COUNT];

        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return res;
    }

    /**
     * 将int型数据转换成二进制，加到数组 {@code bytes}
     * 中从 {@code offset} 开始的地方
     *
     * @param value 需要转换的int型数据
     * @return 添加数据后的二进制数组
     */
    public static byte[] toBytes(byte[] bytes, int offset, int value) {
        for (int i = 0; i < INT_BYTE_COUNT; i++) {
            bytes[i + offset] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 将long型数据转换成二进制数组 (8 * 8b)
     *
     * @param value 需要转换的long型数据
     * @return 数据转换的二进制数组
     */
    public static byte[] toBytes(long value) {
        byte[] res = new byte[LONG_BYTE_COUNT];

        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return res;
    }

    /**
     * 将long型数据转换成二进制，加到数组 {@code bytes}
     * 中从 {@code offset} 开始的地方
     *
     * @param value 需要转换的long型数据
     * @return 添加数据后的二进制数组
     */
    public static byte[] toBytes(byte[] bytes, int offset, long value) {
        for (int i = 0; i < LONG_BYTE_COUNT; i++) {
            bytes[i + offset] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 将short型数据转换成二进制数组 (2 * 8b)
     *
     * @param value 需要转换的short型数据
     * @return 数据转换的二进制数组
     */
    public static byte[] toBytes(short value) {
        byte[] res = new byte[SHORT_BYTE_COUNT];

        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return res;
    }

    /**
     * 将short型数据转换成二进制，加到数组 {@code bytes}
     * 中从 {@code offset} 开始的地方
     *
     * @param value 需要转换的short型数据
     * @return 添加数据后的二进制数组
     */
    public static byte[] toBytes(byte[] bytes, int offset, short value) {
        for (int i = 0; i < LONG_BYTE_COUNT; i++) {
            bytes[i + offset] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 将二进制数组 {@code bytes} 转换成十进制的 int值
     *
     * @param bytes 需要转换的二进制数组
     * @return 转换的 int型 值
     */
    public static int toInt(byte[] bytes) {
        if (bytes.length != INT_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes length [" +
                    bytes.length + "] is not 4, can't transfer to [int].");
        }
        int res = 0;

        for (int i = 0; i < INT_BYTE_COUNT; i++) {
            res |= (bytes[i] << (i << 3)) & (0xFF << (i << 3));
        }
        return res;
    }

    /**
     * 将二进制数组 {@code bytes} 从 {@code offset} 起后四位，
     * 转换成 int型 的十进制数。
     *
     * @param bytes 需要转换的二进制数组
     * @param offset 起始下标偏移量
     * @return 转换的 int型 值
     */
    public static int toInt(byte[] bytes, int offset) {
        if (bytes.length - offset < INT_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes rest length [" +
                    (bytes.length - offset) + "] is less than 4, can't transfer to [int].");
        }
        byte[] tempBytes = new byte[INT_BYTE_COUNT];

        System.arraycopy(bytes, offset, tempBytes, 0, tempBytes.length);

        return toInt(tempBytes);
    }

    /**
     * 将二进制数组 {@code bytes} 转换成十进制的 long值
     *
     * @param bytes 需要转换的二进制数组
     * @return 转换的 long型 值
     */
    public static int toLong(byte[] bytes) {
        if (bytes.length != LONG_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes length [" +
                    bytes.length + "] is not 8, can't transfer to [long].");
        }
        int res = 0;

        for (int i = 0; i < LONG_BYTE_COUNT; i++) {
            res |= (bytes[i] << (i << 3)) & (0xFF << (i << 3));
        }
        return res;
    }

    /**
     * 将二进制数组 {@code bytes} 从 {@code offset} 起后四位，
     * 转换成 long型 的十进制数。
     *
     * @param bytes 需要转换的二进制数组
     * @param offset 起始下标偏移量
     * @return 转换的 long型 值
     */
    public static int toLong(byte[] bytes, int offset) {
        if (bytes.length - offset < LONG_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes rest length [" +
                    (bytes.length - offset) + "] is less than 8, can't transfer to [long].");
        }
        byte[] tempBytes = new byte[LONG_BYTE_COUNT];

        System.arraycopy(bytes, offset, tempBytes, 0, tempBytes.length);

        return toLong(tempBytes);
    }

    /**
     * 将二进制数组 {@code bytes} 转换成十进制的 short值
     *
     * @param bytes 需要转换的二进制数组
     * @return 转换的 short型 值
     */
    public static int toShort(byte[] bytes) {
        if (bytes.length != SHORT_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes length [" +
                    bytes.length + "] is not 2, can't transfer to [short].");
        }
        int res = 0;

        for (int i = 0; i < SHORT_BYTE_COUNT; i++) {
            res |= (bytes[i] << (i << 3)) & (0xFF << (i << 3));
        }
        return res;
    }

    /**
     * 将二进制数组 {@code bytes} 从 {@code offset} 起后四位，
     * 转换成 short型 的十进制数。
     *
     * @param bytes 需要转换的二进制数组
     * @param offset 起始下标偏移量
     * @return 转换的 short型 值
     */
    public static int toShort(byte[] bytes, int offset) {
        if (bytes.length - offset < SHORT_BYTE_COUNT) {
            throw new InvalidBytesNumberException("bytes rest length [" +
                    (bytes.length - offset) + "] is less than 2, can't transfer to [short].");
        }
        byte[] tempBytes = new byte[SHORT_BYTE_COUNT];

        System.arraycopy(bytes, offset, tempBytes, 0, tempBytes.length);

        return toShort(tempBytes);
    }
}
