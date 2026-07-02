package top.mddata.sdk.core.aes;

import java.util.ArrayList;

/**
 * 字节数组拼接辅助类。
 * 用于将 randomStr + msgLen + msg + appId 拼接为完整的明文块。
 */
class ByteGroup {
    private final ArrayList<Byte> byteContainer = new ArrayList<>();

    public byte[] toBytes() {
        byte[] bytes = new byte[byteContainer.size()];
        for (int i = 0; i < byteContainer.size(); i++) {
            bytes[i] = byteContainer.get(i);
        }
        return bytes;
    }

    public ByteGroup addBytes(byte[] bytes) {
        for (byte b : bytes) {
            byteContainer.add(b);
        }
        return this;
    }

    public int size() {
        return byteContainer.size();
    }
}
