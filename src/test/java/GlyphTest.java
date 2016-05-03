import glyph.Glyph;
import glyph.LanguageType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GlyphTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testWillThrowExceptionWhenGlyphCaseDoesNotMatchCharacterCase() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Character case and glyphCase do not match");
        new Glyph(new double[0], Glyph.GlyphCase.UPPER, Glyph.GlyphType.LETTER, LanguageType.GENERAL_PL, 'c');
    }

    @Test
    public void testWillThrowExceptionWhenGlyphTypeDoesNotMatchCharacterType() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Character type and glyphType do not match");
        new Glyph(new double[0], Glyph.GlyphCase.UPPER, Glyph.GlyphType.LETTER, LanguageType.GENERAL_PL, '1');
    }

    @Test
    public void testWillThrowExceptionWhenGlyphTypeIsDigitAndGlyphCaseIsLower() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Character case and glyphCase do not match");
        new Glyph(new double[0], Glyph.GlyphCase.LOWER, Glyph.GlyphType.DIGIT, LanguageType.GENERAL_PL, '1');
    }

    @Test
    public void testCanBeConstructedWithoutExceptionsFoValidTypeCaseCombination() {
        new Glyph(new double[0], Glyph.GlyphCase.UPPER, Glyph.GlyphType.DIGIT, LanguageType.GENERAL_PL, '7');
        new Glyph(new double[0], Glyph.GlyphCase.LOWER, Glyph.GlyphType.LETTER, LanguageType.GENERAL_PL, 'a');
        new Glyph(new double[0], Glyph.GlyphCase.UPPER, Glyph.GlyphType.LETTER, LanguageType.GENERAL_PL, 'A');
    }
}
