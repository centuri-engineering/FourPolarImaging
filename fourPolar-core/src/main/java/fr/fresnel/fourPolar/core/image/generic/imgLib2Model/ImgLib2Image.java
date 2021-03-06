package fr.fresnel.fourPolar.core.image.generic.imgLib2Model;

import java.util.Arrays;
import java.util.Objects;

import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.types.TypeConverter;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelType;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.view.Views;

/**
 * Implementation of {@code Image} for the ImgLib2 image.
 * 
 * @param <U> is our pixel data type.
 * @param <V> is the ImgLib2 data type.
 */
class ImgLib2Image<U extends PixelType, V extends NativeType<V>> implements Image<U> {
    private final Img<V> _img;
    private final IMetadata _metadata;
    private final TypeConverter<U, V> _tConverter;

    private final ImageFactory _factory;
    final private long[] _dim;

    /**
     * This constructor is works as a wrapper from ImgLib2 type to our type.
     * 
     * @param img        is the ImgLib2 interface.
     * @param tConverter is the appropriate converter from ImgLib2 type to
     *                   {@link PixelType}.
     * @param factory    is the associated {@link ImageFactory}.
     * @param metadata   is the metadata associated with this image.
     */
    ImgLib2Image(final Img<V> img, final TypeConverter<U, V> tConverter, final ImageFactory factory,
            final IMetadata metadata) {
        this._img = img;
        this._tConverter = tConverter;
        this._factory = factory;
        this._metadata = metadata;
        this._dim = metadata.getDim();
        this._isMetadataConsistentWithImage();
    }

    @Override
    public IPixelRandomAccess<U> getRandomAccess() {
        return new ImgLib2PixelRandomAccess<U, V>(this._img.randomAccess(), this._tConverter);
    }

    @Override
    public IPixelCursor<U> getCursor() {
        return new ImgLib2PixelCursor<U, V>(this._img.cursor(), this.getMetadata().getDim(), this._tConverter);
    }

    @Override
    public String toString() {
        return _img.toString();
    }

    /**
     * This method returns the {@link Img} interface associated with this
     * implementation.
     */
    public Img<V> getImg() {
        return this._img;
    }

    @Override
    public ImageFactory getFactory() {
        return _factory;
    }

    @Override
    public IPixelCursor<U> getCursor(long[] bottomCorner, long[] len) throws IllegalArgumentException {
        Objects.requireNonNull(bottomCorner, "bottomCorner can't be null");
        Objects.requireNonNull(len, "len can't be null");

        if (bottomCorner.length != this._dim.length || len.length != this._dim.length) {
            throw new IllegalArgumentException("bottom corner or does not have same dimension as image.");
        }

        boolean bottomCornerHasNegative = Arrays.stream(bottomCorner).summaryStatistics().getMin() < 0;
        if (bottomCornerHasNegative) {
            throw new IllegalArgumentException("bottom corner or can't be negative.");
        }

        boolean lenIsPositiveNegative = Arrays.stream(len).summaryStatistics().getMin() > 0;
        if (!lenIsPositiveNegative) {
            throw new IllegalArgumentException("Length must be at least one");
        }

        for (int i = 0; i < bottomCorner.length; i++) {
            if (bottomCorner[i] + len[i] > this._dim[i])
                throw new IllegalArgumentException("bottomCorner + len cannot exceed image dimension.");
        }

        Cursor<V> cursor = Views.iterable(Views.offsetInterval(this._img, bottomCorner, len)).cursor();
        return new ImgLib2PixelCursor<>(cursor, this._dim, this._tConverter);
    }

    @Override
    public IMetadata getMetadata() {
        return this._metadata;
    }

    private void _isMetadataConsistentWithImage() {
        if (this._metadata.axisOrder() != AxisOrder.NoOrder
                && this._metadata.axisOrder().numAxis != this._dim.length) {
            throw new IllegalArgumentException("Number of axis in metadata AxisOrder should equal image dimension");
        }

    }

}