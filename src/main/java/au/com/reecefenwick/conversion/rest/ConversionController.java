package au.com.reecefenwick.conversion.rest;

import au.com.reecefenwick.conversion.service.DocConversionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
@RestController
public class ConversionController {

    @Autowired
    private DocConversionService docConversionService;

    /**
     * Example to process each PDF page separately
     * @param fileToConvert
     * @throws IOException
     */
    @RequestMapping(value = "/api/test", method = RequestMethod.POST)
    @ApiIgnore
    public void convertFile(MultipartFile fileToConvert) throws IOException {
        PDDocument document = docConversionService.parsePDF(fileToConvert.getInputStream());

        List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();
        int page = 0;

        for (PDPage pdPage : pdPages) {
            ++page;
            BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300);
            ImageIOUtil.writeImage(bim, UUID.randomUUID() + "-" + page + ".tif", 300);
        }

        document.close();
    }

    @RequestMapping(value = "/api/convert", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void convertPdfToTiff(@RequestParam(value = "file", required = true) MultipartFile fileToConvert, HttpServletResponse response)throws IOException {

        PDDocument pddoc = PDDocument.load(fileToConvert.getInputStream());

        response.setContentType("image/tiff");
        response.setHeader("Content-Disposition", "attachment; filename=" + UUID.randomUUID() + ".tiff");

        docConversionService.savePdfAsTiff(pddoc, response.getOutputStream());

        pddoc.close();

        response.flushBuffer();
    }

}
