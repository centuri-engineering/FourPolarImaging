package fr.fresnel.fourPolar.core.imageSet.acquisition.sample.finders.namePattern;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FilterPolarizationFileTest {

    @Test    
    public void list_FilterPol90_ReturnsImg1_C1_Pol90() {
        // Start with the Pol0 file
        File root = new File("fourPolar-core/src/test/java/fr/fresnel/fourPolar/core/imageSet/acquisition/sample/finders/namePattern/TestFiles/FourCamera/");
        File pol0File = new File(root, "Img1_C1_Pol0.tif");

        FilterPolarizationFile filter = new FilterPolarizationFile(pol0File, "Pol0", "Pol90");
        
        assertTrue(root.list(filter)[0].equals("Img1_C1_Pol90.tif"));


    }
}