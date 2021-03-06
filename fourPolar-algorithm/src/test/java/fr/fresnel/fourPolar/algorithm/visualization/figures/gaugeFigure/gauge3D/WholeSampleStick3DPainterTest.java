package fr.fresnel.fourPolar.algorithm.visualization.figures.gaugeFigure.gauge3D;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.exceptions.image.orientation.CannotFormOrientationImage;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFile;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImageToImgLib2Converter;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImgLib2ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.metadata.Metadata;
import fr.fresnel.fourPolar.core.image.generic.pixel.IPixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.Pixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.Float32;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.ARGB8;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.color.ColorBlender;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.color.SoftLightColorBlender;
import fr.fresnel.fourPolar.core.image.orientation.IOrientationImage;
import fr.fresnel.fourPolar.core.image.orientation.OrientationImageFactory;
import fr.fresnel.fourPolar.core.image.soi.ISoIImage;
import fr.fresnel.fourPolar.core.image.soi.SoIImage;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;
import fr.fresnel.fourPolar.core.shape.IShape;
import fr.fresnel.fourPolar.core.shape.ShapeFactory;
import fr.fresnel.fourPolar.core.util.image.generic.colorMap.ColorMap;
import fr.fresnel.fourPolar.core.util.image.generic.colorMap.ColorMapFactory;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.IAngleGaugePainter;
import ij.ImagePlus;
import ij.io.FileSaver;
import net.imglib2.img.display.imagej.ImageJFunctions;

/**
 * Note that when creating Image for gauge figure in this text, the number of z
 * planes must soi_z * sticklen.
 */
