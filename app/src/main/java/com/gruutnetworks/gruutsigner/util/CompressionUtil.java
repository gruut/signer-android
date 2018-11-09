package com.gruutnetworks.gruutsigner.util;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class CompressionUtil {

    /**
     * Compress byte array data with LZ4
     * @param data original data
     * @return compressed data
     */
    public static byte[] compress(byte[] data) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        final int decompressedLength = data.length;

        // compress data
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);

        return compressed;
    }

    /**
     * Decompress byte array data with LZ4
     * @param data compressed data
     * @param len length of original data
     * @return decompressed data
     */
    public static byte[] decompress(byte[] data, int len) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[len];
        decompressor.decompress(data, 0, restored, 0, len);

        return restored;
    }
}
