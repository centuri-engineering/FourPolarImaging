package fr.fresnel.fourPolar.io.imagingSetup;

import java.io.IOException;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fr.fresnel.fourPolar.core.imagingSetup.IFourPolarImagingSetup;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;
import fr.fresnel.fourPolar.io.imagingSetup.imageFormation.fov.IFieldOfViewJSONAdaptor;
import fr.fresnel.fourPolar.io.physics.channel.IChannelJSONAdaptor;
import fr.fresnel.fourPolar.io.physics.na.INumericalApertureJSONAdaptor;

/**
 * This class is used for writing the fourPolar imaging setup to disk.
 */
@JsonPropertyOrder({ "Number Of Cameras", "Field Of View", "Numerical Aperture", "Channels" })
class FourPolarImagingSetupJSONAdaptor {
    @JsonProperty("Field Of View")
    private IFieldOfViewJSONAdaptor _fovAdaptor;

    @JsonProperty("Numerical Aperture")
    private INumericalApertureJSONAdaptor _naAdaptor;

    @JsonProperty("Channels")
    private TreeMap<String, IChannelJSONAdaptor> _channelAdaptor;

    @JsonProperty("Number Of Cameras")
    private Cameras _cameras;

    public void toYaml(IFourPolarImagingSetup imagingSetup) {
        _setFieldOfViewAdaptor(imagingSetup);
        _setNumericalApertureAdaptor(imagingSetup);
        _setChannels(imagingSetup);
        _setNCameras(imagingSetup);
    }

    public void fromYaml(IFourPolarImagingSetup imagingSetup) throws IOException {
        imagingSetup.setFieldOfView(this._fovAdaptor.fromJSON());
        imagingSetup.setNumericalAperture(this._naAdaptor.fromJSON());
        imagingSetup.setCameras(this._cameras);

        for (int channel = 1; channel <= this._channelAdaptor.size(); channel++) {
            imagingSetup.setChannel(channel, this._channelAdaptor.get("Channel " + channel).fromJSON());
        }
    }

    private void _setFieldOfViewAdaptor(IFourPolarImagingSetup imagingSetup) {
        _fovAdaptor = new IFieldOfViewJSONAdaptor();
        _fovAdaptor.toJSON(imagingSetup.getFieldOfView());
    }

    private void _setNumericalApertureAdaptor(IFourPolarImagingSetup imagingSetup) {
        _naAdaptor = new INumericalApertureJSONAdaptor();
        _naAdaptor.toJSON(imagingSetup.getNumericalAperture());
    }

    private void _setChannels(IFourPolarImagingSetup imagingSetup) {
        int nchannel = imagingSetup.getNumChannel();
        this._channelAdaptor = new TreeMap<String, IChannelJSONAdaptor>();

        for (int channel = 1; channel <= nchannel; channel++) {
            IChannelJSONAdaptor adaptor = new IChannelJSONAdaptor();
            adaptor.toJSON(imagingSetup.getChannel(channel));

            this._channelAdaptor.put("Channel " + channel, adaptor);
        }
    }

    private void _setNCameras(IFourPolarImagingSetup imagingSetup) {
        _cameras = imagingSetup.getCameras();
    }
}