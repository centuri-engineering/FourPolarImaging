package fr.fresnel.fourPolar.algorithm.preprocess.registration.descriptorBased;

import fr.fresnel.fourPolar.algorithm.preprocess.registration.IChannelRegistrator;
import fr.fresnel.fourPolar.core.exceptions.image.generic.imgLib2Model.ConverterToImgLib2NotFound;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.imgLib2Model.ImageToImgLib2Converter;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImageSet;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;
import fr.fresnel.fourPolar.core.preprocess.registration.ChannelRegistrationOrder;
import fr.fresnel.fourPolar.core.preprocess.registration.IChannelRegistrationResult;
import ij.ImagePlus;
import net.imglib2.img.display.imagej.ImageJFunctions;
import registration.descriptorBased.headless.HeadLess_Descriptor_based_registration;
import registration.descriptorBased.plugin.DescriptorParameters;
import registration.descriptorBased.result.DescriptorBased2DResult;
import registration.descriptorBased.result.DescriptorBased2DResult.FailureCause;

/**
 * Register the given polarization image set using the descriptor based
 * registration algorithm. To run the algorithm headless, the original library
 * {@linkplain https://github.com/fiji/Descriptor_based_registration} has been
 * refactord in
 * {@linkplain https://github.com/Masoudas/HeadlessDescriptoBasedRegistration},
 * and is compiled as a jar in this project.
 */
public class DescriptorBasedRegistration implements IChannelRegistrator {
    /**
     * Number of tries for registering the given image.
     */
    private static int _ITERATION_MAX = 3;

    private final HeadLess_Descriptor_based_registration _registrator;

    public DescriptorBasedRegistration() {
        this._registrator = new HeadLess_Descriptor_based_registration();
    }

    @Override
    public IChannelRegistrationResult register(IPolarizationImageSet polarizationImageSet) {
        Image<UINT16> pol0 = polarizationImageSet.getPolarizationImage(Polarization.pol0).getImage();
        Image<UINT16> pol45 = polarizationImageSet.getPolarizationImage(Polarization.pol45).getImage();
        Image<UINT16> pol90 = polarizationImageSet.getPolarizationImage(Polarization.pol90).getImage();
        Image<UINT16> pol135 = polarizationImageSet.getPolarizationImage(Polarization.pol135).getImage();

        DescriptorBased2DResult pol45_pol0 = _registerPolarization(pol0, pol45);
        DescriptorBased2DResult pol90_pol0 = _registerPolarization(pol0, pol90);
        DescriptorBased2DResult pol135_pol0 = _registerPolarization(pol0, pol135);

        return new DescriptorBased2DResultConverter().set(ChannelRegistrationOrder.Pol45_to_Pol0, pol45_pol0)
                .set(ChannelRegistrationOrder.Pol90_to_Pol0, pol90_pol0)
                .set(ChannelRegistrationOrder.Pol135_to_Pol0, pol135_pol0).convert();
    }

    private DescriptorBased2DResult _registerPolarization(Image<UINT16> pol0, Image<UINT16> otherPol) {
        DescriptorBased2DResult result = null;

        ImagePlus imageBase = _wrapToImageJ1(pol0);
        ImagePlus imageToRegister = _wrapToImageJ1(pol0);

        DescriptorParameters registParams = new DescriptorParamsBuilder().build();

        boolean registrationSatisfactory = false;
        for (int itr = 1; itr <= _ITERATION_MAX & !registrationSatisfactory; itr++) {
            result = this._registrator.register(imageToRegister, imageBase, registParams);

            if (_isRegistrationSatisfactory(result)) {
                registrationSatisfactory = true;
            } else {
                registrationSatisfactory = false;
                registParams = this._upadteRegistrationParameters(result, registParams, itr);
            }
        }

        return result;

    }

    /**
     * Wraps the given image interface to ImageJ1 interface.
     * 
     * @param image
     * @return
     * @throws ConverterToImgLib2NotFound
     */
    private ImagePlus _wrapToImageJ1(Image<UINT16> image) {
        ImagePlus imagePlus = null;
        try {
            imagePlus = ImageJFunctions.wrap(ImageToImgLib2Converter.getImg(image, UINT16.zero()), "");
        } catch (ConverterToImgLib2NotFound e) {
            // TODO : Get rid of this annoying exception.
        }
        return imagePlus;
    }

    /**
     * If registration is successful and error is less than one pixel, returns true.
     * Otherwise returns false. Note that this method only determines the
     * termination condition of the registration. It is possible that registration
     * is qualitatively successful, even though this condition is never satisfied.
     * 
     * @param result
     */
    private boolean _isRegistrationSatisfactory(DescriptorBased2DResult result) {
        if (result.isSuccessful() && result.error() < 1) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Update the registration parameters based on the params of the previous
     * iteration.
     * 
     * @param previous_itr
     * @return
     */
    private DescriptorParameters _upadteRegistrationParameters(DescriptorBased2DResult result,
            DescriptorParameters previous_itr, int itr) {
        DescriptorParameters dParameters = null;
        if (result.description() == FailureCause.NO_INLIER_AFTER_RANSAC) {
            dParameters = new DescriptorParamsBuilder(previous_itr).numNeighborsRansac(previous_itr.numNeighbors++)
                    .redundancyRansac(previous_itr.redundancy).build();
        } else if (result.description() == FailureCause.NOT_ENOUGH_FP) {
            dParameters = new DescriptorParamsBuilder().dog_sigma(DescriptorParamsBuilder.DOG_SIGMA_CHOICES[itr])
                    .detectionThresholdFP(DescriptorParamsBuilder.THRESHOLD_CHOICES[itr]).build();
        }

        return dParameters;
    }

}