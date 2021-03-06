package fr.fresnel.fourPolar.core.util.shape;

import java.util.Arrays;
import java.util.Objects;

import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.shape.IShape;
import fr.fresnel.fourPolar.core.shape.IShapeIterator;
import fr.fresnel.fourPolar.core.shape.Rotation3DOrder;

/**
 * WARNING: When the time is right, this class should be rewritten using methods
 * of ImgLib2 library.
 */
class ScaledShape implements IShape {
    private final IShape _originalShape;
    private final AxisOrder _axisOrder;
    private final long[] _max;

    public ScaledShape(IShape originalShape, AxisOrder axisOrder, long[] max) {
        this._max = Objects.requireNonNull(max, "max cannot be null");
        if (Arrays.stream(max).min().getAsLong() <= 0) {
            throw new IllegalArgumentException("Scale dimension must be greater than one");
        }

        this._axisOrder = Objects.requireNonNull(axisOrder, "newAxisOrder cannot be null");
        if (!axisOrder.name().contains(originalShape.axisOrder().name())) {
            throw new IllegalArgumentException("newAxisOrder must contain shape axis");
        }

        this._originalShape = Objects.requireNonNull(originalShape, "Shape cannot be null");
    }

    @Override
    public IShapeIterator getIterator() {
        return new ScaledShapeIterator(_originalShape, _axisOrder, _max);
    }


    @Override
    public AxisOrder axisOrder() {
        return this._axisOrder;
    }

    @Override
    public int shapeDim() {
        return this._originalShape.shapeDim() + _max.length;
    }

    @Override
    public IShape rotate3D(double angle1, double angle2, double angle3, Rotation3DOrder rotation3dOrder) {
        throw new IllegalArgumentException("Can't rotate 3d shape.");

    }

    @Override
    public IShape rotate2D(double angle) {
        throw new IllegalArgumentException("Can't rotate scaled shape.");

    }

    @Override
    public IShape translate(long[] translation) {
        throw new IllegalArgumentException("Can't translate scaled shape.");

    }

    @Override
    public boolean isInside(long[] point) {
        throw new IllegalArgumentException("Can't check is inside for scaled shape.");
    }

    @Override
    public IShape and(IShape shape) {
        throw new IllegalArgumentException("Can't and scaled shape.");

    }

    @Override
    public int spaceDim() {
        return this._originalShape.spaceDim() + _max.length;
    }

}