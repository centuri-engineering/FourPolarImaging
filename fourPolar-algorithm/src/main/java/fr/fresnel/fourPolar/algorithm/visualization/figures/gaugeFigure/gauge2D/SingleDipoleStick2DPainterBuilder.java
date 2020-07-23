package fr.fresnel.fourPolar.algorithm.visualization.figures.gaugeFigure.gauge2D;

import java.util.Objects;

import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;
import fr.fresnel.fourPolar.core.image.soi.ISoIImage;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMap;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMapFactory;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureLocalization;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.AngleGaugeType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.IAngleGaugePainter;

/**
 * Using this class, we can generate a {@link IGaugeFigure} that is
 * representative of the 2D orientation of a single dipole. In other words, the
 * size of the {@link IGaugeFigure} is such that only a single dipole can be
 * represented inside, and the {@link GaugeFigureLocalization} is SingleDipole.
 * <p>
 * Note that to use {@link IGuagePainter} interface generated by this class, the
 * region that should be fed to it must be a single Point, otherwise an
 * exception is returned. Note that the same interface can be used to repaint
 * the stick for different dipole positions.
 * <p>
 * Note that the axis order of the generated gauge figure is as defined in
 * {@link IGaugeFigure#AXIS_ORDER}.
 */
public class SingleDipoleStick2DPainterBuilder extends ISingleDipoleStick2DPainterBuilder {
    private GaugeFigure _gaugeFigure;
    private IOrientationImage _orientationImage;
    private ISoIImage _soiImage;

    private ColorMap _colorMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_SPECTRUM);
    private int _thickness = 4;
    private int _length = 50;

    /**
     * This ratio determines the size of the underlying figure with respect to stick
     * length. Default value is 8. Hence figure dimension is (8 * _length) * (8 *
     * _length)
     */
    private int _figSizeToStickLenRatio = 8;

    /**
     * The orientation angle that would be represented as slope of sticks,
     */
    private OrientationAngle _slopeAngle = null;

    /**
     * The orientation angle that would be used as the color of sticks.
     */
    private OrientationAngle _colorAngle = null;

    /**
     * Initialize the painter with the given orientation and soi image, for the
     * given angle gauge type.
     * 
     * @param orientationImage         is the orientation image
     * @param soiImage                 is the corresponding soi Image of @param
     *                                 orientationImage.
     * @param gaugeType                is the angle gauge type to be painted.
     * 
     * @param IllegalArgumentException is thrown in case soi and orientation image
     *                                 are not from the same set, or that soi or
     *                                 orientation image have channels.
     * 
     */
    public SingleDipoleStick2DPainterBuilder(IOrientationImage orientationImage, ISoIImage soiImage,
            AngleGaugeType gaugeType) {
        Objects.requireNonNull(soiImage, "soiImage cannot be null");
        Objects.requireNonNull(orientationImage, "orientationImage cannot be null");
        Objects.requireNonNull(gaugeType, "gaugeType cannot be null");

        if (!orientationImage.getCapturedSet().getSetName().equals(soiImage.getFileSet().getSetName())
                || orientationImage.channel() != soiImage.channel()) {
            throw new IllegalArgumentException("orientation and soi images don't belong to the same set or channel.");
        }

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
     * Set the ratio of the underlying figure with respect to stick length. Default
     * value is 8. Hence figure dimension is 8 times stick length in each dimension.
     */
    public SingleDipoleStick2DPainterBuilder figSizeToStickLenRatio(int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Ratio has to be greater than one");
        }

        this._figSizeToStickLenRatio = ratio;
        return this;
    }

    private void _setColorAngle(OrientationAngle colorAngle) {
        _colorAngle = colorAngle;
    }

    private void _setSlopeAngle(OrientationAngle slopeAngle) {
        _slopeAngle = slopeAngle;
    }

    private void _setSoIImage(ISoIImage soiImage) {
        this._soiImage = soiImage;
    }

    private void _setOrientationImage(IOrientationImage orientationImage) {
        this._orientationImage = orientationImage;
    }

    private void _setGaugeFigureAsDelta2D() {
        _gaugeFigure = GaugeFigure.singleDipoleDelta2DStick(this._length * this._figSizeToStickLenRatio,
                _soiImage.channel(), _soiImage.getFileSet(), _soiImage.getImage().getFactory());
    }

    private void _checkSoIAndOrientationImageBelongToSameSet(IOrientationImage orientationImage, ISoIImage soiImage) {
        if (!soiImage.belongsTo(orientationImage)) {
            throw new IllegalArgumentException("orientation and soi images don't belong to the same set or channel.");
        }
    }

    /**
     * Build the Painter from the provided constraints for drawing delta sticks.
     * 
     * @param orientationImage is the orientation image
     * @param soiImage         is the corresponding soi Image
     * @return a painter for drawing the rho 2D sticks.
     * 
     * @throws IllegalArgumentException is thrown in case soi and orientation image
     *                                  don't belong together.
     * 
     */
    public IAngleGaugePainter buildDeltaStickPainter(IOrientationImage orientationImage, ISoIImage soiImage) {
        Objects.requireNonNull(soiImage, "soiImage cannot be null");
        Objects.requireNonNull(orientationImage, "orientationImage cannot be null");
        _checkSoIAndOrientationImageBelongToSameSet(orientationImage, soiImage);

        this._setSlopeAngle(OrientationAngle.rho);
        this._setColorAngle(OrientationAngle.delta);
        this._setOrientationImage(orientationImage);
        this._setSoIImage(soiImage);
        this._setGaugeFigureAsDelta2D();
        return new SingleDipoleInPlaneStickPainter(this);

    }

    @Override
    ColorMap getColorMap() {
        return this._colorMap;
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

    @Override
    GaugeFigure getGaugeFigure() {
        return this._gaugeFigure;
    }

    @Override
    OrientationAngle getSlopeAngle() {
        return this._slopeAngle;
    }

    @Override
    OrientationAngle getColorAngle() {
        return this._colorAngle;
    }

}