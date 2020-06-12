package fr.fresnel.fourPolar.algorithm.visualization.figures.polarization;

import java.util.HashMap;

import fr.fresnel.fourPolar.algorithm.util.image.color.GrayScaleToColorConverter;
import fr.fresnel.fourPolar.algorithm.util.image.color.GrayScaleToColorConverter.Color;
import fr.fresnel.fourPolar.core.image.generic.IMetadata;
import fr.fresnel.fourPolar.core.image.generic.IPixelCursor;
import fr.fresnel.fourPolar.core.image.generic.IPixelRandomAccess;
import fr.fresnel.fourPolar.core.image.generic.Image;
import fr.fresnel.fourPolar.core.image.generic.metadata.Metadata;
import fr.fresnel.fourPolar.core.image.generic.pixel.IPixel;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.PixelTypes;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.RGB16;
import fr.fresnel.fourPolar.core.image.generic.pixel.types.UINT16;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImage;
import fr.fresnel.fourPolar.core.image.polarization.IPolarizationImageSet;
import fr.fresnel.fourPolar.core.physics.polarization.Polarization;
import fr.fresnel.fourPolar.core.preprocess.registration.RegistrationRule;
import fr.fresnel.fourPolar.core.visualization.figures.polarization.IPolarizationImageSetComposites;
import fr.fresnel.fourPolar.core.visualization.figures.polarization.PolarizationImageSetCompositesBuilder;

/**
 * Using this class, we can create a composite figure
 * {@see IRegistrationCompositeFigures} for a given
 * {@link IPolarizationImageSet} that is not realigned. To avoid any unambiguity
 * that the set should not have been realigned, we demand the realigner.
 */
public class PolarizationImageSetCompositesCreator implements IPolarizationImageSetCompositesCreater {
    private final Color _baseImageColor;
    private final Color _registeredImageColor;
    private final PolarizationImageSetCompositesBuilder _builder;

    /**
     * Define compisition image creator by assigning colors to base and registered
     * images.
     * 
     * @param numChannels          is the total number of channels.
     * @param baseImageColor       is the color to be used for the base image of
     *                             every rule.
     * @param registeredImageColor is the color to be used for the registered image
     *                             of every rule.
     */
    public PolarizationImageSetCompositesCreator(int numChannels, Color baseImageColor, Color registeredImageColor) {
        this._baseImageColor = baseImageColor;
        this._registeredImageColor = registeredImageColor;
        this._builder = new PolarizationImageSetCompositesBuilder(numChannels);
    }

    @Override
    public IPolarizationImageSetComposites create(IPolarizationImageSet polImageSet) {
        HashMap<RegistrationRule, Image<RGB16>> compositeImages = this._createCompositesOfRules(polImageSet);
        return this._buildPolarizationImageSetComposites(compositeImages, polImageSet);
    }

    private HashMap<RegistrationRule, Image<RGB16>> _createCompositesOfRules(IPolarizationImageSet polImageSet) {
        HashMap<RegistrationRule, Image<RGB16>> compositeImages = new HashMap<>();

        for (RegistrationRule rule : RegistrationRule.values()) {
            Image<RGB16> compositeImage = this.createRuleCompositeImage(rule, polImageSet);
            compositeImages.put(rule, compositeImage);
        }
        return compositeImages;
    }

    private Image<UINT16> _getPolarizationImage(IPolarizationImageSet polImageSet, Polarization polarization) {
        return polImageSet.getPolarizationImage(polarization).getImage();
    }

    private Image<RGB16> createRuleCompositeImage(RegistrationRule rule, IPolarizationImageSet polImageSet) {
        IPixelRandomAccess<RGB16> baseImageMonochromeView = this._getMonochromeViewOfPolarizationImage(polImageSet,
                rule.getBaseImagePolarization(), this._baseImageColor);
        IPixelRandomAccess<RGB16> toRegisterImageMonochromeView = this._getMonochromeViewOfPolarizationImage(
                polImageSet, rule.getToRegisterImagePolarization(), this._registeredImageColor);
        return this._mergeMonochromeViews(baseImageMonochromeView, toRegisterImageMonochromeView,
                polImageSet.getPolarizationImage(rule.getBaseImagePolarization()));
    }

    private IPixelRandomAccess<RGB16> _getMonochromeViewOfPolarizationImage(IPolarizationImageSet polImageSet,
            Polarization pol, Color color) {
        Image<UINT16> baseImage = this._getPolarizationImage(polImageSet, pol);
        return GrayScaleToColorConverter.createMonochromeView(baseImage, color);
    }

    private Image<RGB16> _mergeMonochromeViews(IPixelRandomAccess<RGB16> baseMonochromeView,
            IPixelRandomAccess<RGB16> registeredMonochromeView, IPolarizationImage imageBase) {
        Image<RGB16> compositeImage = this._createImageForRuleCompositeFromBasePolarizationImage(imageBase);

        for (IPixelCursor<RGB16> compositeCursor = compositeImage.getCursor(); compositeCursor.hasNext();) {
            IPixel<RGB16> pixel = compositeCursor.next();
            final long[] position = compositeCursor.localize();

            baseMonochromeView.setPosition(position);
            registeredMonochromeView.setPosition(position);

            pixel.value().set(baseMonochromeView.getPixel().value());
            pixel.value().add(registeredMonochromeView.getPixel().value());

            compositeCursor.setPixel(pixel);
        }

        return compositeImage;
    }

    private Image<RGB16> _createImageForRuleCompositeFromBasePolarizationImage(IPolarizationImage imageBase) {
        IMetadata metadata = new Metadata.MetadataBuilder(imageBase.getImage().getMetadata())
                .bitPerPixel(PixelTypes.RGB_16).build();
        return imageBase.getImage().getFactory().create(metadata, RGB16.zero());
    }

    private IPolarizationImageSetComposites _buildPolarizationImageSetComposites(
            HashMap<RegistrationRule, Image<RGB16>> compositeImages, IPolarizationImageSet polImageSet) {
        this._builder.fileSet(polImageSet.getFileSet()).channel(polImageSet.channel());

        for (RegistrationRule rule : RegistrationRule.values()) {
            this._builder.compositeImage(rule, compositeImages.get(rule));
        }

        return this._builder.build();
    }

}