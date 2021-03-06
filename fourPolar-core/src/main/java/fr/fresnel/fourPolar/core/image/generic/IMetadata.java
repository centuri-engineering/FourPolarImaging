package fr.fresnel.fourPolar.core.image.generic;

import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;

/**
 * An interface for accessing tiff metadata.
 */
public interface IMetadata {
    /**
     * Returns the order of axis associated with the image.
     */
    public AxisOrder axisOrder();

    /**
     * Returns number of channels associated with the image. If no {@link AxisOrder}
     * is defined, returns -1.
     */
    public int numChannels();

    /**
     * Get number of bits per pixel.
     */
    public int bitPerPixel();

    /**
     * Get dimension of the associated image.
     */
    public long[] getDim();

}