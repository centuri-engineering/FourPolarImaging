package fr.fresnel.fourPolar.io.imagingSetup;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.junit.jupiter.api.Test;

import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.imagingSetup.IFourPolarImagingSetup;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.fov.FieldOfView;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.fov.IFieldOfView;
import fr.fresnel.fourPolar.core.physics.channel.Channel;
import fr.fresnel.fourPolar.core.physics.channel.IChannel;
import fr.fresnel.fourPolar.core.physics.na.INumericalAperture;
import fr.fresnel.fourPolar.core.physics.na.NumericalAperture;
import fr.fresnel.fourPolar.core.shape.IBoxShape;
import fr.fresnel.fourPolar.core.shape.ShapeFactory;

/**
 * Note that in all the test, the files are created in the 4POLAR subfolder of
 * the root.
 */
public class FourPolarImagingSetupToYamlTest {

    @Test
    public void write_WriteOneCameraTWoChannel_FileGeneratedinResourceFolder()
            throws JsonGenerationException, JsonMappingException, IOException {
        IFourPolarImagingSetup imagingSetup = new DummyFPSetupForWrite();

        imagingSetup.setCameras(Cameras.One);

        IBoxShape rect0 = ShapeFactory.closedBox(new long[] { 0, 0 }, new long[] { 127, 127 }, AxisOrder.XY);
        IBoxShape rect45 = ShapeFactory.closedBox(new long[] { 127, 0 }, new long[] { 255, 127 }, AxisOrder.XY);
        IBoxShape rect90 = ShapeFactory.closedBox(new long[] { 0, 127 }, new long[] { 127, 255 }, AxisOrder.XY);
        IBoxShape rect135 = ShapeFactory.closedBox(new long[] { 127, 127 }, new long[] { 255, 255 },
                AxisOrder.XY);

        FieldOfView fov = new FieldOfView(rect0, rect45, rect90, rect135);
        imagingSetup.setFieldOfView(fov);

        INumericalAperture na = new NumericalAperture(1.45, 5.65, 3.4342, 1.3434);
        imagingSetup.setNumericalAperture(na);

        Channel prop = new Channel(1e-9, 1.45, 1.54, 1.34, 3.11);
        imagingSetup.setChannel(1, prop);
        imagingSetup.setChannel(2, prop);

        File rootFolder = new File(FourPolarImagingSetupToYamlTest.class.getResource("").getPath(), "ToYaml");
        rootFolder.mkdir();
        FourPolarImagingSetupToYaml writer = new FourPolarImagingSetupToYaml(imagingSetup, rootFolder);
        writer.write();

    }

}

class DummyFPSetupForWrite implements IFourPolarImagingSetup {
    private Cameras cameras;
    private Hashtable<Integer, IChannel> channels = new Hashtable<>();
    private INumericalAperture na;
    private IFieldOfView fov;

    @Override
    public Cameras getCameras() {
        return cameras;
    }

    @Override
    public void setCameras(Cameras cameras) throws IllegalArgumentException {
        this.cameras = cameras;
    }

    @Override
    public IChannel getChannel(int channel) throws IllegalArgumentException {
        return channels.get(channel);
    }

    @Override
    public void setChannel(int channel, IChannel propagationChannel) throws IllegalArgumentException {
        channels.put(channel, propagationChannel);
    }

    @Override
    public int getNumChannel() {
        return this.channels.size();
    }

    @Override
    public INumericalAperture getNumericalAperture() {
        return na;
    }

    @Override
    public void setNumericalAperture(INumericalAperture na) {
        this.na = na;
    }

    @Override
    public IFieldOfView getFieldOfView() {
        return this.fov;
    }

    @Override
    public void setFieldOfView(IFieldOfView fov) throws IllegalArgumentException {
        this.fov = fov;
    }

}