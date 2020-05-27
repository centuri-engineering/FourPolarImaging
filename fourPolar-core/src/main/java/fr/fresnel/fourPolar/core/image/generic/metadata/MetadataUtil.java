package fr.fresnel.fourPolar.core.image.generic.metadata;

import java.util.Objects;

import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.axis.AxisOrder;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelType;
import fr.fresnel.fourPolar.core.util.shape.IPointShape;
import fr.fresnel.fourPolar.core.util.shape.ShapeFactory;

/**
 * A set of utility methods on metadata information.
 */
public class MetadataUtil {
	private MetadataUtil() {
		throw new AssertionError();
	}

	/**
	 * Returns true if image is planer (has at most one z point, and time point, and
	 * channel or a combination of them).
	 */
	public static boolean isImagePlanar(IMetadata metadata) {
		long[] dim = metadata.getDim();

		long nChannels = dim[metadata.axisOrder().t_axis];
		long nZpoints = dim[metadata.axisOrder().z_axis];
		long nTimepoints = dim[metadata.axisOrder().c_axis];

		return nChannels <= 1 && nZpoints <= 1 && nTimepoints <= 1;
	}

	/**
	 * Returns how many xy planes are present in the image.
	 */
	public static int getNPlanes(IMetadata metadata) {
		Objects.requireNonNull(metadata, "metadata cannot be null.");
		long[] dims = metadata.getDim();

		int nPlanes = 1;
		for (int dim = 2; dim < dims.length; dim++) {
			nPlanes *= dims[dim];
		}

		return nPlanes;
	}

	public static IPointShape getPlaneDim(IMetadata metadata) {
		Objects.requireNonNull(metadata, "metadata cannot be null.");

		long[] dims = metadata.getDim();
		return new ShapeFactory().point(new long[] { dims[0], dims[1] }, AxisOrder.XY);
	}

}