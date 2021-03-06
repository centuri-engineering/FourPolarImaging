package fr.fresnel.fourPolar.core.image.captured;

import java.util.Objects;

import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFile;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;

/**
 * This class holds a captured image as an unsigned short. Note that the
 * captured image may be single or full channel.
 */
class CapturedImage implements ICapturedImage {
    private final ICapturedImageFile _file;
    private final Image<UINT16> _img;
    private final String _label;

    /**
     * This class holds a captured image as an unsigned short.
     * 
     * @param file  is the file this image corresponds to.
     * @param label returns the label of this image in the captured file set. This
     *              the same label as those generated by {@link Cameras}.
     * @param img   is the captured image.
     * 
     * @throws IllegalArgumentException
     */
    public CapturedImage(ICapturedImageFile file, String label, Image<UINT16> img) {
        this._file = Objects.requireNonNull(file);
        this._img = Objects.requireNonNull(img);
        this._label = label;
    }

    /**
     * Returns the corresponding file set.
     */
    @Override
    public ICapturedImageFile getCapturedImageFile() {
        return _file;
    }

    /**
     * Returns the image.
     */
    @Override
    public Image<UINT16> getImage() {
        return _img;
    }

    @Override
    public String getLabel() {
        return _label;
    }

    @Override
    public int[] channels() {
        return _file.channels();
    }
}