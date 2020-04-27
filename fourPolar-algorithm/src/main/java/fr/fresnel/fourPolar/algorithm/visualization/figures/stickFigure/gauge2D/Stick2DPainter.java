package fr.fresnel.fourPolar.algorithm.visualization.figures.stickFigure.gauge2D;

import java.util.Objects;

import fr.fresnel.fourPolar.algorithm.util.image.converters.GrayScaleToColorConverter;
import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.Pixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImageRandomAccess;
import fr.fresnel.fourPolar.core.physics.dipole.IOrientationVector;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationVector;
import fr.fresnel.fourPolar.core.util.image.colorMap.ColorMap;
import fr.fresnel.fourPolar.core.util.shape.IShape;
import fr.fresnel.fourPolar.core.util.shape.IShapeIterator;
import fr.fresnel.fourPolar.core.util.shape.ShapeFactory;
import fr.fresnel.fourPolar.core.util.shape.ShapeUtils;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.AngleGaugeType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.IAngleGaugePainter;

class Stick2DPainter implements IAngleGaugePainter {
    final private long[] _soiImageDim;
    final private IGaugeFigure _stick2DFigure;
    final private IPixelRandomAccess<RGB16> _stick2DFigureRA;

    final private IOrientationImageRandomAccess _orientationRA;
    final private IPixelRandomAccess<UINT16> _soiRA;
    final private ColorMap _colormap;

    /**
     * We generate a single stick, and then rotate and translate it for different
     * dipoles.
     */
    final private IShape _stick;

    private final OrientationAngle _slopeAngle;
    private final OrientationAngle _colorAngle;
    private final double _maxColorAngle;

    private final IShape _stickFigureRegion;

    public Stick2DPainter(Stick2DPainterBuilder builder) throws ConverterToImgLib2NotFound {
        this._soiImageDim = builder.getSoIImage().getImage().getDimensions();
        this._soiRA = builder.getSoIImage().getImage().getRandomAccess();

        this._stick2DFigure = builder.getGaugeFigure();
        this._stick2DFigureRA = _stick2DFigure.getImage().getRandomAccess();

        this._orientationRA = builder.getOrientationImage().getRandomAccess();

        this._colormap = builder.getColorMap();

        this._slopeAngle = this._getSlopeAngle(this._stick2DFigure.getType());
        this._colorAngle = this._getColorAngle(this._stick2DFigure.getType());
        this._maxColorAngle = OrientationVector.maxAngle(_colorAngle);

        this._stick = this._defineBaseStick(
            builder.getSticklength(), builder.getStickThickness(),
            this._stick2DFigure.getImage().getDimensions());
        
        this._stickFigureRegion = this._defineGaugeImageBoundaryAsBoxShape(
            this._stick2DFigure.getImage().getDimensions());

        this._fillGaugeFigureWithSoI(
            builder.getSoIImage().getImage(), this._stick2DFigure.getImage());

    }

    /**
     * Place the soi of each dipole in the corresponding position in the gauge
     * figure.
     * 
     * @throws ConverterToImgLib2NotFound
     */
    private void _fillGaugeFigureWithSoI(Image<UINT16> soiImage, Image<RGB16> gaugeFigure)
            throws ConverterToImgLib2NotFound {
        GrayScaleToColorConverter.convertPlane(soiImage, gaugeFigure);
    }

    /**
     * Define the image region from pixel zero to dim - 1;
     */
    private IShape _defineGaugeImageBoundaryAsBoxShape(long[] imDimension) {
        long[] imageMax = imDimension.clone();
        for (int i = 0; i < imageMax.length; i++) {
            imageMax[i] -= 1;
        }

        return new ShapeFactory().closedBox(new long[imDimension.length], imageMax);
    }

    private IShape _defineBaseStick(int len, int thickness, long[] imDimension) {
        long[] stickMin = new long[imDimension.length];
        long[] stickMax = new long[imDimension.length];

        stickMin[0] = -thickness / 2;
        stickMin[1] = -len / 2;
        stickMax[0] = thickness / 2;
        stickMax[1] = len / 2;

        return new ShapeFactory().closedBox(stickMin, stickMax);
    }

