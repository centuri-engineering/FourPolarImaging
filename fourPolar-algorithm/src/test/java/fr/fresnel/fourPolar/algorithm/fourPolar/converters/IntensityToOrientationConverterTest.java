package fr.fresnel.fourPolar.algorithm.fourPolar.converters;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.fresnel.fourPolar.algorithm.exceptions.fourPolar.converters.ImpossibleOrientationVector;
import fr.fresnel.fourPolar.algorithm.exceptions.fourPolar.propagation.OpticalPropagationNotInvertible;
import fr.fresnel.fourPolar.core.exceptions.physics.propagation.PropagationFactorNotFound;
import fr.fresnel.fourPolar.core.physics.channel.Channel;
import fr.fresnel.fourPolar.core.physics.dipole.DipoleSquaredComponent;
import fr.fresnel.fourPolar.core.physics.dipole.IOrientationVector;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationVector;
import fr.fresnel.fourPolar.core.physics.na.NumericalAperture;
import fr.fresnel.fourPolar.core.physics.polarization.IntensityVector;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;
import fr.fresnel.fourPolar.core.physics.propagation.InverseOpticalPropagation;
import fr.fresnel.fourPolar.core.physics.propagation.OpticalPropagation;

public class IntensityToOrientationConverterTest {
    private static IIntensityToOrientationConverter _converter;

    @BeforeAll
    public static void setInversePropagation() throws PropagationFactorNotFound, OpticalPropagationNotInvertible {
        // Channel and NA parameters are irrelevant to these tests.
        Channel channel = new Channel(520e-9, 1, 0.7, 1, 0.7);
        NumericalAperture na = new NumericalAperture(1.45, 1.015, 1.45, 1.015);

        OpticalPropagation optPropagation = new OpticalPropagation(channel, na);
        InverseOpticalPropagation inverseProp = new InverseOpticalPropagation(optPropagation);

        inverseProp.setInverseFactor(Polarization.pol0, DipoleSquaredComponent.XX, 0.091685566886620);
        inverseProp.setInverseFactor(Polarization.pol90, DipoleSquaredComponent.XX, -0.166595030225210);
        inverseProp.setInverseFactor(Polarization.pol45, DipoleSquaredComponent.XX, 0.419325727568000);
        inverseProp.setInverseFactor(Polarization.pol135, DipoleSquaredComponent.XX, 0.419325727568000);

        inverseProp.setInverseFactor(Polarization.pol0, DipoleSquaredComponent.YY, -0.166595030225194);
        inverseProp.setInverseFactor(Polarization.pol90, DipoleSquaredComponent.YY, 0.091685572902112);
        inverseProp.setInverseFactor(Polarization.pol45, DipoleSquaredComponent.YY, 0.419325693894715);
        inverseProp.setInverseFactor(Polarization.pol135, DipoleSquaredComponent.YY, 0.419325693894715);

        inverseProp.setInverseFactor(Polarization.pol0, DipoleSquaredComponent.ZZ, 0.201522875905556);
        inverseProp.setInverseFactor(Polarization.pol90, DipoleSquaredComponent.ZZ, 0.201522859722606);
        inverseProp.setInverseFactor(Polarization.pol45, DipoleSquaredComponent.ZZ, -0.612462773475724);
        inverseProp.setInverseFactor(Polarization.pol135, DipoleSquaredComponent.ZZ, -0.612462773475724);

        inverseProp.setInverseFactor(Polarization.pol0, DipoleSquaredComponent.XY, -0.000000000000000);
        inverseProp.setInverseFactor(Polarization.pol90, DipoleSquaredComponent.XY, 0.000000000000000);
        inverseProp.setInverseFactor(Polarization.pol45, DipoleSquaredComponent.XY, 0.309169707239095);
        inverseProp.setInverseFactor(Polarization.pol135, DipoleSquaredComponent.XY, -0.309169707239096);

        _converter = new IntensityToOrientationConverter(inverseProp);
    }

    @Test
    public void convert_BenchMark() throws ImpossibleOrientationVector {
        IntensityVector intensity = new IntensityVector(1, 1, 1, 1);

        IOrientationVector vector = null;
        for (int i = 0; i < 1000000; i++) {
            vector = _converter.convert(intensity);
        }

        System.out.println(vector.getAngle(OrientationAngle.eta));
    }

