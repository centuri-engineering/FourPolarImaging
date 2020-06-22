package fr.fresnel.fourPolar.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import fr.fresnel.fourPolar.algorithm.preprocess.fov.FoVCalculator;
import fr.fresnel.fourPolar.algorithm.preprocess.fov.IFoVCalculator;
import fr.fresnel.fourPolar.algorithm.util.image.color.GrayScaleToColorConverter;
import fr.fresnel.fourPolar.algorithm.util.image.color.GrayScaleToColorConverter.Color;
import fr.fresnel.fourPolar.algorithm.visualization.figures.polarization.PolarizationImageSetCompositesCreator;
import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.exceptions.imageSet.acquisition.IncompatibleCapturedImage;
import fr.fresnel.fourPolar.core.image.captured.file.CapturedImageFileSetBuilder;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImageToImgLib2Converter;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImgLib2ImageFactory;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImageSet;
import fr.fresnel.fourPolar.core.imageSet.acquisition.registration.RegistrationImageSet;
import fr.fresnel.fourPolar.core.imageSet.acquisition.registration.RegistrationImageSet.RegistrationImageType;
import fr.fresnel.fourPolar.core.imageSet.acquisition.sample.SampleImageSet;
import fr.fresnel.fourPolar.core.imagingSetup.FourPolarImagingSetup;
import fr.fresnel.fourPolar.core.imagingSetup.IFourPolarImagingSetup;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.fov.IFieldOfView;
import fr.fresnel.fourPolar.core.physics.channel.Channel;
import fr.fresnel.fourPolar.core.physics.na.NumericalAperture;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;
import fr.fresnel.fourPolar.core.preprocess.RegistrationSetProcessResult;
import fr.fresnel.fourPolar.core.preprocess.registration.RegistrationRule;
import fr.fresnel.fourPolar.core.visualization.figures.polarization.IPolarizationImageSetComposites;
import fr.fresnel.fourPolar.io.exceptions.image.generic.metadata.MetadataParseError;
import fr.fresnel.fourPolar.io.image.captured.tiff.checker.TiffCapturedImageChecker;
import fr.fresnel.fourPolar.io.image.generic.IMetadataReader;
import fr.fresnel.fourPolar.io.image.generic.tiff.scifio.SCIFIOUINT16TiffReader;
import fr.fresnel.fourPolar.io.image.generic.tiff.scifio.metadata.SCIFIOMetadataReader;
import fr.fresnel.fourPolar.io.image.polarization.TiffPolarizationImageSetWriter;
import fr.fresnel.fourPolar.io.imagingSetup.FourPolarImagingSetupToYaml;
import fr.fresnel.fourPolar.io.preprocess.RegistrationSetProcessResultToYAML;
import fr.fresnel.fourPolar.io.visualization.figures.polarization.tiff.TiffPolarizationImageSetCompositesWriter;
import fr.fresnel.fourPolar.ui.algorithms.preprocess.registrationSet.IRegistrationSetProcessor;
import fr.fresnel.fourPolar.ui.algorithms.preprocess.registrationSet.RegistrationSetProcessorBuilder;
import fr.fresnel.fourPolar.ui.algorithms.preprocess.sampleSet.ISampleImageSetPreprocessor;
import fr.fresnel.fourPolar.ui.algorithms.preprocess.sampleSet.SampleImageSetPreprocessorBuilder;
import fr.fresnel.fourPolar.ui.exceptions.algorithms.preprocess.registrationSet.RegistrationSetProcessFailure;
import fr.fresnel.fourPolar.ui.exceptions.algorithms.preprocess.sampleSet.SampleSetPreprocessFailure;
import javassist.tools.reflect.CannotCreateException;
import net.imglib2.RealPoint;
import net.imglib2.img.display.imagej.ImageJFunctions;

/**
 * With this pre-choice, Sophie (AKA boss) can preprocess a sample image. For
 * this end, she has to provide a bead image (which can also be the same sample
 * image as well).
 * 
 * Use control + space to see the list of possibilities for a variable.
 * 
 * To use this code, Sophie only needs to fill the static parameters in
 * SophiesPreChoice up to the point where she sees the message that says don't
 * change anything from here.
 */
public class SophiesPreChoice {
    // Introduce yourself
    public static String userName = "Sophie 'The Boss' Brasselet";

    // RootFolder
    public static String rootFolder = "D:\\4PolarBackendTest\\Masoud";

