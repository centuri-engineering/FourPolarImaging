package fr.fresnel.fourPolar.algorithm.fourPolar;

import fr.fresnel.fourPolar.algorithm.exceptions.fourPolar.IteratorMissMatch;
import fr.fresnel.fourPolar.algorithm.exceptions.fourPolar.converters.ImpossibleOrientationVector;
import fr.fresnel.fourPolar.algorithm.fourPolar.converters.IIntensityToOrientationConverter;
import fr.fresnel.fourPolar.core.fourPolar.IIntensityVectorIterator;
import fr.fresnel.fourPolar.core.fourPolar.IOrientationVectorIterator;
import fr.fresnel.fourPolar.core.physics.dipole.IOrientationVector;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationVector;
import fr.fresnel.fourPolar.core.physics.polarization.IntensityVector;

/**
 * An implementation of the 4Polar algorithm, which can be used to convert an
 * intensity iterator to an orientation iterator.
 * 
 * Note that essentialy this implementation can be used to convert all
 * intensities associated with one channel.
 * 
 */
public class FourPolarMapper {
    final private IIntensityToOrientationConverter _converter;

    /**
     * @param converter is the intensity converter for one class.
     */
    public FourPolarMapper(IIntensityToOrientationConverter converter) {
        this._converter = converter;
    }

    /**
     * Iterates over the given set of intensities and puts the calculated
     * orientation vector into the orientation set via its iterator.
     * 
     * @param intensityIterator   is the intensity set iterator.
     * @param orientationIterator is the orientation vector set iterator.
     * @throws ImpossibleOrientationVector
     */
    public void map(IIntensityVectorIterator intensityIterator, IOrientationVectorIterator orientationIterator)
            throws IteratorMissMatch {
        if (orientationIterator.size() != intensityIterator.size()) {
            throw new IteratorMissMatch("Orientation and intensity iterators don't have same size. Hence,"
                    + " orientation image does not correspond to polarization image.");
        }

        IOrientationVector orientationVector = new OrientationVector(0, 0, 0);
        while (intensityIterator.hasNext()) {
            IntensityVector intensity = intensityIterator.next();

            try {
                _converter.convert(intensity, orientationVector);
            } catch (ImpossibleOrientationVector e) {
                orientationVector.setAngles(Double.NaN, Double.NaN, Double.NaN);
            }

            orientationIterator.next();
            orientationIterator.set(orientationVector);
        }
    }

    /**
     * Iterates over the given set of intensities and if sum of intensity is greater
     * than the threshold, puts the calculated orientation vector into the
     * orientation set via its iterator. Puts NaN for all orientation angles if the
     * position does not exceed threshold.
     * 
     * @param intensityIterator   is the intensity set iterator.
     * @param orientationIterator is the orientation vector set iterator.
     * @throws ImpossibleOrientationVector
     */
    public void map(IIntensityVectorIterator intensityIterator, IOrientationVectorIterator orientationIterator,
            double soiThreshold) throws IteratorMissMatch {
        if (orientationIterator.size() != intensityIterator.size()) {
            throw new IteratorMissMatch("Orientation and intensity iterators don't have same size. Hence,"
                    + " orientation image does not correspond to polarization image.");
        }

        IOrientationVector orientationVector = new OrientationVector(0, 0, 0);
        while (intensityIterator.hasNext()) {
            IntensityVector intensity = intensityIterator.next();

            if (intensity.getSumOfIntensity() >= soiThreshold) {
                try {
                    _converter.convert(intensity, orientationVector);
                } catch (ImpossibleOrientationVector e) {
                    orientationVector.setAngles(Double.NaN, Double.NaN, Double.NaN);
                }

            } else {
                orientationVector.setAngles(Double.NaN, Double.NaN, Double.NaN);
            }

            orientationIterator.next();
            orientationIterator.set(orientationVector);

        }
    }

}