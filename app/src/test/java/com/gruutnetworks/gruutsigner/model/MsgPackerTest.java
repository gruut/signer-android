package com.gruutnetworks.gruutsigner.model;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;
import com.gruutnetworks.gruutsigner.util.KeystoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.*;

public class MsgPackerTest {

    MsgHeader header;
    byte[] compressedMsg;
    byte[] mac;
    byte[] body;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    byte[] getBodyByteArr(byte[] bytes) {
        int offset = 0;
        header = new Builder()
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

        compressedMsg = Arrays.copyOfRange(bytes, offset, offset += (header.getTotalLen() - MSG_HEADER_LEN));

        mac = Arrays.copyOfRange(bytes, offset, bytes.length);
        body = decompressData(header.getCompressType(), compressedMsg);

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

    boolean checkMacValidity(MsgHeader header, byte[] compressedData, byte[] mac) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header.convertToByteArr());
            outputStream.write(compressedData);

            byte[] headerAndBody = outputStream.toByteArray();
            outputStream.close();

            return KeystoreUtil.verifyHmacSignature(headerAndBody, mac);
        } catch (IOException e) {
            return false;
        }
    }
}