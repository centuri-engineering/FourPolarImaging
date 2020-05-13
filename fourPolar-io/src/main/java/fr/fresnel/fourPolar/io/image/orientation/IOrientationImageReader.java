package fr.fresnel.fourPolar.io.image.orientation;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.exceptions.image.orientation.CannotFormOrientationImage;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;

/**
 * And interface for reading an orientation image from the disk.
 */
public interface IOrientationImageReader {
    /**
     * Read the orientation and return the corresponding {@link IOrientationImage}.
     * 
     * @param root4PProject is the location of the 4Polar folder of the project
     *                      {@see PathFactoryOfProject}.
     * @param fileSet       is the captured file set corresponding to this
     *                      orientation image.
     * @param channel       is the channel number.
     * @return
     */
    public IOrientationImage read(File root4PProject, ICapturedImageFileSet fileSet, int channel)
            throws IOException, CannotFormOrientationImage;

    /**
     * Close all resources associated with this reader.
     * 
     * @throws IOException
     */
    public void close() throws IOException;
}