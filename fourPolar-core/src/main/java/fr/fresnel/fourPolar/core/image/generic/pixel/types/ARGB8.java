package fr.fresnel.fourPolar.core.image.generic.pixel.types;

/**
 * This class models the RGB pixel values. Each color has a maximum value of
 * 255.
 */
public class ARGB8 implements PixelType {
    /**
     * Maximum value allowed for each color.
     */
    public static int MAX_VAL = 255;
    public static int MIN_VAL = 0;

    private int _r;
    private int _g;
    private int _b;

    private final static ARGB8 _zero = new ARGB8(0, 0, 0);

    /**
     * Constructs the type with specified values.
     * 
     * @param r
     * @param g
     * @param b
     */
    public ARGB8(int r, int g, int b) {
        this.set(r, g, b);
    }

    public void set(int r, int g, int b) {
        this._r = this._limitColorRange(r);
        this._g = this._limitColorRange(g);
        this._b = this._limitColorRange(b);
    }

    public void set(ARGB8 rgb16) {
        this._r = this._limitColorRange(rgb16._r);
        this._g = this._limitColorRange(rgb16._g);
        this._b = this._limitColorRange(rgb16._b);
    }

    public void add(ARGB8 rgb16) {
        this.set(rgb16._r + this._r, rgb16._g + this._g, rgb16._b + this._b);
    }

    /**
     * Limits the range of color to the range specified by {@code UINT16}
     * 
     * @param color
     * @return
     */
    private int _limitColorRange(int color) {
        int compressedColor = 0;
        if (color < ARGB8.MIN_VAL) {
            compressedColor = 0;
        } else if (color > ARGB8.MAX_VAL) {
            compressedColor = ARGB8.MAX_VAL;
        } else {
            compressedColor = color;
        }

        return compressedColor;

    }

    /**
     * returns the int equivalent of R.
     * 
     * @return
     */
    public int getR() {
        return _r;
    }

    /**
     * returns the int equivalent of G.
     * 
     * @return
     */
    public int getG() {
        return _g;
    }

    /**
     * returns the int equivalent of B.
     * 
     * @return
     */
    public int getB() {
        return _b;
    }

    @Override
    public PixelTypes getType() {
        return PixelTypes.RGB_16;
    }

    /**
     * Return a shared zero instance. Used for initializing types.
     * 
     * @return a shared instance with value zero.
     */
    public static ARGB8 zero() {
        return _zero;
    }

    @Override
    public PixelType copy() {
        return new ARGB8(getR(), getG(), getB());
    }

}