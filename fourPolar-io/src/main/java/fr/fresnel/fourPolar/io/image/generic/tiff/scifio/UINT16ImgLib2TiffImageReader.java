package fr.fresnel.fourPolar.io.image.generic.tiff.scifio;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImgLib2ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * This class reads a 16 bit unsigned image using SCIFIO library.
 */
public class UINT16ImgLib2TiffImageReader extends GrayScaleImgLib2TiffReader<UINT16> {
    final private UnsignedShortType imgLib2Type = new UnsignedShortType();

    public UINT16ImgLib2TiffImageReader(ImgLib2ImageFactory factory) {
        super(factory);
    }

    @Override
    public Image<UINT16> read(File path) throws IOException {
        this._reader.setSource(path.getAbsolutePath(), this._config);
        final Img<UnsignedShortType> img = this._imgOpener.openImgs(_reader, imgLib2Type, this._config).get(0);

        return this._imgFactory.create(img, imgLib2Type);
    }

    @Override
    public void close() throws IOException {
        this._reader.close();

    }

}