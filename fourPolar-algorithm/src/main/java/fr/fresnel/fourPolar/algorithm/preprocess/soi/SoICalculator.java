package fr.fresnel.fourPolar.algorithm.preprocess.soi;

import fr.fresnel.fourPolar.core.fourPolar.IIntensityVectorIterator;
import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.pixel.Pixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.physics.polarization.IntensityVector;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;

/**
 * Using this class, we can calculate the sum of intensity of polarizations, for
 * a {@link IPolarizationsImageSet}, and using it's iterator.
 */
public class SoICalculator implements ISoICalculator {

    public static ISoICalculator create() {
        return new SoICalculator();
    }

    private SoICalculator() {
    }

    @Override
    public void calculateUINT16Sum(IIntensityVectorIterator intensityIterator, IPixelCursor<UINT16> pixelCursor) {
        while (intensityIterator.hasNext()) {
            IntensityVector intensity = intensityIterator.next();

            double soi = intensity.getIntensity(Polarization.pol0) + intensity.getIntensity(Polarization.pol90)
                    + intensity.getIntensity(Polarization.pol45) + intensity.getIntensity(Polarization.pol135);

            pixelCursor.next();
            pixelCursor.setPixel(new Pixel<UINT16>(new UINT16((int) soi)));
        }

    }

}