package fr.fresnel.fourPolar.core.imageSet.acquisition;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.exceptions.image.acquisition.CorruptCapturedImage;

/**
 * Used for checking that a captured image file has the desired format,
 */
public interface ICapturedImageChecker {
    /**
     * Returns the image extension type for which the interface is implemented.
     * The extension should not contain dot (see for example {@link} TiffImageChecker).
     * @return
     */
    public String getExtension();
    
    /**
     * Checks compatibility and returns a boolean.
     * @param imagePath
     * @return
     * @throws IOException
     * @throws CorruptCapturedImage
     */
    public boolean checkCompatible(File imagePath) throws IOException, CorruptCapturedImage;

}