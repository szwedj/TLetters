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

    private GlyphExtractor glyphExtractor;
    private BufferedImage bufferedImage;

    @Before
    public void setupGlyphExtractor() {
        glyphExtractor = new GlyphExtractor();
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
        glyphExtractor.setImage(bufferedImage);
        testedMethod.invoke(glyphExtractor);
        assertEquals(0, ((List) linesField.get(glyphExtractor)).size());

        bufferedImage.setRGB(0, 0, Color.BLACK.getRGB());
        glyphExtractor.setImage(bufferedImage);
        testedMethod.invoke(glyphExtractor);
        assertEquals(1, ((List) linesField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(1, 5);
        glyphExtractor.setImage(bufferedImage);
        testedMethod.invoke(glyphExtractor);
        assertEquals(1, ((List) linesField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(5, 1);
        glyphExtractor.setImage(bufferedImage);
        testedMethod.invoke(glyphExtractor);
        assertEquals(5, ((List) linesField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(5, 5);
        glyphExtractor.setImage(bufferedImage);
        testedMethod.invoke(glyphExtractor);
        assertEquals(5, ((List) linesField.get(glyphExtractor)).size());

        glyphExtractor.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "", 0)
                        .getGeneratedImage()
        );
        testedMethod.invoke(glyphExtractor);
        assertEquals(0, ((List) linesField.get(glyphExtractor)).size());

        glyphExtractor.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "test", 0)
                        .getGeneratedImage()
        );
        testedMethod.invoke(glyphExtractor);
        assertEquals(1, ((List) linesField.get(glyphExtractor)).size());

        glyphExtractor.setImage(ImageIO.read(new File(TEST_FILE_PATH)));
        testedMethod.invoke(glyphExtractor);
        assertEquals(20, ((List) linesField.get(glyphExtractor)).size());
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
        glyphExtractor.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(0, ((List) lettersField.get(glyphExtractor)).size());

        bufferedImage.setRGB(0, 0, Color.BLACK.getRGB());
        glyphExtractor.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(1, ((List) lettersField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(1, 5);
        glyphExtractor.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(5, ((List) lettersField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(5, 1);
        glyphExtractor.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(5, ((List) lettersField.get(glyphExtractor)).size());

        bufferedImage = createLinesOfSinglePixelGlyphs(5, 5);
        glyphExtractor.setImage(bufferedImage);
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(25, ((List) lettersField.get(glyphExtractor)).size());

        glyphExtractor.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "", 0)
                        .getGeneratedImage()
        );
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(0, ((List) lettersField.get(glyphExtractor)).size());

        glyphExtractor.setImage(
                new ImageGenerator()
                        .generateImage(new Font("Sans", Font.PLAIN, 14), 14, "test test test", 0)
                        .getGeneratedImage()
        );
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(12, ((List) lettersField.get(glyphExtractor)).size());

        glyphExtractor.setImage(ImageIO.read(new File(TEST_FILE_PATH)));
        cutLineWithText.invoke(glyphExtractor);
        testedMethod.invoke(glyphExtractor);
        assertEquals(9 + (150 + 56 + 81) * 3, ((List) lettersField.get(glyphExtractor)).size());
    }

    private BufferedImage createLinesOfSinglePixelGlyphs(int lines, int glyphsPerLine) {
        int height = lines * 2 - 1;
        int width = glyphsPerLine * 2 - 1;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (i % 2 == 0 && j % 2 == 0) {
                    bufferedImage.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }
        return bufferedImage;
    }
}
