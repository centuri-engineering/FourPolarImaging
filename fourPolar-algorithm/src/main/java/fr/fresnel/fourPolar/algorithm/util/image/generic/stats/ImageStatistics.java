package fr.fresnel.fourPolar.algorithm.util.image.generic.stats;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RealType;
import fr.fresnel.fourPolar.core.util.image.generic.metadata.MetadataUtil;

public class ImageStatistics {

    /**
     * Returns the minimum and maximum value of each plane of the image.
     * 
     * @return an array, where first row contains the minimums, and second the
     *         maximums of each plane of the image.
     */
    public static <T extends RealType> double[][] getPlaneMinMax(Image<T> image) {
        int nPlanes = MetadataUtil.getNPlanes(image.getMetadata());
        double[][] minMax = new double[2][nPlanes];

        long[] planeDim = MetadataUtil.getPlaneDim(image.getMetadata());
        long planeSize = planeDim[0] * planeDim[1];

        IPixelCursor<T> cursor = image.getCursor();
        for (int plane = 0; plane < nPlanes; plane++) {
            int planeCounter = 1;

            double planeMin = Double.POSITIVE_INFINITY;
            double planeMax = 0;
            while (cursor.hasNext() && planeCounter++ <= planeSize) {
                double pixel = cursor.next().value().getRealValue();

                planeMin = pixel < planeMin ? pixel : planeMin;
                planeMax = pixel > planeMax ? pixel : planeMax;
            }
            planeCounter = 1;
            minMax[0][plane] = planeMin;
            minMax[1][plane] = planeMax;

        }

        return minMax;

    }

        /**
     * Calculates the demanded percentile of the first plane of the image.
     * 
     * @param image    is the image instance.
     * @param quantile is the desired percentile.
     * @return the percentile as documented in {@link Percentile}.
     */
    public static <T extends RealType> double computePercentileFirstPlane(Image<T> image, int quantile) {
        return ImagePercentileCalculator.computePercentileFirstPlane(image, quantile);
    }

}