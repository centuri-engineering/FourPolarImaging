package fr.fresnel.fourPolar.core.image.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.fresnel.fourPolar.core.image.vector.filter.Filter;

/**
 * A class for building a {@link FilterComposite}.
 */
public class FilterCompositeBuilder extends IFilterCompositeBuilder {
    private final String _id;
    private final List<Filter> _filters = new ArrayList<>();

    private int _xStart = -1;
    private int _yStart = -1;
    private int _widthPercent = -1;
    private int _heightPercent = -1;

    /**
     * Start building the composite by providing a id for the filter, together with
     * the first filter element.
     * 
     * @param id     is filter id.
     * @param filter is the first filter of the composite.
     */
    public FilterCompositeBuilder(String id, Filter filter) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(filter);

        _filters.add(filter);
        _id = id;
    }

    /**
     * @param x is the x start position of the filter. If negative, it will be set
     *          to empty.
     */
    public void xStart(int x) {
        _xStart = x;
    }

    /**
     * @param y is the y start position of the filter. If negative, it will be set
     *          to empty.
     */
    public void yStart(int y) {
        _yStart = y;
    }

    /**
     * @param percent is the width percentage of where the filter is applied. If
     *                negative, it will be set to empty.
     */
    public void widthPercent(int percent) {
        _widthPercent = percent;
    }

    /**
     * @param percent is the height percentage of where the filter is applied. If
     *                negative, it will be set to empty.
     */
    public void heightPercent(int percent) {
        _heightPercent = percent;
    }

    /**
     * Adds a new filter to this composite. It's the responsibility of the caller to
     * ensure that this filter is correctly constructed.
     * 
     * @param filter is the new filter.
     */
    public void addFilter(Filter filter) {
        Objects.requireNonNull(filter);

        _filters.add(filter);
    }

    public FilterComposite build() {
        return new FiltersComposite(this);
    }

    @Override
    int xStart() {
        return _xStart;
    }

    @Override
    int yStart() {
        return _yStart;
    }

    @Override
    int widthPercent() {
        return _widthPercent;
    }

    @Override
    int heightPercent() {
        return _heightPercent;
    }

    @Override
    String id() {
        return _id;
    }

    @Override
    List<Filter> filters() {
        return _filters;
    }

}