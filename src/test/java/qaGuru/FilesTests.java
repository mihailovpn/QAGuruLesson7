package qaGuru;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Condition;
import com.codeborne.xlstest.XLS;
import org.apache.poi.ss.formula.ptg.ScalarConstantPtg;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesTests {

    @Test
    void DownloadTxtTest() throws Exception {
        open("https://github.com/mihailovpn/QAGuruLesson7/blob/main/README.md");
        File download = $("#raw-url").download();
        String result;

        System.out.println();
        try (InputStream is = new FileInputStream(download)) {
            result = new String(is.readAllBytes(), "UTF-8");
            is.close();
        };
        assertThat(result).contains("QAGuruLesson7");
    }

    @Test
    void downloadPdfTest() throws Exception {
        open("https://docs.pexip.com/admin/download_pdf.htm");
        File download = $("#mc-main-content > table:nth-child(4) > tbody > tr > td:nth-child(1) > a").download();
        PDF parsed = new PDF(download);
        assertThat(parsed.author).contains("Pexip AS");
        assertThat(parsed.title).contains("Pexip");
    }

    @Test
    void downloadXlxTest() throws Exception {
        open("https://fish.gov.ru/otkrytoe-agentstvo/otkrytye-dannye/perechen-podvedomstvennyh-organizaczij/");
        File download = $(byText("/wp-content/uploads/documents/ob_agentstve/opendata/7702679523-perechenpodved/data-13-structure-7.xls")).download();
        XLS parsed = new XLS(download);
        assertThat(parsed.name).contains("data-13-structure-7");
        assertThat(parsed.excel.getSheetAt(0).getRow(1).getCell(2)
                .getStringCellValue()).isEqualTo("ФГБУ «Главрыбвод»");
    }

    @Test
    void parseZipTest() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("Test.zip"){
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
            /* for text files
            Scanner sc = new Scanner(zis);
            while (sc.hasNext()) {
                sc.nextLine();
            }
            */
        }
    }

    @Test
    void uploadTxtTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("example.txt");
        $("input[value = 'Upload'").click();
        $("#uploaded-files").shouldHave(Condition.text("example.txt"));
    }
}
