package fr.fresnel.fourPolar.algorithm.visualization.figures.gaugeFigure.gauge3D;

import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;
import fr.fresnel.fourPolar.core.image.soi.ISoIImage;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMap;

/**
 * A simple private interface for accessing the parameters of the builder.
 */
abstract public class IWholeSampleStick3DPainterBuilder {
    abstract ColorMap getColorMap();

    abstract int getSticklength();

    abstract IOrientationImage getOrientationImage();

    abstract ISoIImage getSoIImage();

    abstract int getStickThickness();
}