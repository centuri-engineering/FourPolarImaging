package fr.fresnel.fourPolar.io.image.generic.tiff.scifio;

import java.io.File;
import java.io.IOException;
import java.security.KeyException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.fresnel.fourPolar.core.exceptions.imageSet.acquisition.IncompatibleCapturedImage;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class Float32ImgLib2TiffImageReaderTest {
    private static File _testResource;

    @BeforeAll
    private static void setTestResouce() {
        _testResource = new File(Float32ImgLib2TiffImageReaderTest.class.getResource("Readers").getPath());
    }

    @Test
    public void read_UShortTiff_ShouldShowImage()
            throws IllegalArgumentException, IOException, InterruptedException, KeyException, IncompatibleCapturedImage {
        final File image = new File(_testResource, "UINT16.tif");

        final UINT16ImgLib2TiffImageReader reader = new UINT16ImgLib2TiffImageReader(
                new UnsignedShortType());
        Img<UnsignedShortType> img = reader.read(image);
        ImageJFunctions.show(img);
        TimeUnit.SECONDS.sleep(10);
                
    }

    @Test
    public void read_FloatTiff_ShouldShowImage()
            throws IllegalArgumentException, IOException, InterruptedException, KeyException, IncompatibleCapturedImage {
        final File image = new File(_testResource, "FloatImage.tif");

        final TiffGrayScaleReader<FloatType> reader = new TiffGrayScaleReader<FloatType>(
                new FloatType());
        Img<FloatType> img = reader.read(image);
        ImageJFunctions.show(img);
        TimeUnit.SECONDS.sleep(10);

    }

    @Test
    public void read_SameImageTenThousandTimes_ShouldNotRunOutOfResource()
            throws IllegalArgumentException, IOException, InterruptedException, KeyException, IncompatibleCapturedImage {
        final File image = new File(_testResource, "FloatImage.tif");
        final TiffGrayScaleReader<FloatType> reader = new TiffGrayScaleReader<FloatType>(
                new FloatType());
        for (int i = 0; i < 10000; i++) {
            reader.read(image);
        }
        
        reader.close();


    }


}