package au.com.reecefenwick.conversion.rest;

import au.com.reecefenwick.conversion.Application;
import au.com.reecefenwick.conversion.service.DocConversionService;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConversionControllerTest {

    @Autowired
    DocConversionService docConversionService;

    private MockMvc restConversionMockMvc;

    @Before
    public void setup() throws Exception {
        ConversionController conversionController = new ConversionController();
        ReflectionTestUtils.setField(conversionController, "docConversionService", docConversionService);
        this.restConversionMockMvc = MockMvcBuilders.standaloneSetup(conversionController).build();
    }

    private File getTestPDF(boolean needsMultiPage) throws IOException {
        String filename = needsMultiPage ? "data/multiPagePDF.pdf" : "data/testPDF.pdf";

        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(filename).getFile());
    }

    @Test
    public void testFileUpload() throws Exception {
        // TODO - test more file scenarios, e.g. no filename, contentType etc
        MockMultipartFile mockFile = new MockMultipartFile("file", Files.toByteArray(getTestPDF(false)));

        restConversionMockMvc
                .perform(fileUpload("/api/test")
                        .file("file", Files.toByteArray(getTestPDF(false)))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/tiff"));
    }

}
