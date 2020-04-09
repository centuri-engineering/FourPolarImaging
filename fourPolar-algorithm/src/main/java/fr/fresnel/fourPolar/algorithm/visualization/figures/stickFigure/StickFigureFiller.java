package fr.fresnel.fourPolar.algorithm.visualization.figures.stickFigure;

import java.util.Objects;

import fr.fresnel.fourPolar.algorithm.exceptions.visualization.figures.stickFigure.AngleStickUndefined;
import fr.fresnel.fourPolar.algorithm.visualization.figures.stickFigure.stick.AngleStickGenerator;
import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.pixel.IPixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.Float32;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationAngle;
import fr.fresnel.fourPolar.core.physics.dipole.OrientationVector;
import fr.fresnel.fourPolar.core.util.colorMap.ColorMap;
import fr.fresnel.fourPolar.core.visualization.figures.stickFigure.IStickFigure;
import fr.fresnel.fourPolar.core.visualization.figures.stickFigure.stick.IAngleStick;
import fr.fresnel.fourPolar.core.visualization.figures.stickFigure.stick.IAngleStickIterator;
import fr.fresnel.fourPolar.core.visualization.figures.stickFigure.stick.StickType;

/**
 * Using this class, we can fill the stick figure with sticks.
 */
public class StickFigureFiller {
    public static void fillWith2DStick(final IOrientationImage orientationImage, final IStickFigure stickFigure,
            final int length, final int thickness, final ColorMap colorMap) {
        Objects.requireNonNull(orientationImage, "orientationImage cannot be null");
        Objects.requireNonNull(stickFigure, "stickFigure cannot be null");
        Objects.requireNonNull(colorMap, "colorMap cannot be null");

        if (stickFigure.getType() == StickType.Rho2D) {
            _fillWithRho2DStick(orientationImage, stickFigure, length, thickness, colorMap);
        } else if (stickFigure.getType() == StickType.Delta2D) {
            _fillWithDelta2DStick(orientationImage, stickFigure, length, thickness, colorMap);
        } else {
            _fillWithEta2DStick(orientationImage, stickFigure, length, thickness, colorMap);
        }

    }

    /**
     * Using this method, we generate 2D rho sticks (@see AngleStickGenerator) for
     * all positions (all pixels) in the orientation image. All sticks will have the
     * same length and thickness with this method.
     * 
     * @param orientationImage is the desired orientation image.
     * @param stickFigure      is the stick figure that corresponds to this
     *                         orientation image (created using the
     *                         {@link ISoIImage} that corresponds to this
     *                         orientation image).
     * @param length           is the desired length of the stick in pixels, must be
     *                         greater than equal one.
     * @param colorMap         is the color map used for filling the sticks.
     * @param thickness        is the desired thickness of sticks, must be greater
     *                         than equal one.
     */
    private static void _fillWithRho2DStick(final IOrientationImage orientationImage, final IStickFigure stickFigure,
            final int length, final int thickness, final ColorMap colorMap) {
        final IPixelCursor<Float32> rhoCursor = orientationImage.getAngleImage(OrientationAngle.rho).getImage()
                .getCursor();
        final IPixelRandomAccess<RGB16> stickRA = stickFigure.getImage().getRandomAccess();
        final AngleStickGenerator stickGenerator = new AngleStickGenerator(colorMap);

        while (rhoCursor.hasNext()) {
            final float rhoAngle = rhoCursor.next().value().get();

            final long[] position = rhoCursor.localize();
            try {
                final IAngleStick angleStick = stickGenerator.generate2DStick(rhoAngle, rhoAngle,
                        OrientationVector.MAX_Rho, position, length, thickness);

                final IAngleStickIterator stickIterator = angleStick.getIterator();
                final RGB16 color = angleStick.getColor();

                while (stickIterator.hasNext()) {
                    stickRA.setPosition(position);
                    final IPixel<RGB16> stickPixel = stickRA.getPixel();
                    stickPixel.value().set(color.getR(), color.getG(), color.getB());

                }

            } catch (final AngleStickUndefined e) {
                // Skip the postions where angle is NaN.
            }
        }
    }

