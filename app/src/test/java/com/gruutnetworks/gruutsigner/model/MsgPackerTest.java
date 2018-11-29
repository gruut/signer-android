package com.gruutnetworks.gruutsigner.model;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.*;

public class MsgPackerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    byte[] getBodyByteArr(byte[] bytes) {
        int offset = 0;
        MsgHeader header = new MsgHeader.Builder()
                .setGruutConstant(bytes[offset++])
                .setMainVersion((byte) (bytes[offset] >> 4))
                .setSubVersion((byte) (bytes[offset++] & 0x0f))
                .setMsgType(bytes[offset++])
                .setMacType(bytes[offset++])
                .setCompressionType(bytes[offset++])
                .setNotUsed(bytes[offset++])
                .setTotalLen(Arrays.copyOfRange(bytes, offset, offset += HEADER_TOTAL_LEN_SIZE))
                .setLocalChainId(Arrays.copyOfRange(bytes, offset, offset += HEADER_LOCAL_CHAIN_ID_SIZE))
                .setSender(Arrays.copyOfRange(bytes, offset, offset += HEADER_SENDER_SIZE))
                .setReserved(Arrays.copyOfRange(bytes, offset, offset += HEADER_RESERVED_SIZE))
                .build();

        byte[] compressedMsg = Arrays.copyOfRange(bytes, offset, offset += (header.getTotalLen() - MSG_HEADER_LEN));

        byte[] mac = Arrays.copyOfRange(bytes, offset, bytes.length);
        byte[] body = decompressData(header.getCompressType(), compressedMsg);

        return body;
    }

    private byte[] decompressData(TypeComp typeComp, byte[] data) {
        switch (typeComp) {
            case LZ4:
                return CompressionUtil.decompress(data);
            case NONE:
            default:
                return data;
        }
    }
}