package fr.fresnel.fourPolar.io.visualization.figures.gaugeFigure;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.io.exceptions.visualization.gaugeFigure.GaugeFigureIOException;

/**
 * An interface for reading a {@link IGaugeFigure} from the disk.
 */
public interface IGaugeFigureReader {
    /**
     * Write the given composites to the folder designated to the processed (bead)
     * images, as defined by
     * {@link PathFactoryOfProject#getFolder_ProcessedBeadImages}.
     * 
     * @param root4PProject        is the location of the 4Polar folder of the
     *                             project {@see PathFactoryOfProject}.
     * @param visualizationSession is the name of the visualization session to which
     *                             this gauge figure belong.
     * @param channel              is the channel number of this image.
     * @param capturedImageFileSet is the captured image set to which this gauge
     *                             figure belong.
     * @throws GaugeFigureIOException thrown in case of IO issues.
     * 
     * @return the composites for the given channel number.
     */
    public IGaugeFigure read(File root4PProject, String visualizationSession, int channel,
            ICapturedImageFileSet capturedImageFileSet) throws GaugeFigureIOException;

    /**
     * Close all resources associated with the reader.
     * 
     * @throws IOException
     */
    public void close() throws IOException;
}