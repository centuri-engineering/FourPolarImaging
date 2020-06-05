package fr.fresnel.fourPolar.io.visualization.figures.gaugeFigure.tiff;

import java.io.File;
import java.nio.file.Paths;

import fr.fresnel.fourPolar.core.PathFactoryOfProject;
import fr.fresnel.fourPolar.core.image.captured.file.ICapturedImageFileSet;
import fr.fresnel.fourPolar.core.physics.channel.ChannelUtils;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.GaugeFigureType;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.IGaugeFigure;
import fr.fresnel.fourPolar.core.visualization.figures.gaugeFigure.guage.AngleGaugeType;

/**
 * A set of utility methods.
 */
class TiffGaugeFigureIOUtil {
    private static String _GAUGE_FIGURE_FOLDER = "GaugeFigures";

    public static File getGaugeImageFile(File root4PProject, String visualizationSession, int channel,
            IGaugeFigure gaugeFigure) {
        File visualizationRoot = PathFactoryOfProject.getFolder_Visualization(root4PProject);
        String setName = gaugeFigure.getFileSet().getSetName();
        String channelAsString = ChannelUtils.channelAsString(channel);
        String gaugeFigureName = _getGaugeFigureName(gaugeFigure.getFigureType(), gaugeFigure.getGaugeType());

        return Paths.get(visualizationRoot.getAbsolutePath(), visualizationSession, _GAUGE_FIGURE_FOLDER, setName,
                channelAsString, gaugeFigureName).toFile();
    }

    private static String _getGaugeFigureName(GaugeFigureType figureType, AngleGaugeType gaugeType) {
        return figureType + "_" + gaugeType + ".tif";
    }
}