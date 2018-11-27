package com.gruutnetworks.gruutsigner.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CompressionUtilTest {

    String testStr = "{\n" +
            "  \"title\": \"Up\",\n" +
            "  \"description\": \"Merger가 Merger Network에 조인할 때\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"mID\": {\n" +
            "      \"description\": \"송신자 Merger\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"time\": {\n" +
            "      \"description\": \"송신 시간\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"ver\": {\n" +
            "      \"description\": \"version\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"cID\": {\n" +
            "      \"description\": \"local chain ID\",\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"required\": [\n" +
            "    \"mID\",\n" +
            "    \"time\",\n" +
            "    \"ver\",\n" +
            "    \"cID\"\n" +
            "  ]\n" +
            "}";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void compressAndDecompress() {
        byte[] targetStr = testStr.getBytes();
        int originalLen = targetStr.length;

        byte[] compressed = CompressionUtil.compress(testStr.getBytes());
        assertThat(CompressionUtil.decompress(compressed, originalLen), is(targetStr));
    }
}