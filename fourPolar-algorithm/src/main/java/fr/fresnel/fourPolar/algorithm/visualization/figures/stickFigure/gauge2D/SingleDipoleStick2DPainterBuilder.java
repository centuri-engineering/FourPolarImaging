package fr.fresnel.fourPolar.algorithm.visualization.figures.stickFigure.gauge2D;

import java.util.Objects;

import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.image.generic.metadata.Metadata;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;
import fr.fresnel.fourPolar.core.image.soi.ISoIImage;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMap;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMapFactory;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureFactory;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.AngleGaugeType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.IAngleGaugePainter;

/**
 * Using this class, we can generate a {@link IGaugeFigure} that is
 * representative of the 2D orientation of a single dipole. In other words, the
 * size of the {@link IGaugeFigure} is such that only a single dipole can be
 * represented inside, and the {@link GaugeFigureType} is SingleDipole.
 * <p>
 * Note that to use {@link IGuagePainter} interface generated by this class, the
 * region that should be fed to it must be a single Point, otherwise an
 * exception is returned. Note that the same interface can be used to repaint
 * the stick for different dipole positions.
 * <p>
 * Note that the generated gauge figure is an XYZT image.
 */
public class SingleDipoleStick2DPainterBuilder extends ISingleDipoleStick2DPainterBuilder {
    private final IOrientationImage _orientationImage;
    private final ISoIImage _soiImage;
    private final AngleGaugeType _gaugeType;

    private ColorMap _colorMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_SPECTRUM);
    private int _thickness = 4;
    private int _length = 50;
    private GaugeFigureType _gaugeFigureType = GaugeFigureType.WholeSample;

    private IGaugeFigure _gaugeFigure;

    public SingleDipoleStick2DPainterBuilder(IOrientationImage orientationImage, ISoIImage soiImage,
            AngleGaugeType gaugeType) {
        Objects.requireNonNull(soiImage, "soiImage cannot be null");
        Objects.requireNonNull(orientationImage, "orientationImage cannot be null");
        Objects.requireNonNull(gaugeType, "gaugeType cannot be null");

        if (!orientationImage.getCapturedSet().getSetName().equals(soiImage.getFileSet().getSetName())
                || orientationImage.channel() != soiImage.channel()) {
            throw new IllegalArgumentException("orientation and soi images don't belong to the same set or channel.");
        }

        this._gaugeType = gaugeType;
        this._soiImage = soiImage;
        this._orientationImage = orientationImage;
    }

    /**
     * Define the colormap used for drawing the sticks. Note that two criteria
     * should be satisfied when choosing colormap:
     * 
     * 1- It must not have black or white colors, otherwise, it will be
     * misinterpreted as intensity (because the background is an SoI image).
     * 
     * 2- For Rho2D sticks, the colormap must wrap to the same color at both ends of
     * the spectrum, so that 0 and 180 degree have the same color.
     */
    public SingleDipoleStick2DPainterBuilder colorMap(ColorMap colorMap) {
        Objects.requireNonNull(colorMap, "colorMap cannot be null;");
        return this;
    }

    /**
     * Define the thickness of each stick.
     */
    public SingleDipoleStick2DPainterBuilder stickThickness(int thickness) {
        if (thickness < 1) {
            throw new IllegalArgumentException("thickness must be at least one");
        }

        this._thickness = thickness;

        return this;
    }

    /**
     * Define the length of each stick.
     */
    public SingleDipoleStick2DPainterBuilder stickLen(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be at least one");
        }

        this._length = length;

        return this;
    }

    /**
     * Build the Painter from the provided constraints.
     * 
     * @return the interface for the painter of sticks.
     * @throws ConverterToImgLib2NotFound in case the Image interface of SoIImage
     *                                    cannot be converted to ImgLib2 image type.
     */
    public IAngleGaugePainter build() throws ConverterToImgLib2NotFound {
        IMetadata orientImMetadata = this._orientationImage.getAngleImage(OrientationAngle.rho).getImage()
                .getMetadata();
        this._gaugeFigure = this._createGaugeFigure(orientImMetadata);
        return new SingleDipoleInPlaneStickPainter(this);

    }

    /**
     * Create a XYCZT gauge figure, where (X,Y) = (lenStick, lenStick) and (C,Z,T) =
     * (1,1,1). This is to make the gauge figure consistent with all the other gauge
     * figures.
     * 
     */
    private IGaugeFigure _createGaugeFigure(IMetadata orientationImMetadata) {
        long[] dim = new long[IGaugeFigure.AXIS_ORDER.numAxis];
        dim[0] = this._length;
        dim[1] = this._length;

        IMetadata gaugeFigMetadata = new Metadata.MetadataBuilder(dim).axisOrder(AxisOrder.XYCZT).build();
        Image<RGB16> gaugeImage = this._soiImage.getImage().getFactory().create(gaugeFigMetadata, RGB16.zero());
        return GaugeFigureFactory.create(this._gaugeFigureType, this._gaugeType, gaugeImage,
                this._soiImage.getFileSet());
    }

    @Override
    ColorMap getColorMap() {
        return this._colorMap;
    }

    @Override
    IGaugeFigure getGaugeFigure() {
        return _gaugeFigure;
    }

    @Override
    int getSticklength() {
        return _length;
    }

    @Override
    IOrientationImage getOrientationImage() {
        return _orientationImage;
    }

    @Override
    ISoIImage getSoIImage() {
        return _soiImage;
    }

    @Override
    int getStickThickness() {
        return _thickness;
    }

}