    // Registration image
    public static String registrationImage = "AVG_rotor_60.tif";

    // Registration image type
    public static RegistrationImageType registrationImageType = RegistrationImageType.SAMPLE;

    // Sample image
    public static String sampleImage = "AVG_rotor_60.tif";

    // Number of channels
    public static int[] channels = { 1 };

    // Wavelength (in meter)
    public static double[] wavelengths = { 1e-9 };

    // Number of cameras
    public static Cameras camera = Cameras.One;

    // Registration result composite image colors.
    private static Color baseImageColor = Color.Green;
    private static Color toRegisterImageColor = Color.Red;

    private static Bdv registrationImageViewer;

    public static void main(String[] args) throws CannotCreateException, IncompatibleCapturedImage, IOException {
        // -------------------------------------------------------------------
        // YOU DON'T NEED TO CHANGE ANYTHING FROM HERE ON!
        // -------------------------------------------------------------------
        beadImageSet = createRegistrationImageSet();
        sampleImageSet = createSampleImageSet();

        _showRegistrationImageSetForFoVCalculation();

    }

    // Imaging setup
    public static IFourPolarImagingSetup setup = initializeImagingSetup();

    // Bead image set
    private static RegistrationImageSet beadImageSet = null;

    // Sample image set.
    private static SampleImageSet sampleImageSet = null;

