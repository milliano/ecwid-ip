package com.ecwid.ip;

import java.util.ArrayList;
import java.util.List;

public class IPCounter {
    private final Node rootNode = new Node();
    private long ipCount = 0;
    private long addedIpCount = 0;

    public long getIPCount() {
        return ipCount;
    }

    public long getAddedIpCount() {
        return addedIpCount;
    }

    public void addIP(String ipAddress) {
        int[] ipAddressParts = parseIP(ipAddress);
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

    private int[] parseIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return null;
        int[] bytes = new int[4];
        for (int i = 0; i < 4; i++) {
            int part;
            try {
                part = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (part < 0 || part > 255) return null;
            bytes[i] = part;
        }
        return bytes;
    }

    private class Node {
        private final long[] values = new long[4];
        private final List<Node> childNodes = new ArrayList<>();

        public Node setValue(int value, boolean isLastNode) {
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

        private void setBit(long[] bits, int value) {
            int index = value / 64;
            int offset = value % 64;

            bits[index] = bits[index] | (1L << offset);
        }

        private boolean isBitSet(long[] bits, int value) {
            int index = value / 64;
            int offset = value % 64;

            long mask = 1L << offset;
            return (bits[index] & mask) != 0;
        }

        private int getLeftBitsCount(long[] bits, int value) {
            int index = value / 64;
            int offset = value % 64;

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