package uk.gov.hmcts.reform.divorce.emclient;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class EvidenceManagementFileUploadTest extends BaseIntegrationTest {

    private String[] fileName = {"PNGFile.png", "BMPFile.bmp", "PDFFile.pdf", "TIFFile.TIF", "JPEGFile.jpg",
            "PNGFile.png", "BMPFile.bmp", "PDFFile.pdf", "TIFFile.TIF", "JPEGFile.jpg"};
    private String[] fileContentType = {"image/png", "image/bmp", "application/pdf", "image/tiff", "image/jpeg",
            "image/png", "image/bmp", "application/pdf", "image/tiff", "image/jpeg"};

    @Test
    public void verifyPngFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("PNGFile.png", "image/png");
    }

    @Test
    public void verifyJpegFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("JPEGFile.jpg", "image/jpeg");
    }

    @Test
    public void verifyTifFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("TIFFile.TIF", "image/tiff");
    }

    @Test
    @WithTag("test-type:smoke")
    public void verifyPdfFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("PDFFile.pdf", "application/pdf");
    }

    @Test
    public void verifyUploadedFileUrlAndDownLoadedFile() {
        assert extractUploadedFileUrl("PDFFile.pdf", "application/pdf").getStatusCode() == 200;
    }

    @Test
    public void verifyBmpFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("BMPFile.bmp", "image/bmp");
    }

    @Test
    public void verifyInvalidFileTypeUpload() {

        String fileUploadResponseAsaString = invalidFileUpload("InvalidFileType.zip", "application/zip");

        assert !fileUploadResponseAsaString.contains("InvalidFileType.zip");
        assert !fileUploadResponseAsaString.contains("application/zip");
        assert fileUploadResponseAsaString.contains("this service only accepts the following file types ('jpg, jpeg, bmp, tif, tiff, png, pdf");
    }

    @Test
    public void verifyBulkFileUpload() {

        String fileUploadResponseAsaString = bulkFileUpload(fileName, fileContentType);

        assert fileUploadResponseAsaString.contains("PNGFile.png");
        assert fileUploadResponseAsaString.contains("image/png");

        assert fileUploadResponseAsaString.contains("BMPFile.bmp");
        assert fileUploadResponseAsaString.contains("image/bmp");

        assert fileUploadResponseAsaString.contains("TIFFile.TIF");
        assert fileUploadResponseAsaString.contains("image/tiff");

        assert fileUploadResponseAsaString.contains("JPEGFile.jpg");
        assert fileUploadResponseAsaString.contains("image/jpeg");

        assert fileUploadResponseAsaString.contains("PDFFile.pdf");
        assert fileUploadResponseAsaString.contains("application/pdf");
    }

    @Test
    public void verifyElevenMBPdfFileTypeUpload() {
        uploadFileAndVerifyStoredInEvidenceManagement("ScienceData.pdf", "application/pdf");
    }

    @Test
    @WithTag("test-type:smoke")
    public void verifyFileUploadWithS2SToken() {
        uploadFileWithS2STokenAndVerifyStoredInEmStore("PDFFile.pdf", "application/pdf");
    }

    @Test
    public void verifyElevenMBPdfWithS2SToken() {
        uploadFileWithS2STokenAndVerifyStoredInEmStore("ScienceData.pdf", "application/pdf");
    }

    @Test
    public void verifyInvalidFileTypeUploadWithS2SToken() {
        String fileUploadResponseAsaString = invalidFileUploadWithS2SToken("InvalidFileType.zip", "application/zip");

        assert !fileUploadResponseAsaString.contains("InvalidFileType.zip");
        assert !fileUploadResponseAsaString.contains("application/zip");
        assert fileUploadResponseAsaString.contains("\"status\":400");
        assert fileUploadResponseAsaString.contains("this service only accepts the following file types ('jpg, jpeg, "
                + "bmp, tif, tiff, png, pdf");
    }
}