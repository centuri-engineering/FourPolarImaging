package fr.fresnel.fourPolar.io.physics.propagation;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import fr.fresnel.fourPolar.core.exceptions.fourPolar.propagationdb.PropagationChannelNotInDatabase;
import fr.fresnel.fourPolar.core.fourPolar.propagationdb.IOpticalPropagationDB;
import fr.fresnel.fourPolar.core.imagingSetup.IFourPolarImagingSetup;
import fr.fresnel.fourPolar.core.physics.propagation.IOpticalPropagation;
import fr.fresnel.fourPolar.io.PathFactoryOfProject;

/**
 * Using this class, we write the {@link IOpticalPropagation} of each channel to
 * the project folder for the user as Yaml.
 */
public class OpticalPropagationToYaml {

    public void write(File root4PProject, IFourPolarImagingSetup setup, IOpticalPropagationDB database)
            throws PropagationChannelNotInDatabase, IOException {
        ObjectMapper mapper = getYamlMapper();
        for (int channel = 1; channel <= setup.getNumChannel(); channel++) {
            IOpticalPropagationJSONAdaptor adaptor = getJSONAdaptor(setup, database, channel);

            File path = getFilePath(root4PProject, channel);

            mapper.writeValue(path, adaptor);
        }

    }

    /**
     * Set the yaml mapper.
     * @return
     */
    private ObjectMapper getYamlMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

    /**
     * Returns the JSON adaptor for the given channel of the setup.
     * 
     * @param setup
     * @param database
     * @throws PropagationChannelNotInDatabase
     */
    private IOpticalPropagationJSONAdaptor getJSONAdaptor(IFourPolarImagingSetup setup, IOpticalPropagationDB database,
            int channel) throws PropagationChannelNotInDatabase {
        IOpticalPropagation optProp = database.search(setup.getChannel(channel), setup.getNumericalAperture());

        IOpticalPropagationJSONAdaptor jsonAdaptor = new IOpticalPropagationJSONAdaptor();
        jsonAdaptor.toJSON(optProp);

        return jsonAdaptor;

    }

    private String _getFileName(int channel) {
        return "OpticalPropagation_Channel" + channel + ".yaml";
    }

    private File getFilePath(File root4PProject, int channel) {
        return new File(PathFactoryOfProject.getFolder_Params(root4PProject),
                _getFileName(channel));

    }



}