package fr.fresnel.fourPolar.core.imagingSetup.imageFormation.fov;

import java.util.Hashtable;

import fr.fresnel.fourPolar.core.physics.polarization.Polarizations;

/**
 * Models the field of view of the bead images of a particular channel.
 */
class FieldOfView implements IFieldOfView {
    Hashtable<Polarizations, Rectangle> fov = new Hashtable<Polarizations, Rectangle>(4);

    public FieldOfView(Rectangle pol0, Rectangle pol45, Rectangle pol90, Rectangle pol135){
        fov.put(Polarizations.pol0, pol0);
        fov.put(Polarizations.pol45, pol45);
        fov.put(Polarizations.pol90, pol90);
        fov.put(Polarizations.pol135, pol135);
    }

    @Override
    public Rectangle getFoV(Polarizations pol) {
        return fov.get(pol);
    }
    
}