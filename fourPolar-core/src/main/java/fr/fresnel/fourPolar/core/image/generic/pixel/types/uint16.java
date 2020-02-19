package fr.fresnel.fourPolar.core.image.generic.pixel.types;

/**
 * This class models a pixel of unsigned short type, whose range is from 0 to
 * 2^16-1. Note that summation results in saturation rather than overflow,
 * meaning that if the summation results in a value greater than 2^16-1, it will
 * be rounded to 2^16-1,
 */
public class uint16 extends PixelType {
    public static int MIN_VAL = 0;
    public static int MAX_VAL = 65535;

    private int _pixel = 0;

    public uint16(int value) {
        this.set(value);
    }

    public void set(int value) {
        if (value < 0) {
            this._pixel = 0;
        } else if (value > MAX_VAL) {
            this._pixel = MAX_VAL;
        } else {
            this._pixel = value;
        }
    }

    public int get() {
        return this._pixel;
    }

    /**
     * Sums the given pixel with this pixel. The result is rounded to
     * {@value MAX_VAL} if exceeds {@value MAX_VAL}.
     * 
     * @param pixel
     */
    public void sum(uint16 pixel) {
        this.set(pixel.get() + this.get());
    }

    /**
     * Subtracts the given pixel value from this pixel.
     * 
     * @param pixel
     */
    public void subtract(uint16 pixel) {
        this.set(this.get() - pixel.get());
    }

    @Override
    public Type getType() {
        return Type.UINT16;
    }

}