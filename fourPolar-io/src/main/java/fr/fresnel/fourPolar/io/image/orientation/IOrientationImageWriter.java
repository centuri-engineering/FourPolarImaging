package fr.fresnel.fourPolar.io.image.orientation;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.image.captured.fileSet.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;

/**
 * And interface for writing an orientation image to the disk.
 */
public interface IOrientationImageWriter {
    /**
     * Write the orientation image to the {@link IOrientationImageFileSet} paths.
     * 
     * @param rootFolder is the root folder of the captured images.
     * @param fileSet is the captured file set corresponding to this orientation image.
     * @param image is the orientation image.
     * @throws IOException thrown in case of low-level problems.
     */
    public void write(File rootFolder, ICapturedImageFileSet fileSet, IOrientationImage image) throws IOException;
}