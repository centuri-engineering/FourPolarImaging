package fr.fresnel.fourPolar.io.image.orientation.file;

import java.io.File;
import java.nio.file.Paths;

import fr.fresnel.fourPolar.core.PathFactoryOfProject;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;

/**
 * A concrete implementation of the {@link IOrientationImageFileSet}. Note that
 * with this implementation, all images will have a tiff extension.
 */
public class TiffOrientationImageFileSet implements IOrientationImageFileSet {
    private final static String _extension = "tif";
    private final String _setName;
    private final File _rhoImage;
    private final File _deltaImage;
    private final File _etaImage;

    /**
     * A concrete implementation of the {@link IOrientationImageFileSet}. The path
     * for angle images is
     * 
     * @param fileSet
     */
    public TiffOrientationImageFileSet(File rootFolder, ICapturedImageFileSet fileSet) {
        this._setName = fileSet.getSetName();

        File parentFolder = this._getSetParentFolder(rootFolder, fileSet.getChannel());

        if (!parentFolder.exists()){
            parentFolder.mkdirs();
        }

        this._rhoImage = new File(parentFolder, "Rho" + "." + _extension);
        this._deltaImage = new File(parentFolder, "Delta" + "." + _extension);
        this._etaImage = new File(parentFolder, "Eta" + "." + _extension);
    }

    @Override
    public File getFile(OrientationAngle angle) {
        File imageFile = null;

        switch (angle) {
            case rho:
                imageFile = _rhoImage;
                break;

            case delta:
                imageFile = _deltaImage;
                break;

            case eta:
                imageFile = _etaImage;
                break;

            default:
                break;
        }
        return imageFile;
    }

    @Override
    public String getSetName() {
        return this._setName;
    }

    private File _getSetParentFolder(File rootFolder, int channel) {
        return Paths.get(PathFactoryOfProject.getFolder_OrientationImages(rootFolder).getAbsolutePath(),
                "Channel" + channel, this._setName).toFile();
    }

}