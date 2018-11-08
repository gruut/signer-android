package com.gruutnetworks.gruutsigner.util;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class CompressionUtil {

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

    public static byte[] decompress(byte[] data, int len) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[len];
        decompressor.decompress(data, 0, restored, 0, len);

        return restored;
    }

    // not work...
    public static byte[] decompress(byte[] data) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        final int compressedLength = data.length;

        // decompress data
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] restored = new byte[compressedLength * 4];
        decompressor.decompress(data, 0, compressedLength, restored, 0, compressedLength * 4);

        return restored;
    }
}