    /**
     * Using this method, we generate delta 2D sticks (@see AngleStickGenerator) for
     * all positions (all pixels) in the orientation image. All sticks will have the
     * same length and thickness with this method.
     * 
     * @param orientationImage is the desired orientation image.
     * @param stickFigure      is the stick figure that corresponds to this
     *                         orientation image (created using the
     *                         {@link ISoIImage} that corresponds to this
     *                         orientation image).
     * @param length           is the desired length of the stick in pixels, must be
     *                         greater than equal one.
     * @param colorMap         is the color map used for filling the sticks.
     * @param thickness        is the desired thickness of sticks, must be greater
     *                         than equal one.
     */
    private static void _fillWithDelta2DStick(final IOrientationImage orientationImage, final IStickFigure stickFigure,
            final int length, final int thickness, final ColorMap colorMap) {
        final IPixelCursor<Float32> rhoCursor = orientationImage.getAngleImage(OrientationAngle.rho).getImage()
                .getCursor();
        final IPixelCursor<Float32> deltaCursor = orientationImage.getAngleImage(OrientationAngle.rho).getImage()
                .getCursor();
        final IPixelRandomAccess<RGB16> stickRA = stickFigure.getImage().getRandomAccess();
        final AngleStickGenerator stickGenerator = new AngleStickGenerator(colorMap);

        _loopOverTwoAngles(length, thickness, rhoCursor, deltaCursor, stickRA, stickGenerator);
    }

    /**
     * Using this method, we generate eta 2D sticks (@see AngleStickGenerator) for
     * all positions (all pixels) in the orientation image. All sticks will have the
     * same length and thickness with this method.
     * 
     * @param orientationImage is the desired orientation image.
     * @param stickFigure      is the stick figure that corresponds to this
     *                         orientation image (created using the
     *                         {@link ISoIImage} that corresponds to this
     *                         orientation image).
     * @param length           is the desired length of the stick in pixels, must be
     *                         greater than equal one.
     * @param colorMap         is the color map used for filling the sticks.
     * @param thickness        is the desired thickness of sticks, must be greater
     *                         than equal one.
     */
    private static void _fillWithEta2DStick(final IOrientationImage orientationImage, final IStickFigure stickFigure,
            final int length, final int thickness, final ColorMap colorMap) {
        final IPixelCursor<Float32> rhoCursor = orientationImage.getAngleImage(OrientationAngle.rho).getImage()
                .getCursor();
        final IPixelCursor<Float32> eta = orientationImage.getAngleImage(OrientationAngle.eta).getImage().getCursor();
        final IPixelRandomAccess<RGB16> stickRA = stickFigure.getImage().getRandomAccess();
        final AngleStickGenerator stickGenerator = new AngleStickGenerator(colorMap);

        _loopOverTwoAngles(length, thickness, rhoCursor, eta, stickRA, stickGenerator);
    }

    /**
     * Loops over two orinetation angles to generate slope and color for the given pixels.
     */
    private static void _loopOverTwoAngles(final int length, final int thickness, final IPixelCursor<Float32> rhoCursor,
            final IPixelCursor<Float32> deltaCursor, final IPixelRandomAccess<RGB16> stickRA,
            final AngleStickGenerator stickGenerator) {
        while (rhoCursor.hasNext()) {
            final float rhoAngle = rhoCursor.next().value().get();
            final float deltaAngle = deltaCursor.next().value().get();

            final long[] position = rhoCursor.localize();
            try {
                final IAngleStick angleStick = stickGenerator.generate2DStick(rhoAngle, deltaAngle,
                        OrientationVector.MAX_Delta, position, length, thickness);

                final IAngleStickIterator stickIterator = angleStick.getIterator();
                final RGB16 color = angleStick.getColor();

                while (stickIterator.hasNext()) {
                    stickRA.setPosition(position);
                    final IPixel<RGB16> stickPixel = stickRA.getPixel();
                    stickPixel.value().set(color.getR(), color.getG(), color.getB());

                }

            } catch (final AngleStickUndefined e) {
                // Skip the postions where angle is NaN.
            }
        }
    }

}