public class WholeSampleStick3DPainterTest {
    /**
     * The figure generated in this example would be the same as the Delta2DStick,
     * because all sticks would be in the same plane.
     * 
     * The importance of this test is to ensure that the 3D figure can be generated
     * for the planar images.
     * 
     * The sticks that are drawn are flipped, in the sense that instead of showing
     * 45 they show 135. Note that three planes would be filled, because stick has
     * non-zero length in all directions
     * 
     * @throws InterruptedException
     */
    @Test
    public void draw_SingleZPlaneEta90_DrawsFlippedAngleSticks()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound, InterruptedException {
        long[] dim = { 1024, 512, 1, 1, 1 };
        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        int j = 0;
        for (int i = 0; i <= 180; i += 1) {
            j = i % 20 >= 1 ? j : j + 2;
            setPixel(rhoRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(i)));
            setPixel(deltaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(i)));
            setPixel(etaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(90)));
        }

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 1024, 512, 0, 0, 0 }, axisOrder);

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());

        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);
        painter.draw(entireImageRegion, new UINT16(0));

        IGaugeFigure stickFigure = painter.getFigure();
        _saveStickFigure(stickFigure, "3DStick_SingleZPlaneEta90.tiff");

        assertTrue(true);

    }

    /**
     * The purpose of this test is to show that for 3dim images of xyt, the stick
     * figure is generated properly. The sticks that are drawn are flipped, in the
     * sense that instead of showing 45 they show 135, and they are at the same
     * plane for each time
     * 
     */
    @Test
    public void draw_SingleZPlaneEta90OverTime_DrawsFlippedAngleSticks()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound, InterruptedException {
        long[] dim = { 1024, 512, 1, 1, 3 };
        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        for (int t = 0; t < dim[4]; t++) {
            int j = 0;
            for (int i = 0; i <= 180; i += 1) {
                j = i % 20 >= 1 ? j : j + 2;
                setPixel(rhoRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, t },
                        new Float32((float) Math.toRadians(i)));
                setPixel(deltaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, t },
                        new Float32((float) Math.toRadians(i)));
                setPixel(etaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, t },
                        new Float32((float) Math.toRadians(90)));
            }

        }

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 1021, 511, 0, 0, 2 }, axisOrder);

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());

        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);
        painter.draw(entireImageRegion, new UINT16(0));

        IGaugeFigure stickFigure = painter.getFigure();
        _saveStickFigure(stickFigure, "3DStick_SingleZPlaneMultipleTimeEta90.tiff");

        assertTrue(true);

    }

    /**
     * The figure generated in this example would be the same as the Delta2DStick,
     * because all sticks would be in the same plane. The occupied z planes would be
     * 10 +-1, 30+-1, 50+-1 for each time stamp. Why plus minus? Because the stick
     * has a length in the x-y plane too.
     * 
     * The sticks that are drawn are flipped, in the sense that instead of showing
     * 45 they show 135.
     * 
     * @throws InterruptedException
     */
    @Test
    public void draw_MultiZPlaneEta90_DrawsFlippedAngleSticks()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound, InterruptedException {
        long[] dim = { 1024, 512, 1, 3, 2 }; // three z, two t.

        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        for (int t = 0; t < dim[4]; t++) {
            for (int z = 0; z < dim[3]; z++) {
                int j = 0;
                for (int i = 0; i <= 180; i += 1) {
                    j = i % 20 >= 1 ? j : j + 2;
                    setPixel(rhoRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, t },
                            new Float32((float) Math.toRadians(i)));
                    setPixel(deltaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, t },
                            new Float32((float) Math.toRadians(i)));
                    setPixel(etaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, t },
                            new Float32((float) Math.toRadians(90)));
                }

            }
        }

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());
        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);

        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 1024, 512, 0, 3, 2 }, axisOrder);
        painter.draw(entireImageRegion, new UINT16(0));
        IGaugeFigure stickFigure = painter.getFigure();

        _saveStickFigure(stickFigure, "3DStick_MultipleTimeAndZ.tiff");

        assertTrue(true);

    }

    /**
     * The figure generated is a straight line regardless of rho and delta values
     * for all z values.
     * 
     * @throws InterruptedException
     */
    @Test
    public void draw_MultiZPlaneEta0_DrawsPerpendicularSticks()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound, InterruptedException {
        long[] dim = { 1024, 512, 1, 3, 1 };
        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        for (int z = 0; z < dim[3]; z++) {
            int j = 0;
            for (int i = 0; i <= 180; i += 1) {
                j = i % 20 >= 1 ? j : j + 2;
                setPixel(rhoRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, 0 },
                        new Float32((float) Math.toRadians(i)));
                setPixel(deltaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, 0 },
                        new Float32((float) Math.toRadians(i)));
                setPixel(etaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, z, 0 },
                        new Float32((float) Math.toRadians(0)));
            }
        }

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());
        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);

        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 1023, 511, 0, 2, 0 }, AxisOrder.XYCZT);
        painter.draw(entireImageRegion, new UINT16(0));

        _saveStickFigure(painter.getFigure(), "3DStick_MultipleZPlaneEta0.tiff");

        assertTrue(true);

    }

    /**
     * In this test, we define a region which exceeds the dimensions the SoIImage,
     * but the singleZ-Plane sticks are drawn correctly.
     * 
     * @throws CannotFormOrientationImage
     * @throws ConverterToImgLib2NotFound
     */
    @Test
    public void draw_RegionOutOfSoIImage_DrawsSticksInsideTheSoiIImage()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound {
        long[] dim = { 1024, 512, 1, 1, 1 };
        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        int j = 0;
        for (int i = 0; i <= 180; i += 1) {
            j = i % 20 >= 1 ? j : j + 2;
            setPixel(rhoRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(i)));
            setPixel(deltaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(i)));
            setPixel(etaRA, new long[] { 70 + ((i % 20) * 45), 5 + j * 25, 0, 0, 0 },
                    new Float32((float) Math.toRadians(90)));
        }

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());
        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);

        // Notice the region is out of image dimensions.
        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 2000, 2000, 0, 0, 0 }, axisOrder);
        painter.draw(entireImageRegion, new UINT16(0));
        IGaugeFigure stickFigure = painter.getFigure();

        _saveStickFigure(stickFigure, "3DStick_SingleZPlane_RoIOutOfRange.tiff");

        assertTrue(true);

    }

    /**
     * In this test, we have sticks that are going outside the boundary of the image
     * frame. but the sticks are drawn regardless.
     * 
     * @throws CannotFormOrientationImage
     * @throws ConverterToImgLib2NotFound
     */
    @Test
    public void draw_OutOfRangeSticks_DrawsPartOfStickInsideFrame()
            throws CannotFormOrientationImage, ConverterToImgLib2NotFound {
        long[] dim = { 1024, 512, 1, 1, 1 };
        AxisOrder axisOrder = AxisOrder.XYCZT;
        IMetadata metadata = new Metadata.MetadataBuilder(dim).axisOrder(axisOrder).build();

        ICapturedImageFileSet fileSet = new DummyWholeSample3DFileSet();
        Image<Float32> rhoImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> deltaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());
        Image<Float32> etaImage = new ImgLib2ImageFactory().create(metadata, Float32.zero());

        IPixelRandomAccess<Float32> rhoRA = rhoImage.getRandomAccess();
        IPixelRandomAccess<Float32> deltaRA = deltaImage.getRandomAccess();
        IPixelRandomAccess<Float32> etaRA = etaImage.getRandomAccess();

        IPixelCursor<Float32> rhoCursor = rhoImage.getCursor();
        IPixelCursor<Float32> deltaCursor = deltaImage.getCursor();
        IPixelCursor<Float32> etaCursor = etaImage.getCursor();

        while (rhoCursor.hasNext()) {
            IPixel<Float32> pixel = rhoCursor.next();
            deltaCursor.next();
            etaCursor.next();

            pixel.value().set(Float.NaN);
            rhoCursor.setPixel(pixel);
            deltaCursor.setPixel(pixel);
            etaCursor.setPixel(pixel);
        }

        long[] pixel0 = new long[] { 0, 0, 0, 0, 0 };
        setPixel(rhoRA, pixel0, new Float32((float) Math.toRadians(45)));
        setPixel(deltaRA, pixel0, new Float32((float) Math.toRadians(90)));
        setPixel(etaRA, pixel0, new Float32((float) Math.toRadians(90)));

        long[] pixel0511 = new long[] { 0, 511, 0, 0, 0 };
        setPixel(rhoRA, pixel0511, new Float32((float) Math.toRadians(90)));
        setPixel(deltaRA, pixel0511, new Float32((float) Math.toRadians(90)));
        setPixel(etaRA, pixel0511, new Float32((float) Math.toRadians(90)));

        IOrientationImage orientationImage = OrientationImageFactory.create(fileSet, 1, rhoImage, deltaImage, etaImage);
        Image<UINT16> soi = new ImgLib2ImageFactory().create(metadata, UINT16.zero());
        ISoIImage soiImage = SoIImage.create(fileSet, soi, 1);

        ColorMap cMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_PHASE);
        int length = 20;
        int thickness = 4;

        IWholeSampleStick3DPainterBuilder builder = new DummyWholeSampleStick3DBuilder(orientationImage, soiImage, cMap,
                thickness, length, new SoftLightColorBlender());
        IAngleGaugePainter painter = new WholeSampleStick3DPainter(builder);

        IShape entireImageRegion = ShapeFactory.closedBox(new long[] { 0, 0, 0, 0, 0 },
                new long[] { 1023, 511, 0, 0, 0 }, axisOrder);
        painter.draw(entireImageRegion, new UINT16(0));
        IGaugeFigure stickFigure = painter.getFigure();

        _saveStickFigure(stickFigure, "3DStick_SingleZPlane_StickOutOfImageFrame.tiff");

        assertTrue(true);

    }

    private void setPixel(IPixelRandomAccess<Float32> ra, long[] position, Float32 value) {
        ra.setPosition(position);
        ra.setPixel(new Pixel<Float32>(value));
    }

    private void _saveStickFigure(IGaugeFigure stickFigure, String stickImageName) throws ConverterToImgLib2NotFound {
        String root = WholeSampleStick3DPainterTest.class.getResource("").getPath();
        ImagePlus imp = ImageJFunctions
                .wrapRGB(ImageToImgLib2Converter.getImg(((GaugeFigure) stickFigure).getImage(), ARGB8.zero()), "RGB");
        FileSaver impSaver = new FileSaver(imp);
        File path = new File(root, stickImageName);
        impSaver.saveAsTiff(path.getAbsolutePath());
    }

}

