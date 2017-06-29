package org.inaetics.dronessimulator.discovery.api.tree;

import java.util.Arrays;

/**
 * Abstract class representing a path in a tree. Provides methods to build path instances from strings.
 *
 * @param <C> The path implementation to use as type for the methods.
 */
public abstract class Path<C extends Path> {
    /** The path delimiter to use. */
    private final String delimiter;

    /** The segments of this path. */
    private final String[] segments;

    /**
     * Instantiates a new path with the given delimiter and segments.
     * @param delimiter The path delimiter to use.
     * @param segments The segments of this path.
     */
    public Path(String delimiter, String... segments) {
        this.delimiter = delimiter;
        this.segments = segments;
    }

    /**
     * Returns the path delimiter used in this path.
     * @return The path delimiter.
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * Returns the segments this path consists of.
     * @return The path segments.
     */
    public String[] getSegments() {
        return this.segments;
    }

    /**
     * Tests whether this path starts with the given prefix path. Returns true if and only if this path is a direct or
     * indirect child of the given path.
     * @param prefix The path to test.
     * @return Whether this path is a direct or indirect child of the given path.
     */
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

    /**
     * Builds a new path from this path and the given path. Appends the given path to the end of this path. Requires the
     * delimiters of both paths to be equal.
     * @param other The path to append.
     * @return A new path instance for the joined path.
     */
    public C join(Path other) {
        return this.addSegments(other.getSegments());
    }

    /**
     * Returns a new path with the given segments appended to the segments of this path.
     * @param newSegments The segments to append.
     * @return A copy of this path with the given segments appended to it.
     */
    public C addSegments(String... newSegments) {
        String[] pathSegments = Arrays.copyOf(this.segments, this.segments.length + newSegments.length);
        System.arraycopy(newSegments, 0, pathSegments, this.segments.length, newSegments.length);
        return this.newChild(this.getDelimiter(), pathSegments);
    }

    /**
     * Tests whether two paths are equal. Two paths are equal if and only if have the same segments in the same order.
     * The delimiter does not necessarily have to be equal.
     * @param other The path to test.
     * @return Whether this path and the given path have the same segments.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Path
            && delimiter.equals(((Path) other).getDelimiter())
            && Arrays.equals(this.segments, ((Path) other).getSegments());
    }

    public int hashCode() {
        return Arrays.hashCode(this.segments) + delimiter.hashCode();
    }

    @Override
    public String toString() {
        return String.join(delimiter, this.getSegments());
    }

    /**
     * Builds a new path object of type C
     * @param delimiter The delimiter to use for the new path.
     * @param segments The segments to add to the path.
     * @return The created child path.
     */
    protected abstract C newChild(String delimiter, String[] segments);
}
