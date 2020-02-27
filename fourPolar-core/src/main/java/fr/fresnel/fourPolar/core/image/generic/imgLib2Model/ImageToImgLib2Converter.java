package fr.fresnel.fourPolar.core.image.generic.imgLib2Model;

import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.Float32;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Using this class, we can convert an {@code Image} interface to a {@code Img}
 * interface.
 */
public class ImageToImgLib2Converter {
    /**
     * This method transforms {@code Image} of type UINT16 to {@code Img} of type
     * UnsignedShortType.
     * 
     * @param image     is the {@code Image} instance.
     * @param pixelType is the pixel type associated with the image.
     * @return the {@link Img} interface, which is the basic image interface of
     *         ImgLib2.
     */
    public static Img<UnsignedShortType> getImg(Image<UINT16> image, UINT16 pixelType) {
        Img<UnsignedShortType> img = null;

        if (image instanceof ImgLib2Image) {
            ImgLib2Image<UINT16, UnsignedShortType> implementation = (ImgLib2Image<UINT16, UnsignedShortType>)image;
            img = implementation.getImg();
        }

        return img;
    }

    /**
     * This method transforms {@code Image} of type Float32 to {@code Img} of type
     * FloatType.
     * 
     * @param image     is the {@code Image} instance.
     * @param pixelType is the pixel type associated with the image.
     * @return the {@link Img} interface, which is the basic image interface of
     *         ImgLib2.
     */
    public static Img<FloatType> getImg(Image<Float32> image, Float32 pixelType) {
        Img<FloatType> img = null;

        if (image instanceof ImgLib2Image) {
            ImgLib2Image<Float32, FloatType> implementation = (ImgLib2Image<Float32, FloatType>)image;
            img = implementation.getImg();
        }

        return img;
    }

    /**
     * This method transforms {@code Image} of type RGB16 to {@code Img} of type
     * ARGBType.
     * 
     * @param image     is the {@code Image} instance.
     * @param pixelType is the pixel type associated with the image.
     * @return the {@link Img} interface, which is the basic image interface of
     *         ImgLib2.
     */
    public static Img<ARGBType> getImg(Image<RGB16> image, RGB16 pixelType) {
        Img<ARGBType> img = null;

        if (image instanceof ImgLib2Image) {
            ImgLib2Image<RGB16, ARGBType> implementation = (ImgLib2Image<RGB16, ARGBType>)image;
            img = implementation.getImg();
        }

        return img;
    }
}