class DummyWholeSample3DFileSet implements ICapturedImageFileSet {

    @Override
    public ICapturedImageFile[] getFile(String label) {
        return null;
    }

    @Override
    public String getSetName() {
        return "Set";
    }

    @Override
    public Cameras getnCameras() {
        return null;
    }

    @Override
    public boolean hasLabel(String label) {
        return false;
    }

    @Override
    public boolean deepEquals(ICapturedImageFileSet fileset) {
        return false;
    }

    @Override
    public Iterator<ICapturedImageFile> getIterator() {
        return null;
    }

    @Override
    public int[] getChannels() {
        return new int[] { 1 };
    }

}

class DummyWholeSampleStick3DBuilder extends IWholeSampleStick3DPainterBuilder {
    private final IOrientationImage _orientationImage;
    private final ISoIImage _soiImage;

    private ColorMap _colorMap = ColorMapFactory.create(ColorMapFactory.IMAGEJ_SPECTRUM);
    private int _thickness = 4;
    private int _length = 50;
    private ColorBlender _blender;

    @Override
    ColorMap getColorMap() {
        return this._colorMap;
    }

    @Override
    int getSticklength() {
        return this._length;
    }

    @Override
    IOrientationImage getOrientationImage() {
        return this._orientationImage;
    }

    @Override
    ISoIImage getSoIImage() {
        return this._soiImage;
    }

    @Override
    int getStickThickness() {
        return this._thickness;
    }

    public DummyWholeSampleStick3DBuilder(IOrientationImage _orientationImage, ISoIImage _soiImage, ColorMap _colorMap,
            int _thickness, int _length, ColorBlender blender) {
        this._orientationImage = _orientationImage;
        this._soiImage = _soiImage;
        this._colorMap = _colorMap;
        this._thickness = _thickness;
        this._length = _length;
        this._blender = blender;
    }

    @Override
    ColorBlender getColorBlender() {
        return this._blender;
    }

    @Override
    GaugeFigure getGaugeFigure() {
        return GaugeFigure.wholeSample3DStick(_soiImage, _length);
    }

}