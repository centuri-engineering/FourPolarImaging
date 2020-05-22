package fr.fresnel.fourPolar.algorithm.preprocess.registration.descriptorBased;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.fourPolar.IIntensityVectorIterator;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImageToImgLib2Converter;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImgLib2ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.metadata.Metadata;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImage;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImageSet;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;
import fr.fresnel.fourPolar.core.preprocess.registration.RegistrationOrder;
import fr.fresnel.fourPolar.core.preprocess.registration.IChannelRegistrationResult;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class DescriptorBasedRegistrationTest {
    private String root = DescriptorBasedRegistrationTest.class.getResource("").getPath();

    @Test
    public void register_beadSet_ResigtrationParamsMatchImageJGUI() {
        DescriptorBasedRegistration registrator = new DescriptorBasedRegistration();

        File pol0 = new File(root, "/BeadSet/pol0.tif");
        File pol45 = new File(root, "/BeadSet/pol45.tif");
        File pol90 = new File(root, "/BeadSet/pol90.tif");
        File pol135 = new File(root, "/BeadSet/pol135.tif");

        DummyPolSet polSet = new DummyPolSet(pol0, pol45, pol90, pol135);
        IChannelRegistrationResult result = registrator.register(polSet);

        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol45_to_Pol0));
        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol90_to_Pol0));
        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol135_to_Pol0));

        assertTrue(result.error(RegistrationOrder.Pol45_to_Pol0) < 1);
        assertTrue(result.error(RegistrationOrder.Pol90_to_Pol0) < 1);
        assertTrue(result.error(RegistrationOrder.Pol135_to_Pol0) < 1);

    }

    @Test
    public void register_LowFPSet_ResigtrationParamsMatchImageJGUI() {
        DescriptorBasedRegistration registrator = new DescriptorBasedRegistration();

        File pol0 = new File(root, "/LowFPSet/AVG_Pol0.tif");
        File pol45 = new File(root, "/LowFPSet/AVG_Pol45.tif");
        File pol90 = new File(root, "/LowFPSet/AVG_Pol90.tif");
        File pol135 = new File(root, "/LowFPSet/AVG_Pol135.tif");

        DummyPolSet polSet = new DummyPolSet(pol0, pol45, pol90, pol135);
        IChannelRegistrationResult result = registrator.register(polSet);

        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol45_to_Pol0));
        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol90_to_Pol0));
        assertTrue(result.registrationSuccessful(RegistrationOrder.Pol135_to_Pol0));

        assertTrue(result.error(RegistrationOrder.Pol45_to_Pol0) < 1);
        assertTrue(result.error(RegistrationOrder.Pol90_to_Pol0) < 1);
        assertTrue(result.error(RegistrationOrder.Pol135_to_Pol0) < 1);

        result.getResultFiles(RegistrationOrder.Pol45_to_Pol0).forEach((t, x) -> {
            try {
                ImageJFunctions.show(ImageToImgLib2Converter.getImg(x, RGB16.zero()));

            } catch (ConverterToImgLib2NotFound e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        System.out.println("x");


    }

}

class DummyPolSet implements IPolarizationImageSet {
    DummyPol pol0;
    DummyPol pol45;
    DummyPol pol90;
    DummyPol pol135;

    public DummyPolSet(File pol0File, File pol45File, File pol90File, File pol135File) {
        ImgOpener opener = new ImgOpener();

        Img<UnsignedShortType> pol0 = opener.openImgs(pol0File.getAbsolutePath(), new UnsignedShortType()).get(0);
        Img<UnsignedShortType> pol45 = opener.openImgs(pol45File.getAbsolutePath(), new UnsignedShortType()).get(0);
        Img<UnsignedShortType> pol90 = opener.openImgs(pol90File.getAbsolutePath(), new UnsignedShortType()).get(0);
        Img<UnsignedShortType> pol135 = opener.openImgs(pol135File.getAbsolutePath(), new UnsignedShortType()).get(0);

        long[] dim = new long[pol0.numDimensions()];
        pol0.dimensions(dim);
        IMetadata metadata = new Metadata.MetadataBuilder(dim).build();

        ImgLib2ImageFactory factory = new ImgLib2ImageFactory();
        this.pol0 = new DummyPol(factory.create(pol0, new UnsignedShortType(), metadata));
        this.pol45 = new DummyPol(factory.create(pol45, new UnsignedShortType(), metadata));
        this.pol90 = new DummyPol(factory.create(pol90, new UnsignedShortType(), metadata));
        this.pol135 = new DummyPol(factory.create(pol135, new UnsignedShortType(), metadata));
    }

    @Override
    public IPolarizationImage getPolarizationImage(Polarization pol) {
        if (pol == Polarization.pol0) {
            return pol0;
        } else if (pol == Polarization.pol45) {
            return pol45;
        } else if (pol == Polarization.pol90) {
            return pol90;
        } else {
            return pol135;
        }
    }

    @Override
    public ICapturedImageFileSet getFileSet() {
        return null;
    }

    @Override
    public IIntensityVectorIterator getIterator() {
        return null;
    }

    @Override
    public int channel() {
        return 0;
    }

}

class DummyPol implements IPolarizationImage {

    Image<UINT16> img;

    public DummyPol(Image<UINT16> img) {
        this.img = img;
    }

    @Override
    public Polarization getPolarization() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Image<UINT16> getImage() {
        return this.img;
    }

}