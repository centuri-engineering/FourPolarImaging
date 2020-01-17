package fr.fresnel.fourPolar.io.imageSet.acquisition.sample.finders.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.fresnel.fourPolar.core.imageSet.acquisition.sample.SampleImageSet;
import fr.fresnel.fourPolar.core.imagingSetup.imageFormation.Cameras;
import fr.fresnel.fourPolar.core.imageSet.acquisition.CapturedImageFileSet;
import fr.fresnel.fourPolar.io.exceptions.imageSet.acquisition.sample.finders.excel.ExcelIncorrentRow;
import fr.fresnel.fourPolar.io.exceptions.imageSet.acquisition.sample.finders.excel.MissingExcelTitleRow;
import fr.fresnel.fourPolar.io.exceptions.imageSet.acquisition.sample.finders.excel.TemplateSampleSetExcelNotFound;
import fr.fresnel.fourPolar.io.imageSet.acquisition.sample.finders.excel.TemplateExcelFileGenerator;
import fr.fresnel.fourPolar.core.imageSet.acquisition.ICapturedImageChecker;

/**
 * This class is used for finding the images of the sample set from an excel
 * file, which is dedicated to one channel.
 */
public class SampleImageSetByExcelFileFinder {
    private ICapturedImageChecker imageChecker = null;

    public SampleImageSetByExcelFileFinder(ICapturedImageChecker imageChecker) {
        this.imageChecker = imageChecker;
    }

    /**
     * Read the excel file provided and adds the images to the sample set.
     * <p>
     * The excel file must have the same format as provided by
     * {@link TemplateExcelFileGenerator}
     * 
     * @param sampleImageSet : Sample image set to be filled.
     * @param channel        : Channel number.
     * @param channelFile    : The path to the corresponding excel file.
     * @throws IncorrectSampleSetExcelFormat
     * @throws TemplateSampleSetExcelNotFound
     */
    public void findChannelImages(SampleImageSet sampleImageSet, int channel, File channelFile)
            throws TemplateSampleSetExcelNotFound, MissingExcelTitleRow, ExcelIncorrentRow {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(channelFile))) {
            Sheet sheet = workbook.getSheetAt(0);

            int titleRow = this._findTitleRow(sheet, sampleImageSet.getImagingSetup().getCameras());
            int nImages = Cameras.getNImages(sampleImageSet.getImagingSetup().getCameras());

            for (int rowCtr = titleRow + 1; rowCtr <= sheet.getLastRowNum(); rowCtr++) {
                Row row = sheet.getRow(rowCtr);
                File[] files = createFiles(nImages, row);

                if (this.imagesExistAndCompatible(files)) {
                    CapturedImageFileSet fileSet = this.createFileSet(files);
                    sampleImageSet.addImage(channel, fileSet);
                }
            }
        } catch (IOException e){
            throw new TemplateSampleSetExcelNotFound("The template file does not exist or corrupted.");

        }
    }

    /**
     * Create the files based on the strings in the row.
     * 
     * @param nColumns
     * @param row
     * @return
     * @throws IOException
     */
    private File[] createFiles(int nImages, Row row) throws ExcelIncorrentRow {
        short nColumns = row.getLastCellNum();
        if (nColumns != nImages) {
            int actualRow = row.getRowNum() + 1;
            throw new ExcelIncorrentRow(
                    "The excel file does not have " + nImages + " column(s) at the " + actualRow + "-th row");
        }

        File[] files = new File[nColumns];
        for (int cellCtr = 0; cellCtr < nColumns; cellCtr++) {
            Cell cell = row.getCell(cellCtr);
            files[cellCtr] = new File(cell.getStringCellValue());
        }

        return files;
    }

    /**
     * Check the file exists and is compatible with our format.
     * 
     * @param files
     * @return
     */
    private boolean imagesExistAndCompatible(File[] files) {
        for (File file : files) {
            if (!file.exists() || !imageChecker.checkCompatible(file))
                return false;
        }
        return true;
    }

    private CapturedImageFileSet createFileSet(File[] files) {
        if (files.length == 1)
            return new CapturedImageFileSet(files[0]);
        else if (files.length == 2)
            return new CapturedImageFileSet(files[0], files[1]);
        else
            return new CapturedImageFileSet(files[0], files[1], files[2], files[3]);
    }

    private int _findTitleRow(Sheet sheet, Cameras camera) throws MissingExcelTitleRow{
        String[] titleRow = Cameras.getLabels(camera);
        int result = -1;
        for (int rowNum = 0; rowNum < sheet.getLastRowNum() & result < 0; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null && this._checkColumnAgainsStrings(camera, titleRow, row)) {
                result = rowNum;
            }
        }

        if (result < 0) {
            throw new MissingExcelTitleRow("The title row was not found in the excel file.");
        }

        return result;
    }

    /**
     * Verifies that the given column equals the given string set.
     * 
     * @param camera
     * @param titleRow
     * @param row
     * @return
     */
    private boolean _checkColumnAgainsStrings(Cameras camera, String[] titleRow, Row row) {
        boolean result = true;
        for (int columnNum = 0; columnNum < Cameras.getNImages(camera) && result == true; columnNum++) {
            String cellText = row.getCell(columnNum).getStringCellValue();
            if (!titleRow[columnNum].equals(cellText)) {
                result = false;
            }
        }

        return result;
    }

}