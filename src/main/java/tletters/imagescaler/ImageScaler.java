package tletters.imagescaler;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
        RENDERING_PROPERTIES.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_PROPERTIES.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private boolean checkHorizontalLine(BufferedImage image, int line, int width) {
        for (int i = 0; i < width; i++) {
            if (image.getRGB(i, line) <= -16350000 && image.getRGB(i, line) > -17000000) {
                return true;
            }
        }
        return false;
    }

    private boolean checkVerticalLine(BufferedImage image, int line, int height) {
        for (int i = 0; i < height; i++) {
            if (image.getRGB(line, i) <= -16350000 && image.getRGB(line, i) > -17000000) {
                return true;
            }
        }
        return false;
    }

    private BufferedImage cutImage(BufferedImage image) {
        int width = image.getWidth(), height = image.getHeight();
        int left = 0, top = 0;
        int right = width - 1;
        int bottom = height - 1;
        while (!checkHorizontalLine(image, top, width)) {
            top++;
        }
        while (!checkVerticalLine(image, left, height)) {
            left++;
        }
        while (!checkHorizontalLine(image, bottom, width)) {
            bottom--;
        }
        while (!checkVerticalLine(image, right, height)) {
            right--;
        }
        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private void saveScalImage(BufferedImage image, String unicode) {
        char unicodeChar = unicode.charAt(0);
        String hex = String.format("%04x", (int) unicodeChar);
        File outputfile = new File(hex + ".png");
        BufferedImage imageAfterScal = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
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
        BufferedImage imageBeforeScal = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
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
        imageBeforeScal = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphic = imageBeforeScal.createGraphics();
        graphic.setRenderingHints(RENDERING_PROPERTIES);
        graphic.setColor(Color.BLACK);
        graphic.setFont(font);
        int x = 0;
        int y = fontMetrics.getAscent() + 2;
        graphic.setBackground(Color.WHITE);
        graphic.clearRect(0, 0, width, height);
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

    public BufferedImage scalImage(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image can not be null!");
        }
        image = cutImage(image);
        return image;
    }

}
