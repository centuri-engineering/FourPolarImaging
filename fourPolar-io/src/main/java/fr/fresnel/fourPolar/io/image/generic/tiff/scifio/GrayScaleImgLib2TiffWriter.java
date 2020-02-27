package fr.fresnel.fourPolar.io.image.generic.tiff.scifio;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelType;
import fr.fresnel.fourPolar.io.image.generic.ImageWriter;
import io.scif.Writer;
import io.scif.config.SCIFIOConfig;
import io.scif.formats.TIFFFormat;
import io.scif.img.ImgSaver;
import io.scif.FormatException;

/**
 * An abstract class for writing grayscale tiff images using the SCIFIO library.
 * 
 * @param <T> extends Pixel type.
 */
public abstract class GrayScaleImgLib2TiffWriter<T extends PixelType> implements ImageWriter<T> {
    final protected ImgSaver _saver;
    final protected SCIFIOConfig _config;
    protected Writer _writer;

    /**
     * An abstract class for writing grayscale tiff images using the SCIFIO library.
     * This constructor creates the save once, which can be used as many times as
     * desired for saving different images of the same time.
     */
    public GrayScaleImgLib2TiffWriter() {
        this._saver = new ImgSaver();
        this._config = this._setSCFIOConfig();

        final TIFFFormat tiffFormat = new TIFFFormat();
        tiffFormat.setContext(this._saver.getContext());
        try {
            _writer = tiffFormat.createWriter();

        } catch (FormatException e) {
            // Will never be caught.
        }
    }

    /**
     * Close all resources when done reading all the files.
     */
    public void close() {
        this._saver.context().dispose();
    }

    /**
     * Sets the configuration for how the image is opened.
     * 
     * @return
     */
    private SCIFIOConfig _setSCFIOConfig() {
        // For the time being, we use the very basic config.
        SCIFIOConfig config = new SCIFIOConfig();
        return config;
    }

    @Override
    public abstract void write(File path, Image<T> image) throws IOException;

    @Override
    public abstract void write(File path, IMetadata metadata, Image<T> tiff) throws IOException;
}