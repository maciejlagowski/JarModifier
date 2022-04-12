package io.github.maciejlagowski.jarmodifier.util;

public class StringBytesConverter {

    public static String getStringFromBytes(byte[] tab) {
        StringBuilder sb = new StringBuilder();
        for (byte b : tab) {
            sb.append((char) b);
        }
        return sb.toString();
    }
}
