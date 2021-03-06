package fr.fresnel.fourPolar.core.exceptions.imageSet.acquisition;

import fr.fresnel.fourPolar.core.image.captured.file.RejectedCapturedImage;

/**
 * Exception thrown when the content of the given captured image is corrupt
 * (including metadata, etc). The class returns the rejected image as a
 * {@link RejectedCapturedImage}.
 * 
 */
public class IncompatibleCapturedImage extends Exception{
    private static final long serialVersionUID = 53687232131008L;

    private RejectedCapturedImage _rejectedImage;
    
    public IncompatibleCapturedImage(RejectedCapturedImage rejectedImage) {
        this._rejectedImage = rejectedImage;    
    }

    /**
     * Return the rejected image.
     * @return
     */
    public RejectedCapturedImage getRejectedImage() {
        return _rejectedImage;
    }

    @Override
    public String getMessage() {
        return _rejectedImage.getReason();
    }
}
