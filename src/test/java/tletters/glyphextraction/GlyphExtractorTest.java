package tletters.glyphextraction;

import org.junit.Before;
import org.junit.Test;
import tletters.imagegeneration.ImageGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GlyphExtractorTest {

    private static final String TEST_FILE_PATH = "src/test/resources/images/lorem_ipsum.png";

    private GlyphExtractor glyphExtracter;
    private BufferedImage bufferedImage;

    @Before
    public void setupGlyphExtractor() {
        glyphExtracter = new GlyphExtractor();
        bufferedImage = null;
    }

    @Test
    public void testCutLineWithText() throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException, IOException {
        Method testedMethod = GlyphExtractor.class.getDeclaredMethod("cutLineWithText");
        testedMethod.setAccessible(true);

        Field linesField = GlyphExtractor.class.getDeclaredField("lines");
        linesField.setAccessible(true);

        bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, Color.WHITE.getRGB());
        glyphExtracter.setImage(bufferedImage);
        testedMethod.invoke(glyphExtracter);
        assertEquals(0, ((List) linesField.get(glyphExtracter)).size());

        glyphExtracter.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "test", 0)
                        .getGeneratedImage()
        );
        testedMethod.invoke(glyphExtracter);
        assertEquals(2, ((List) linesField.get(glyphExtracter)).size());

        glyphExtracter.setImage(ImageIO.read(new File(TEST_FILE_PATH)));
        testedMethod.invoke(glyphExtracter);
        assertEquals(22, ((List) linesField.get(glyphExtracter)).size());
    }

    @Test
    public void testCutLettersWithLines() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method testedMethod = GlyphExtractor.class.getDeclaredMethod("cutLettersWithLines");
        testedMethod.setAccessible(true);

        Method cutLineWithText = GlyphExtractor.class.getDeclaredMethod("cutLineWithText");
        cutLineWithText.setAccessible(true);

        Field lettersField = GlyphExtractor.class.getDeclaredField("letters");
        lettersField.setAccessible(true);

        bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, Color.WHITE.getRGB());
        glyphExtracter.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtracter);
        testedMethod.invoke(glyphExtracter);
        assertEquals(0, ((List) lettersField.get(glyphExtracter)).size());

        glyphExtracter.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "test test test", 0)
                        .getGeneratedImage()
        );
        cutLineWithText.invoke(glyphExtracter);
        testedMethod.invoke(glyphExtracter);
        assertEquals(14, ((List) lettersField.get(glyphExtracter)).size());

        glyphExtracter.setImage(ImageIO.read(new File(TEST_FILE_PATH)));
        cutLineWithText.invoke(glyphExtracter);
        testedMethod.invoke(glyphExtracter);
        assertEquals(9 + (150 + 56 + 81) * 3, ((List) lettersField.get(glyphExtracter)).size());
    }
}
