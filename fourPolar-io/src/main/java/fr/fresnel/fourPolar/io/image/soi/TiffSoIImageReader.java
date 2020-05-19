package fr.fresnel.fourPolar.io.image.soi;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.soi.ISoIImage;
import fr.fresnel.fourPolar.core.image.soi.SoIImage;
import fr.fresnel.fourPolar.core.physics.channel.ChannelUtils;
import fr.fresnel.fourPolar.io.exceptions.image.generic.NoReaderFoundForImage;
import fr.fresnel.fourPolar.io.exceptions.image.generic.metadata.MetadataParseError;
import fr.fresnel.fourPolar.io.image.generic.ImageReader;
import fr.fresnel.fourPolar.io.image.generic.tiff.TiffImageReaderFactory;
import fr.fresnel.fourPolar.io.image.soi.file.ISoIImageFile;
import fr.fresnel.fourPolar.io.image.soi.file.TiffSoIImageFile;

/**
 * A concrete implementation of {@link ISoIImageReader} to read tiff SoI image.
 */
public class TiffSoIImageReader implements ISoIImageReader {
    final private ImageReader<UINT16> _reader;
    final private int _numChannels;

    public TiffSoIImageReader(ImageFactory factory, int numChannels) throws NoReaderFoundForImage {
        this._reader = TiffImageReaderFactory.getReader(factory, UINT16.zero());
        this._numChannels = numChannels;
    }

    @Override
    public ISoIImage read(File root4PProject, ICapturedImageFileSet fileSet, int channel) throws IOException {
        ChannelUtils.checkChannel(channel, _numChannels);
        ISoIImageFile file = new TiffSoIImageFile(root4PProject, fileSet, channel);

        Image<UINT16> soi = null;
        try {
            soi = this._reader.read(file.getFile());
        } catch (MetadataParseError | IOException e) {
            throw new IOException("SoI images doesn't exist or is corrupted");
        }
        return SoIImage.create(fileSet, soi, channel);
    }

    @Override
    public void close() throws IOException {
        this._reader.close();
    }

}