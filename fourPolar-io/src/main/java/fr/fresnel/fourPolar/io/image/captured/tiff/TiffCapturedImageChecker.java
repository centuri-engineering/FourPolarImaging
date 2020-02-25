package fr.fresnel.fourPolar.io.image.captured.tiff;

import java.io.File;
import java.io.IOException;

import fr.fresnel.fourPolar.core.exceptions.imageSet.acquisition.IncompatibleCapturedImage;
import fr.fresnel.fourPolar.core.image.captured.ICapturedImageChecker;
import fr.fresnel.fourPolar.core.imageSet.acquisition.RejectedCapturedImage;
import io.scif.FormatException;
import io.scif.ImageMetadata;
import io.scif.Metadata;
import io.scif.Reader;
import io.scif.SCIFIO;
import io.scif.util.FormatTools;



/**
 * A class for checking the compatibility of a tiff image with the software
 * criteria.
 */
public class TiffCapturedImageChecker implements ICapturedImageChecker {
    public static String notExist = "The file does not exist or cannot be accessed.";
    public static String lowBitDepth = "Bit depth < 16. The image should not be used.";
    public static String notUnsignedShort = "Chanage format to unsigned 16 bit.";
    public static String badExtension = "The given file is not tiff.";
    public static String corruptContent =  "File IO issue or Corrupt tiff content.";
    public static String formatError = "Format rendition error in SCIFIO package.";

    /**
     * Makes sure that the provided file is tif, and has bit depth of at least 16
     * bits.
     * 
     * @throws IOException
     * @throws IncompatibleCapturedImage
     */
    @Override
    public void checkCompatible(File image) throws IncompatibleCapturedImage {
        try {
            if (!image.exists()){
                throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, notExist));
            }                 
        } catch (SecurityException e) {
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, notExist));
        }
        
        this._checkExtension(image);

        try {
            this._bitDepthAbove16(image);
        } catch (IOException e) {
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, corruptContent));
        } catch (FormatException e) {
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, formatError));
        }
    }

    private void _checkExtension(File image) throws IncompatibleCapturedImage {
        int index = image.getName().lastIndexOf('.');
        String extension = index > 0 ? image.getName().substring(index + 1) : null;

        if (extension == null || !extension.equals(this.getExtension())) {
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, badExtension));
        } 
    }

    /**
     * Using the metadata of the image, make sure that the given image has at least
     * 16 bit depth.
     * 
     * @return
     * @throws IOException
     * @throws FormatException
     */
    private void _bitDepthAbove16(File image) throws FormatException, IOException, IncompatibleCapturedImage {
        final SCIFIO scifio = new SCIFIO();
        final Reader reader = scifio.initializer().initializeReader(image.getAbsolutePath());
        final Metadata meta = reader.getMetadata();

        final ImageMetadata iMeta = meta.get(0);

        if (iMeta.getBitsPerPixel() < 16) {
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, lowBitDepth));
        }

        if (iMeta.getPixelType() != FormatTools.UINT16){
            throw new IncompatibleCapturedImage(new RejectedCapturedImage(image, notUnsignedShort));
        }
        
        reader.close();
        scifio.context().dispose();
    }

    @Override
    public String getExtension() {
        return "tif";
    }

}