package com.gruutnetworks.gruutsigner.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
    public void compressAndFastDecompress() {
        // 압축한 후 풀어서 원본 데이터와 비교
        byte[] targetStr = testStr.getBytes();
        int len = targetStr.length;
        byte[] compressed = CompressionUtil.compress(targetStr);
        assertThat(CompressionUtil.decompress(compressed, len), is(targetStr));
    }

    @Test
    public void compressAndSafeDecompress() {
        // 압축한 후 풀어서 원본 데이터와 비교
        byte[] targetStr = testStr.getBytes();
        byte[] compressed = CompressionUtil.compress(targetStr);
        assertThat(CompressionUtil.decompress(compressed), is(targetStr));
    }
}