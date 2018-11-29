package com.gruutnetworks.gruutsigner.util;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class CompressionUtil {

    /**
     * Compress byte array data with LZ4
     *
     * @param data original data
     * @return compressed data
     */
    public static byte[] compress(byte[] data) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4Compressor compressor = factory.fastCompressor();

        return compressor.compress(data);
    }

    /**
     * Decompress byte array data with LZ4
     * when the decompressed length is known
     *
     * @param data compressed data
     * @param len  length of original data
     * @return decompressed data
     */
    public static byte[] decompress(byte[] data, int len) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4FastDecompressor decompressor = factory.fastDecompressor();

        return decompressor.decompress(data, len);
    }

    /**
     * Decompress byte array data with LZ4
     * when the decompressed length is unknown
     *
     * @param data compressed data
     * @return decompressed data
     */
    public static byte[] decompress(byte[] data) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();

        // 압축 후 최대 크기는 압축된 데이터의 2배라고 가정.
        return decompressor.decompress(data, data.length * 2);
    }
}
