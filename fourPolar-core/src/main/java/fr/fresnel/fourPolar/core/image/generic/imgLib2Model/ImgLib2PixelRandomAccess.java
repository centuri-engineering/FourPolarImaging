package fr.fresnel.fourPolar.core.image.generic.imgLib2Model;

import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.types.TypeConverter;
import fr.fresnel.fourPolar.core.image.generic.pixel.IPixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.Pixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelType;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelTypes;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;

/**
 * Implementation of {@code IPixelRandomAccess} for the ImgLib2 image.
 * 
 * @param <T> extens {@link PixelType}.
 * @param <T> is the generic native type of ImgLib2. Note that only a handful of
 *            datatypes are supported. See {@code TypeConverterFactory} .
 * 
 */
class ImgLib2PixelRandomAccess<U extends PixelType, T extends NativeType<T>> implements IPixelRandomAccess<U> {
    final private RandomAccess<T> _rAccess;
    final private TypeConverter<U, T> _tConverter;
    final private IPixel<U> _pixel;
    final private int _numDim;

    /**
     * Implementation of {@code IPixelRandomAccess} for the ImgLib2 image.
     * 
     * @param randomAccess is the RandomAccess class of ImgLib2.
     * @param converter    is the converter between ImgLib2 data types and our data
     *                     types.
     */
    @SuppressWarnings("unchecked")
    public ImgLib2PixelRandomAccess(final RandomAccess<T> randomAccess, final TypeConverter<U, T> converter) {
        _rAccess = randomAccess;
        _tConverter = converter;
        _numDim = _rAccess.numDimensions();
        _pixel = new Pixel<U>((U)PixelTypes.create(converter.getPixelType()));
    }

    @Override
    public void setPosition(long[] position) {
        if (position.length != this._numDim) {
            throw new IllegalArgumentException("The given position does not have same dimension as the image.");
        }

        this._rAccess.setPosition(position);
    }

    @Override
    public void setPixel(IPixel<U> pixel) throws ArrayIndexOutOfBoundsException {
        try {
            this._tConverter.setNativeType(pixel.value(), this._rAccess.get());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("The given pixel position does not exist");
        }

    }

    @Override
    public IPixel<U> getPixel() throws ArrayIndexOutOfBoundsException {
        try {
            this._tConverter.setPixelType(this._rAccess.get(), _pixel.value());
            return _pixel;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("The given pixel position does not exist");
        }
    }

}