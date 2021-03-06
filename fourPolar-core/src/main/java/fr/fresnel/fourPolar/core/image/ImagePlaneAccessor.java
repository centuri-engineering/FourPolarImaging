package fr.fresnel.fourPolar.core.image;

/**
 * An interface for accessing the planes of
 * 
 * @param <T>
 */
public interface ImagePlaneAccessor<T> {
    /**
     * Returns the image plane this particular position belongs to. It's the
     * responsibilty of the caller to ensure that this position is consistent with
     * the dimension of the image.
     * 
     * @param position is the position.
     * @return the index of the plane this position belongs to.
     * @throws IllegalArgumentException if position does not have same number of
     *                                  dimension as image.
     */
    public int getPlaneIndex(long[] position);

    /**
     * Returns the plane corresponding to the given index.
     * 
     * @param planeIndex is the index of the plane, which is greater than zero.
     * @throws IndexOutOfBoundsException if the plane index is less than zero or
     *                                   does not exist.
     */
    public ImagePlane<T> getImagePlane(int planeIndex);

    /**
     * @return the total number of planes.
     */
    public int numPlanes();
}
