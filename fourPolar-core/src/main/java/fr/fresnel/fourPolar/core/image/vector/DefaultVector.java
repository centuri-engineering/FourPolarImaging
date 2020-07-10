package fr.fresnel.fourPolar.core.image.vector;

import java.util.Objects;
import java.util.Optional;

import fr.fresnel.fourPolar.core.image.generic.pixel.types.ARGB8;
import fr.fresnel.fourPolar.core.image.vector.animation.Animation;
import fr.fresnel.fourPolar.core.image.vector.filter.FilterComposite;
import fr.fresnel.fourPolar.core.util.shape.IBoxShape;
import fr.fresnel.fourPolar.core.util.shape.ILineShape;
import fr.fresnel.fourPolar.core.util.shape.IPointShape;
import fr.fresnel.fourPolar.core.util.shape.IShape;

/**
 * A default implementation for the {@link Vector}.
 */
class DefaultVector implements Vector {
    private IShape _shape;
    private ARGB8 _color;
    private ARGB8 _fillColor;
    private int _strokeWidth;
    private Animation _animation;

    private FilterComposite _filterComposite;

    /**
     * Private constructor for setting colors and stroke width. Sets fill color
     * equal to color, and stroke with to 1.
     */
    private DefaultVector(ARGB8 color) {
        Objects.requireNonNull(color, "color can't be null");

        _color = color;
        _fillColor = color;
        _strokeWidth = 1;

        _filterComposite = null;
        _animation = null;
    }

    /**
     * Constructs the vector with a line shape, setting its color equal to fill
     * color, and stroke width equal to 1.
     * 
     * @param lineShape is the point shape of the vector.
     * @param color     is the color of the vector
     */
    DefaultVector(ILineShape lineShape, ARGB8 color) {
        this(color);

        Objects.requireNonNull(lineShape, "lineShape can't be null");
        _shape = lineShape;
    }

    /**
     * Constructs the vector with a box shape, setting its color equal to fill
     * color, and stroke width equal to 1.
     * 
     * @param boxShape is the point shape of the vector.
     * @param color    is the color of the vector
     */
    DefaultVector(IBoxShape boxShape, ARGB8 color) {
        this(color);
        _shape = boxShape;
    }

    @Override
    public IShape shape() {
        return this._shape;
    }

    @Override
    public ARGB8 color() {
        return this._color;
    }

    @Override
    public ARGB8 fill() {
        return this._fillColor;
    }

    @Override
    public int strokeWidth() {
        return this._strokeWidth;
    }

    @Override
    public Optional<Animation> animation() {
        return Optional.ofNullable(_animation);
    }

    @Override
    public void setShape(IBoxShape box) {
        Objects.requireNonNull(box, "box shape can't be null");

        this._shape = box;
    }

    @Override
    public void setShape(ILineShape line) {
        Objects.requireNonNull(line, "line shape can't be null");

        this._shape = line;
    }

    @Override
    public void setColor(ARGB8 color) {
        Objects.requireNonNull(color, "Color can't be null");

        this._color = color;
    }

    @Override
    public void setFill(ARGB8 color) {
        this._fillColor = color;
    }

    @Override
    public void setStrokeWidth(int width) {
        this._strokeWidth = width;
    }

    @Override
    public Optional<FilterComposite> filter() {
        return Optional.ofNullable(_filterComposite);
    }

    @Override
    public void setFilter(FilterComposite composite) {
        _filterComposite = composite;
    }

    @Override
    public void setAnimation(Animation animation) {
        this._animation = animation;
    }

}