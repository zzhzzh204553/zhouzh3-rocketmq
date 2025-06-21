package com.zhouzh3.rocketmq.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author haig
 */
public class BufferUtils {

    /**
     * 将 ByteBuffer 内容解码为指定 Charset 的字符串，不影响原始缓冲区状态。
     *
     * @param buffer  ByteBuffer 实例
     * @param charset 字符集（例如 StandardCharsets.UTF_8）
     * @return 解码后的字符串
     */
    public static String newString(ByteBuffer buffer, Charset charset) {
        if (buffer == null || charset == null) {
            throw new IllegalArgumentException("buffer 和 charset 不能为 null");
        }
        // 保留原始 buffer 状态
        ByteBuffer duplicate = buffer.duplicate();
        byte[] bytes = new byte[duplicate.remaining()];
        duplicate.get(bytes);
        return new String(bytes, charset);
    }
}
