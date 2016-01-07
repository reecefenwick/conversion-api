package au.com.reecefenwick.conversion.service;

import com.icafe4j.image.ImageColorType;
import com.icafe4j.image.ImageParam;
import com.icafe4j.image.options.TIFFOptions;
import com.icafe4j.image.quant.DitherMatrix;
import com.icafe4j.image.quant.DitherMethod;
import com.icafe4j.image.tiff.TIFFTweaker;
import com.icafe4j.image.tiff.TiffFieldEnum;
import com.icafe4j.io.MemoryCacheRandomAccessOutputStream;
import com.icafe4j.io.RandomAccessOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class DocConversionService {

    public PDDocument parsePDF(InputStream inputStream) throws IOException {
        return PDDocument.loadNonSeq(inputStream, null);
    }

    /**
     *
     * @param pdf - PDDocument
     * @param outputStream
     * @return Filename on disk
     * @throws IOException
     */
    public void savePdfAsTiff(PDDocument pdf, ServletOutputStream outputStream) throws IOException {
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
                throw e;
            }
        }

        RandomAccessOutputStream rout = new MemoryCacheRandomAccessOutputStream(outputStream);

        ImageParam.ImageParamBuilder builder = ImageParam.getBuilder();
        ImageParam[] param = new ImageParam[1];
        TIFFOptions tiffOptions = new TIFFOptions();
        tiffOptions.setTiffCompression(TiffFieldEnum.Compression.CCITTFAX4);
        builder.imageOptions(tiffOptions);
        builder.colorType(ImageColorType.FULL_COLOR).ditherMatrix(DitherMatrix.getBayer8x8Diag()).applyDither(true).ditherMethod(DitherMethod.BAYER);
        param[0] = builder.build();

        TIFFTweaker.writeMultipageTIFF(rout, param, images);

        rout.close();
    }

}