    private static IFourPolarImagingSetup initializeImagingSetup() {
        IFourPolarImagingSetup setup = FourPolarImagingSetup.instance();
        setup.setCameras(camera);

        for (int i = 0; i < channels.length; i++) {
            setup.setChannel(channels[i], new Channel(wavelengths[i], 0, 0, 0, 0));
        }

        setup.setNumericalAperture(new NumericalAperture(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        return setup;
    }

    public static void writeSetupToDisk() throws IOException {
        FourPolarImagingSetupToYaml writer = new FourPolarImagingSetupToYaml(setup, sampleImageSet.rootFolder());
        writer.write();
    }

    public static RegistrationImageSet createRegistrationImageSet()
            throws CannotCreateException, IncompatibleCapturedImage {
        RegistrationImageSet beadImageSet = new RegistrationImageSet(new File(rootFolder), registrationImageType);

        File beadImagePath = new File(rootFolder, registrationImage);
        ICapturedImageFileSet beadCapturedImageFileSet = createFileSet(beadImagePath);

        beadImageSet.addImageSet(beadCapturedImageFileSet);

        return beadImageSet;
    }

    public static SampleImageSet createSampleImageSet() throws CannotCreateException, IncompatibleCapturedImage {
        SampleImageSet sampleImageSet = new SampleImageSet(new File(rootFolder));

        File sampleImagePath = new File(rootFolder, sampleImage);
        ICapturedImageFileSet sampleCapturedImageFileSet = createFileSet(sampleImagePath);

        sampleImageSet.addImageSet(sampleCapturedImageFileSet);

        return sampleImageSet;
    }

    public static ICapturedImageFileSet createFileSet(File pol0_45_90_135)
            throws CannotCreateException, IncompatibleCapturedImage {
        IMetadataReader metadataReader = new SCIFIOMetadataReader();
        TiffCapturedImageChecker checker = new TiffCapturedImageChecker(metadataReader);
        CapturedImageFileSetBuilder builder = new CapturedImageFileSetBuilder(setup, checker);

        builder.add(channels, pol0_45_90_135);

        return builder.build();
    }

    private static void _showRegistrationImageSetForFoVCalculation() {
        File beadImagePath = beadImageSet.getIterator().next().getFile(Cameras.getLabels(camera)[0])[0].file();

        try {
            Image<UINT16> beadImageGray = new SCIFIOUINT16TiffReader(new ImgLib2ImageFactory()).read(beadImagePath);
            Image<RGB16> beadImageColor = GrayScaleToColorConverter.colorUsingMaxEachPlane(beadImageGray);
            registrationImageViewer = BdvFunctions.show(ImageToImgLib2Converter.getImg(beadImageColor, RGB16.zero()),
                    "SoI", BdvOptions.options().is2D());

            Behaviours behaviours = new Behaviours(new InputTriggerConfig());
            behaviours.install(registrationImageViewer.getBdvHandle().getTriggerbindings(), "my-new-behaviours");

            CalculateFoVAndContinue doubleClick = new CalculateFoVAndContinue(registrationImageViewer);
            behaviours.behaviour(doubleClick, "print global pos", "button1");

        } catch (IOException | MetadataParseError | ConverterToImgLib2NotFound e) {
            e.printStackTrace();
        }
    }

    public static IFoVCalculator createFieldOfViewCalculator() {
        File beadImage = beadImageSet.getIterator().next().getFile(Cameras.getLabels(camera)[0])[0].file();

        IMetadataReader metadataReader = new SCIFIOMetadataReader();
        IMetadata metadata = null;
        try {
            metadata = metadataReader.read(beadImage);
        } catch (IOException | MetadataParseError e) {
            // Caught before.
        }

        return FoVCalculator.oneCamera(metadata);
    }

    public static IRegistrationSetProcessor createRegistrationSetProcessor() {
        return new RegistrationSetProcessorBuilder(setup).registrationCompositeCreator(
                new PolarizationImageSetCompositesCreator(channels.length, baseImageColor, toRegisterImageColor))
                .build();
    }

    public static ISampleImageSetPreprocessor createSampleImageSetPreprocessor(RegistrationSetProcessResult result) {
        SampleImageSetPreprocessorBuilder processorBuilder = new SampleImageSetPreprocessorBuilder(setup, result);
        return processorBuilder.build();
    }

}

class CalculateFoVAndContinue implements ClickBehaviour {
    Bdv bdv;
    IFoVCalculator foVCalculator;

    int numClicksToDetectFoVs = 5;
    int clickCounter = 0;

    public CalculateFoVAndContinue(Bdv bdv) {
        this.bdv = bdv;
        foVCalculator = SophiesPreChoice.createFieldOfViewCalculator();
    }

    @Override
    public void click(int x, int y) {
        if (clickCounter == 1) {
            this._setFoVPol0Max(x, y);                     
        }else {
            this._setFoVMinimum(clickCounter, x, y);
        }
        clickCounter++;

        if (clickCounter >= numClicksToDetectFoVs) {
            this._setFoVPol45_90_135Max();
            bdv.close();
            IFieldOfView fov = createFoV();

            SophiesPreChoice.setup.setFieldOfView(fov);

            RegistrationImageSet registrationImageSet = this.createRegImageSet();

            // Call preprocessor here
            IRegistrationSetProcessor preprocessor = SophiesPreChoice.createRegistrationSetProcessor();
            RegistrationSetProcessResult result = this.preprocessRegistrationSet(registrationImageSet, preprocessor);

            _serializeRegistrationSetProcess(result, new File(SophiesPreChoice.rootFolder));

            _writeRegistrationComposites(registrationImageSet, preprocessor);

            // Call processor here.
            _processAndWriteSampleImages(result);

            try {
                SophiesPreChoice.writeSetupToDisk();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void _processAndWriteSampleImages(RegistrationSetProcessResult result) {
        SampleImageSet sampleImageSet = this.createSampleSet();
        TiffPolarizationImageSetWriter writer = new TiffPolarizationImageSetWriter();
        ISampleImageSetPreprocessor processor = SophiesPreChoice.createSampleImageSetPreprocessor(result);
        for (Iterator<ICapturedImageFileSet> itr = sampleImageSet.getIterator(); itr.hasNext();) {
            try {
                processor.setCapturedImageSet(itr.next());
            } catch (SampleSetPreprocessFailure e) {
                e.printStackTrace();
            }

            for (int channel = 1; channel <= SophiesPreChoice.channels.length; channel++) {
                IPolarizationImageSet polSet = null;
                try {
                    polSet = processor.getPolarizationImageSet(channel);
                } catch (SampleSetPreprocessFailure e) {
                    e.printStackTrace();
                }
                writeSamplePolarizationImage(writer, sampleImageSet, polSet);
            }
        }
    }

    private void _writeRegistrationComposites(RegistrationImageSet registrationImageSet,
            IRegistrationSetProcessor preprocessor) {
        TiffPolarizationImageSetCompositesWriter compositesWriter = new TiffPolarizationImageSetCompositesWriter();
        for (int channel = 1; channel <= SophiesPreChoice.channels.length; channel++) {
            IPolarizationImageSetComposites composites = preprocessor.getRegistrationComposite(channel).get();
            this.showRegistrationCompositeImage(composites);
            this.writeComposite(compositesWriter, composites, registrationImageSet.rootFolder());
        }
    }

    private void writeSamplePolarizationImage(TiffPolarizationImageSetWriter writer, SampleImageSet sampleImageSet,
            IPolarizationImageSet polSet) {
        try {
            writer.write(sampleImageSet.rootFolder(), polSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SampleImageSet createSampleSet() {
        try {
            return SophiesPreChoice.createSampleImageSet();
        } catch (CannotCreateException | IncompatibleCapturedImage e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeComposite(TiffPolarizationImageSetCompositesWriter compositesWriter,
            IPolarizationImageSetComposites composites, File rootFolder) {
        try {
            compositesWriter.writeAsRegistrationComposite(rootFolder, composites);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private RegistrationSetProcessResult preprocessRegistrationSet(RegistrationImageSet registrationImageSet,
            IRegistrationSetProcessor preprocessor) {
        try {
            return preprocessor.process(registrationImageSet);
        } catch (RegistrationSetProcessFailure e) {
            e.printStackTrace();
            return null;
        }
    }

    private RegistrationImageSet createRegImageSet() {
        try {
            return SophiesPreChoice.createRegistrationImageSet();
        } catch (CannotCreateException | IncompatibleCapturedImage e) {
            return null;
        }
    }

    private IFieldOfView createFoV() {
        // Create FoV here
        return foVCalculator.calculate();
    }

    private void showRegistrationCompositeImage(IPolarizationImageSetComposites composites) {
        try {
            for (RegistrationRule rule : RegistrationRule.values()) {
                ImageJFunctions.show(
                        ImageToImgLib2Converter.getImg(composites.getCompositeImage(rule).getImage(), RGB16.zero()),
                        rule.name() + " of channel " + composites.channel());

            }

        } catch (ConverterToImgLib2NotFound e) {

        }
    }

    private void _serializeRegistrationSetProcess(RegistrationSetProcessResult result, File root4PProject) {
        // Preprocess result serializer
        RegistrationSetProcessResultToYAML processResultToYAML = new RegistrationSetProcessResultToYAML(
                SophiesPreChoice.setup, result);

        try {
            processResultToYAML.write(root4PProject);
        } catch (IOException e) {
            System.out.println("Unable to write registration result to disk.");
            e.printStackTrace();
        }
    }

    private void _setFoVMinimum(int numClicks, int x, int y) {
        long[] coordinate = _convertClickPointToPixelCoordinate(x, y);

        if (numClicks == 0) {
            foVCalculator.setMin(coordinate[0], coordinate[1], Polarization.pol0);
        } else if (numClicks == 2) {
            foVCalculator.setMin(coordinate[0], coordinate[1], Polarization.pol45);
        } else if (numClicks == 3) {
            foVCalculator.setMin(coordinate[0], coordinate[1], Polarization.pol90);
        } else if (numClicks == 4) {
            foVCalculator.setMin(coordinate[0], coordinate[1], Polarization.pol135);
        }

    }

    private void _setFoVPol0Max(int x, int y) {
        long[] coordinate = _convertClickPointToPixelCoordinate(x, y);
        foVCalculator.setMax(coordinate[0], coordinate[1], Polarization.pol0);
    }

    private void _setFoVPol45_90_135Max() {
        long[] len = _getPol0FovLen();

        long[] pol45_min = foVCalculator.getMinPoint(Polarization.pol45);
        long[] pol90_min = foVCalculator.getMinPoint(Polarization.pol90);
        long[] pol135_min = foVCalculator.getMinPoint(Polarization.pol135);

        foVCalculator.setMax(pol45_min[0] + len[0], pol45_min[1] + len[1], Polarization.pol45);
        foVCalculator.setMax(pol90_min[0] + len[0], pol90_min[1] + len[1], Polarization.pol90);
        foVCalculator.setMax(pol135_min[0] + len[0], pol135_min[1] + len[1], Polarization.pol135);

    }

    private long[] _getPol0FovLen() {
        long[] pol0Min = foVCalculator.getMinPoint(Polarization.pol0);
        long[] pol0Max = foVCalculator.getMaxPoint(Polarization.pol0);

        return new long[]{pol0Max[0] - pol0Min[0] + 1, pol0Max[1] - pol0Min[1] + 1};
    }

    private long[] _convertClickPointToPixelCoordinate(int x, int y) {
        final RealPoint pos = new RealPoint(5);
        bdv.getBdvHandle().getViewerPanel().displayToGlobalCoordinates(x, y, pos);

        double[] pos1 = new double[5];
        pos.localize(pos1);
        long[] pos2 = Arrays.stream(pos1).mapToLong((t) -> (long) t).limit(2).toArray();
        return pos2;
    }

}