    @Test
    public void convert_UnfeasibleIntensityVectors_ThrowsImpossibleOrientationVector() {
        IntensityVector vec1 = new IntensityVector(1, 0, 0, 0);
        IntensityVector vec2 = new IntensityVector(0, 1, 0, 0);
        IntensityVector vec3 = new IntensityVector(0, 0, 1, 0);
        IntensityVector vec4 = new IntensityVector(0, 0, 0, 1);
        IntensityVector vec5 = new IntensityVector(1, 0, 0, 1);
        IntensityVector vec6 = new IntensityVector(0, 0, 0, 0);

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec1);
        });

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec2);
        });

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec3);
        });

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec4);
        });

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec5);
        });

        assertThrows(ImpossibleOrientationVector.class, () -> {
            _converter.convert(vec6);
        });

    }

    /**
     * In this test, we try to compare our results with the forward problem. To do
     * so, we have calculated the intensity from an orientation angle using the
     * integration formulas (forward problem). Then, we try to estimate the angles
     * with the back propagation matrix (inverse problem). These two methods will
     * not yield the same results (especially for marginal cases such as delta=180
     * or eta=0), but for more appropriate angles the error margin should not be
     * very high.
     * 
     * Also note that for cases where rho is close to zero, we may get a rho close
     * to 180. This is due to near equivalence of those angles.
     */
    @Test
    public void convert_CurcioForwardValues_OrientationErrorIsLessThanThreshold()
            throws IOException, ImpossibleOrientationVector {
        double error = Math.PI / 180 * 10;

        double etaGreaterThan = Math.PI / 180 * 10;
        double deltaLessThan = Math.PI / 180 * 160;

        InputStream stream = IntensityToOrientationConverterTest.class
                .getResourceAsStream("forwardMethodData-Curcio.txt");
        InputStreamReader iReader = new InputStreamReader(stream);
        BufferedReader buffer = new BufferedReader(iReader);

        String intensityOrientationPair = null;
        boolean equals = true;
        do {
            intensityOrientationPair = buffer.readLine();

            String[] values = intensityOrientationPair.split(",");
            if (isEtaGreaterThan(values, etaGreaterThan) && isDeltaLessThan(values, deltaLessThan)) {
                IntensityVector iVector = new IntensityVector(Double.parseDouble(values[0]),
                        Double.parseDouble(values[2]), Double.parseDouble(values[1]), Double.parseDouble(values[3]));

                OrientationVector original = new OrientationVector(Double.parseDouble(values[4]),
                        Double.parseDouble(values[6]), Double.parseDouble(values[5]));

                IOrientationVector calculated = _converter.convert(iVector);

                equals = _checkForwardAnglePrecision(original, calculated, error);

            }

        } while (intensityOrientationPair != null && equals);

        assertTrue(equals);
    }

    /**
     * In this test, we try to compare our results with the inverse problem
     * implemented by Curcio. It's expected that in all cases, both methods return
     * the same angles.
     * 
     * Note the data is in degree rather than radian, and that the intensities are
     * those in the forward data.
     */
    @Test
    public void convert_CurcioInverseValues_OrientationErrorIsLessThanThreshold()
            throws IOException, ImpossibleOrientationVector {
        double error = Math.PI / 180 * 20;

        BigDecimal etaGreaterThan = new BigDecimal(Math.PI / 180 * 10);
        BigDecimal rhoGreaterThan = new BigDecimal(Math.PI / 180 * 2);
        BigDecimal deltaLessThan = new BigDecimal(Math.PI / 180 * 160);        

        String intensityOrientationPair = null;
        boolean equals = true;

        BufferedReader forwardData = _readFile("forwardMethodData-Curcio.txt");
        BufferedReader inverseData = _readFile("inverseMethodData-Curcio.txt");
        int counter = 0;
        while ((intensityOrientationPair = inverseData.readLine()) != null && equals) {
            ++counter;
            String[] angles = intensityOrientationPair.split(",");

            double rho = Double.parseDouble(angles[0]) / 180 * Math.PI;
            double eta = Double.parseDouble(angles[1]) / 180 * Math.PI;
            double delta = Double.parseDouble(angles[2]) / 180 * Math.PI;

            if (!Double.isNaN(rho) && !Double.isNaN(delta) && !Double.isNaN(eta)
                    && isGreaterThan(eta, etaGreaterThan) && isGreaterThan(rho, rhoGreaterThan)
                    && isLessThan(delta, deltaLessThan)) {
                String[] intensities = forwardData.readLine().split(",");

                IntensityVector iVector = new IntensityVector(Double.parseDouble(intensities[0]),
                        Double.parseDouble(intensities[2]), Double.parseDouble(intensities[1]),
                        Double.parseDouble(intensities[3]));

                OrientationVector original = new OrientationVector(rho, delta, eta);
                IOrientationVector calculated = _converter.convert(iVector);

                System.out.println(counter);
                equals = _checkForwardAnglePrecision(original, calculated, error);

            }

        }

        assertTrue(equals);
    }

    private boolean isGreaterThan(double value, BigDecimal threshold) {
        return new BigDecimal(value).compareTo(threshold) == 1;
    }

    private boolean isLessThan(double value, BigDecimal threshold) {
        return new BigDecimal(value).compareTo(threshold) == -1;
    }

    /**
     * To check forward angle precision, we check that delta and eta have acceptable
     * error. For rho, we check that delta or (pi-delta) are close to the forward
     * angle (this is due to rounding error around 0 degree as mentiond.)
     */
    private static boolean _checkForwardAnglePrecision(IOrientationVector vec1, IOrientationVector vec2, double error) {
        return (
            _checkPrecision(
                Math.PI - vec1.getAngle(OrientationAngle.rho), vec2.getAngle(OrientationAngle.rho),error)
            || _checkPrecision(vec1.getAngle(OrientationAngle.rho), vec2.getAngle(OrientationAngle.rho), error))
            && _checkPrecision(vec1.getAngle(OrientationAngle.delta), vec2.getAngle(OrientationAngle.delta), error)
            && _checkPrecision(vec1.getAngle(OrientationAngle.eta), vec2.getAngle(OrientationAngle.eta), error);
    }

    private static boolean _checkPrecision(double val1, double val2, double error) {
        return Math.abs(val1 - val2) < error;
    }

    private BufferedReader _readFile(String file) {
        InputStream stream = IntensityToOrientationConverterTest.class.getResourceAsStream(file);
        InputStreamReader iReader = new InputStreamReader(stream);
        return new BufferedReader(iReader);

    }

}