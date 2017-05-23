package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.Arrays;

public class Path {
    private final String delimiter;
    private final String[] segments;

    public Path(String delimiter, String... segments) {
        this.delimiter = delimiter;
        this.segments = segments;
    }

    public String[] getSegments() {
        return this.segments;
    }

    public boolean startsWith(Path prefix) {
        boolean result = true;
        String[] otherSegments = prefix.getSegments();

        // The prefix is longer than this path. prefix can never be the prefix of this
        if(otherSegments.length > this.segments.length) {
            result = false;
        }

        // Check if both paths start with the same elements
        for(int i = 0; i < this.segments.length && i < otherSegments.length && result; i++) {
            if(!this.segments[i].equals(otherSegments[i])) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Path) {
            return Arrays.equals(this.segments, ((Path) other).getSegments());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.join(delimiter, this.getSegments());
    }

}
