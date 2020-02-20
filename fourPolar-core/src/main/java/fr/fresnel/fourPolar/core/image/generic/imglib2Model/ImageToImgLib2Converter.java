package fr.fresnel.fourPolar.core.image.generic.imglib2Model;

import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.float32;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.uint16;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Using this class, we can convert an {@code Image} interface to a {@code Img} interface.
 */
public class ImageToImgLib2Converter {
    /**
     * This method transforms {@code Image} of type uint16 to {@code Img} of type
     * UnsignedShortType.
     * 
     * @param image is the {@code Image} instance. 
     * @param pixelType is the pixel type associated with the image.
     * @return
     */
    public static Img<UnsignedShortType> getImg(Image<uint16> image, uint16 pixelType) {
        Img<UnsignedShortType> img = null;

        if (image instanceof ImgLib2Image) {
            img = (Img<UnsignedShortType>) (image);
        }

        return img;
    }

    /**
     * This method transforms {@code Image} of type float32 to {@code Img} of type
     * FloatType.
     * 
     * @param image is the {@code Image} instance. 
     * @param pixelType is the pixel type associated with the image.
     * @return
     */
    public static Img<FloatType> getImg(Image<float32> image, float32 pixelType) {
        Img<FloatType> img = null;

        if (image instanceof ImgLib2Image) {
            img = (Img<FloatType>) (image);
        }

        return img;
    }

    /**
     * This method transforms {@code Image} of type RGB16 to {@code Img} of type
     * ARGBType.
     * 
     * @param image is the {@code Image} instance. 
     * @param pixelType is the pixel type associated with the image.
     * @return
     */
    public static Img<ARGBType> getImg(Image<RGB16> image, RGB16 pixelType) {
        Img<ARGBType> img = null;

        if (image instanceof ImgLib2Image) {
            img = (Img<ARGBType>) (image);
        }

        return img;
    }
}