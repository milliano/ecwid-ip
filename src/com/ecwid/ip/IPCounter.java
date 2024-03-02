package com.ecwid.ip;

import java.util.ArrayList;
import java.util.List;

public class IPCounter {
    Node rootNode = new Node();
    long ipCount = 0;
    long addedIpCount = 0;

    public void addIP(String ipAddress) {
        byte[] ipAddressParts = parseIP(ipAddress);
        if (ipAddressParts == null) {
            System.out.println("skip wrong ipAddress: " + ipAddress);
            return;
        }
        addedIpCount++;
        Node node2 = rootNode.setValue(ipAddressParts[0], false);
        Node node3 = node2.setValue(ipAddressParts[1], false);
        Node node4 = node3.setValue(ipAddressParts[2], false);
        node4.setValue(ipAddressParts[3], true);
    }

    public long getIPCount() {
        return ipCount;
    }

    public long getAddedIpCount() {
        return addedIpCount;
    }

    private byte[] parseIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return null;
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            int part;
            try {
                part = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (part < 0 || part > 255) return null;
            bytes[i] = (byte) (part - 128);
        }
        return bytes;
    }

    private class Node {
        private final long[] values = new long[4];
        private final List<Node> childNodes = new ArrayList<>();

        public Node setValue(byte value, boolean isLastNode) {
            if (isLastNode) {
                if (isBitSet(values, value)) return null;
                setBit(values, value);
                ipCount++;
                return null;
            }

            int nodePosition = getLeftBitsCount(values, value);

            if (isBitSet(values, value)) {
                return childNodes.get(nodePosition - 1);
            }

            setBit(values, value);
            Node node = new Node();
            childNodes.add(nodePosition, node);
            return node;
        }

        private void setBit(long[] bits, byte value) {
            int positiveValue = value + 128;
            int index = positiveValue / 64;
            int bitOffset = positiveValue % 64;

            bits[index] = bits[index] | (1L << bitOffset);
        }

        private boolean isBitSet(long[] bits, byte value) {
            int positiveValue = value + 128;
            int index = positiveValue / 64;
            int bitOffset = positiveValue % 64;

            long mask = 1L << bitOffset;
            return (bits[index] & mask) != 0;
        }

        private int getLeftBitsCount(long[] bits, byte value) {
            int positiveValue = value + 128;
            int index = positiveValue / 64;
            int offset = positiveValue % 64;

            int bitCount = 0;
            for (int i = bits.length - 1; i > index; i--) {
                bitCount += Long.bitCount(bits[i]);
            }

            long mask = Long.MAX_VALUE << offset;
            bitCount += Long.bitCount(bits[index] & mask);
            return bitCount;
        }
    }
}
