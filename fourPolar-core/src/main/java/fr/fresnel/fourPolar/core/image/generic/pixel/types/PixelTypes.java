package fr.fresnel.fourPolar.core.image.generic.pixel.types;

public enum PixelTypes {
    FLOAT_32, RGB_16, UINT_16;

    /**
     * Returns an appropriate zero value instance of the {@link PixelTypes}
     * 
     * @return
     */
    public static PixelType create(PixelTypes type) {
        PixelType pixelType = null;
        switch (type) {
            case FLOAT_32:
                pixelType = new Float32(0);
                break;

            case UINT_16:
                pixelType = new UINT16(0);
                break;

            case RGB_16:
                pixelType = new ARGB8(0, 0, 0);
                break;

            default:
                break;
        }
        return pixelType;
    }
}