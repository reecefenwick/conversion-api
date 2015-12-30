package au.com.reecefenwick.conversion.rest;

import com.icafe4j.image.ImageColorType;
import com.icafe4j.image.ImageParam;
import com.icafe4j.image.options.TIFFOptions;
import com.icafe4j.image.quant.DitherMethod;
import com.icafe4j.image.quant.DitherMatrix;
import com.icafe4j.image.tiff.TIFFTweaker;
import com.icafe4j.image.tiff.TiffFieldEnum;
import com.icafe4j.io.FileCacheRandomAccessOutputStream;
import com.icafe4j.io.RandomAccessOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class ConversionController {

    @RequestMapping(value = "/api/convert", method = RequestMethod.POST)
    public void convertFile(MultipartFile fileToConvert) throws IOException {
        PDDocument document = PDDocument.loadNonSeq(fileToConvert.getInputStream(), null);
        List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();
        int page = 0;
        for (PDPage pdPage : pdPages) {
            ++page;
            BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300);
            ImageIOUtil.writeImage(bim, UUID.randomUUID() + "-" + page + ".tif", 300);
        }

        document.close();
    }

    @RequestMapping(value = "/api/test", method = RequestMethod.POST)
    public void test(MultipartFile fileToConvert) throws IOException {
        PDDocument pddoc = PDDocument.load(fileToConvert.getInputStream());

        savePdfAsTiff(pddoc);
    }

    private static void savePdfAsTiff(PDDocument pdf) throws IOException {
        BufferedImage[] images = new BufferedImage[pdf.getNumberOfPages()];
        for (int i = 0; i < images.length; i++) {
            PDPage page = (PDPage) pdf.getDocumentCatalog().getAllPages()
                    .get(i);
            BufferedImage image;
            try {
              image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 288); //works
//                image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 300); // does not work
                images[i] = image;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = new FileOutputStream("a.tiff");
        RandomAccessOutputStream rout = new FileCacheRandomAccessOutputStream(
                fos);
        ImageParam.ImageParamBuilder builder = ImageParam.getBuilder();
        ImageParam[] param = new ImageParam[1];
        TIFFOptions tiffOptions = new TIFFOptions();
        tiffOptions.setTiffCompression(TiffFieldEnum.Compression.CCITTFAX4);
        builder.imageOptions(tiffOptions);
        builder.colorType(ImageColorType.BILEVEL).ditherMatrix(DitherMatrix.getBayer8x8Diag()).applyDither(true).ditherMethod(DitherMethod.BAYER);
        param[0] = builder.build();
        TIFFTweaker.writeMultipageTIFF(rout, param, images);
        rout.close();
        fos.close();
    }

}
