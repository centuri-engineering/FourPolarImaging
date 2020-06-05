package fr.fresnel.fourPolar.io.visualization.figures.gaugeFigure.tiff;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureFactory;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.AngleGaugeType;
import fr.fresnel.fourPolar.io.exceptions.image.generic.metadata.MetadataParseError;
import fr.fresnel.fourPolar.io.image.generic.ImageReader;
import fr.fresnel.fourPolar.io.image.generic.tiff.TiffImageReaderFactory;
import fr.fresnel.fourPolar.io.visualization.figures.gaugeFigure.IGaugeFigureReader;

/**
 * A concrete implementation of {@link IGaugeFigureReader} that reads the images
 * as tif.
 */
public class TiffGaugeFigureReader implements IGaugeFigureReader {
    private final AngleGaugeType _angleGaugeType;
    private final GaugeFigureType _gaugeFigureType;

    private ImageReader<RGB16> _reader;

    /**
     * Initializes the reader to read a particular form of gauge figure.
     * 
     * @param imageFactory    is the desired image model to be used.
     * @param gaugeFigureType is the type of gauge figure to be read.
     * @param angleGaugeType  is the type of angle gauge associated with the gauge
     *                        figure.
     */
    public TiffGaugeFigureReader(ImageFactory imageFactory, GaugeFigureType gaugeFigureType,
            AngleGaugeType angleGaugeType) {
        this._reader = TiffImageReaderFactory.getReader(imageFactory, RGB16.zero());
        this._gaugeFigureType = gaugeFigureType;
        this._angleGaugeType = angleGaugeType;
    }

    @Override
    public IGaugeFigure read(File root4PProject, String visualizationSession, int channel,
            ICapturedImageFileSet capturedImageFileSet) throws IOException {
        File pathToFigure = this._getPathToFigure(root4PProject, visualizationSession, channel, capturedImageFileSet);

        Image<RGB16> gaugeFigure = this._readGaugeFigure(pathToFigure);

        return GaugeFigureFactory.create(this._gaugeFigureType, this._angleGaugeType, gaugeFigure, capturedImageFileSet,
                channel);
    }

    @Override
    public void close() throws IOException {
        this._reader.close();

    }

    private File _getPathToFigure(File root4PProject, String visualizationSession, int channel,
            ICapturedImageFileSet capturedImageFileSet) {
        return TiffGaugeFigureIOUtil.createGaugeFigurePath(root4PProject, visualizationSession, channel,
                capturedImageFileSet, this._gaugeFigureType, this._angleGaugeType);
    }

    private Image<RGB16> _readGaugeFigure(File pathToFigure) throws IOException {
        Image<RGB16> diskImage = null;
        try {
            this._reader.read(pathToFigure);
        } catch (IOException | MetadataParseError e) {
            throw new IOException("Polarization images don't exist or are corrupted");
        }
        return diskImage;
    }
}