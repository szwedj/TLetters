package tletters.imagescaler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ImageScaler {

    public static final Map<RenderingHints.Key, Object> RENDERING_PROPERTIES = new HashMap<>();

    static {
        RENDERING_PROPERTIES.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private boolean checkHorizontalLine(Raster raster, int line, int width) {
        for (int i = 0; i < width; i++) {
            if (raster.getSample(i, line, 0) != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkVerticalLine(Raster raster, int line, int height) {
        for (int i = 0; i < height; i++) {
            if (raster.getSample(line, i, 0) != 0) {
                return true;
            }
        }
        return false;
    }

    private BufferedImage cutImage(BufferedImage image) {
        Raster raster = image.getAlphaRaster();
        int width = raster.getWidth(), height = raster.getHeight();
        int left = 0, top = 0;
        int right = width - 1;
        int bottom = height - 1;
        while (!checkHorizontalLine(raster, top, width)) {
            top++;
        }
        while (!checkVerticalLine(raster, left, height)) {
            left++;
        }
        while (!checkHorizontalLine(raster, bottom, width)) {
            bottom--;
        }
        while (!checkVerticalLine(raster, right, height)) {
            right--;
        }
        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private void saveScalImage(BufferedImage image, String unicode) {
        char unicodeChar = unicode.charAt(0);
        String hex = String.format("%04x", (int) unicodeChar);
        File outputfile = new File(hex + ".png");

        BufferedImage imageAfterScal = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphicAfterScal = imageAfterScal.createGraphics();
        try {
            graphicAfterScal.setRenderingHints(RENDERING_PROPERTIES);
            graphicAfterScal.drawImage(image, 0, 0, 64, 64, null);
        } finally {
            graphicAfterScal.dispose();
        }
        try {
            ImageIO.write(imageAfterScal, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(ImageScaler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void scalImage(String unicode, String fontName) {
        if (Objects.equals("", unicode) || Objects.equals("", fontName)) {
            throw new IllegalArgumentException("Unicode and fontName can not be empty or null!");
        }
        BufferedImage imageBeforeScal = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphic = imageBeforeScal.createGraphics();
        Font font = null;
        String regex = "([A-Z|a-z]:\\\\[^*|\"<>?\\n]*)|(\\\\\\\\.*?\\\\.*)";
        File fontFile = new File(fontName);
        if (Pattern.matches(regex, fontName)) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(50f);
                GraphicsEnvironment graphicsEnviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                graphicsEnviroment.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
            } catch (IOException | FontFormatException e) {
                System.out.println("Wrong font format!");
            }
        } else {
            font = new Font(fontName, Font.PLAIN, 50);
        }

        graphic.setFont(font);
        FontMetrics fontMetrics = graphic.getFontMetrics();
        int width = fontMetrics.stringWidth(unicode) + 4;
        int height = fontMetrics.getHeight() + 4;
        graphic.dispose();

        imageBeforeScal = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        graphic = imageBeforeScal.createGraphics();
        graphic.setColor(Color.black);
        graphic.setFont(font);
        FontMetrics fontMetricScaleImage = graphic.getFontMetrics();
        int x = 0;
        int y = fontMetricScaleImage.getAscent() + 2;
        graphic.drawString(unicode, x, y);
        graphic.dispose();
        imageBeforeScal = cutImage(imageBeforeScal);
        saveScalImage(imageBeforeScal, unicode);

    }

    public void scalImage(String unicode, BufferedImage image) {
        if (Objects.equals("", unicode) || image == null) {
            throw new IllegalArgumentException("Unicode and image can not be empty or null!");
        }
        image = cutImage(image);
        saveScalImage(image, unicode);
    }
}