    @Override
    public void draw(IShape region, UINT16 soiThreshold) {
        if (region.spaceDim() > this._soiImageDim.length) {
            throw new IllegalArgumentException("The region to draw sticks over in the orientation image "
                    + "cannot have more dimensions than the orientation image.");
        }

        int threshold = soiThreshold.get();
        Pixel<RGB16> pixel = new Pixel<>(RGB16.zero());

        // If region has less dimension than the soi Image, scale it to span higher
        // dimensions too.
        IShapeIterator pixelScalarItr = ShapeUtils.scaleShapeOverHigherDim(region,
                this._stick2DFigure.getImage().getDimensions());
        while (pixelScalarItr.hasNext()) {
            long[] stickCenterPosition = pixelScalarItr.next();

            if (_stickFigureRegion.isInside(stickCenterPosition)) {
                this._soiRA.setPosition(stickCenterPosition);
                final IOrientationVector orientationVector = this._getOrientationVector(stickCenterPosition);
                if (_isSoIAboveThreshold(threshold) && _slopeAndColorAngleExist(orientationVector)) {
                    _drawStick(pixel, orientationVector, stickCenterPosition);
                }
            }

        }

    }

    private void _drawStick(Pixel<RGB16> pixel, IOrientationVector orientationVector, long[] stickCenterPosition) {
        _transformStick(stickCenterPosition, orientationVector);
        this._stick.and(this._stickFigureRegion);
        final RGB16 color = _getStickColor(orientationVector);

        IShapeIterator stickIterator = this._stick.getIterator();
        while (stickIterator.hasNext()) {
            long[] stickPosition = stickIterator.next();
            this._stick2DFigureRA.setPosition(stickPosition);
            pixel.value().set(color.getR(), color.getG(), color.getB());
            this._stick2DFigureRA.setPixel(pixel);

        }
    }

    private RGB16 _getStickColor(IOrientationVector orientationVector) {
        final RGB16 color = this._colormap.getColor(0, this._maxColorAngle, orientationVector.getAngle(_colorAngle));
        return color;
    }

    private void _transformStick(long[] position, IOrientationVector orientationVector) {
        this._stick.resetToOriginalShape();
        this._stick.rotate2D(Math.PI / 2 + orientationVector.getAngle(_slopeAngle));
        this._stick.translate(position);
    }

    private IOrientationVector _getOrientationVector(long[] stickCenterPosition) {
        this._orientationRA.setPosition(stickCenterPosition);
        return this._orientationRA.getOrientation();
    }

    private boolean _isSoIAboveThreshold(int threshold) {
        return this._soiRA.getPixel().value().get() >= threshold;
    }

    private boolean _slopeAndColorAngleExist(final IOrientationVector orientationVector) {
        return !Double.isNaN(orientationVector.getAngle(_slopeAngle))
                && !Double.isNaN(orientationVector.getAngle(_colorAngle));
    }

    @Override
    public IGaugeFigure getStickFigure() {
        return _stick2DFigure;
    }

    private OrientationAngle _getSlopeAngle(AngleGaugeType type) {
        OrientationAngle angle = null;
        switch (type) {
            case Rho2D:
                angle = OrientationAngle.rho;
                break;

            case Delta2D:
                angle = OrientationAngle.rho;
                break;

            case Eta2D:
                angle = OrientationAngle.rho;
                break;

            default:
                break;
        }

        return angle;
    }

    private OrientationAngle _getColorAngle(AngleGaugeType type) {
        OrientationAngle angle = null;
        switch (type) {
            case Rho2D:
                angle = OrientationAngle.rho;
                break;

            case Delta2D:
                angle = OrientationAngle.delta;
                break;

            case Eta2D:
                angle = OrientationAngle.eta;
                break;

            default:
                break;
        }

        return angle;
    }

}