package tletters.imagescaler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class ImageScaler {

    static final public RenderingHints.Key RenderingHintsKey = RenderingHints.KEY_INTERPOLATION;
    static final public Object RenderingHintsValue = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

    private static BufferedImage foundTop(BufferedImage image, WritableRaster writableRaster, int top, int bottom, int width, int minRight, int minBottom) {
        boolean foundTop = false;
        while (top < bottom && !foundTop) {
            int x = 0;
            while (x < width) {
                if (writableRaster.getSample(x, top, 0) != 0) {
                    minRight = x;
                    minBottom = top;
                    foundTop = true;
                    top--;
                    break;
                }
                x++;
            }
            top++;
        }
        return foundLeft(image, writableRaster, top, bottom, width, minRight, minBottom);
    }

    private static BufferedImage foundLeft(BufferedImage image, WritableRaster writableRaster, int top, int bottom, int width, int minRight, int minBottom) {
        boolean foundLeft = false;
        int left = 0;
        int height = writableRaster.getHeight();
        while (left < minRight && !foundLeft) {
            int y = height - 1;
            while (y > top) {
                if (writableRaster.getSample(left, y, 0) != 0) {
                    minBottom = y;
                    foundLeft = true;
                    left--;
                    break;
                }
                y--;
            }
            left++;
        }
        return foundBottom(image, writableRaster, top, bottom, width, minRight, minBottom, left, height);
    }

    private static BufferedImage foundBottom(BufferedImage image, WritableRaster writableRaster, int top, int bottom, int width, int minRight, int minBottom, int left, int height) {
        boolean foundBottom = false;
        while (bottom > minBottom && !foundBottom) {
            int x = width - 1;
            while (x >= left) {
                if (writableRaster.getSample(x, bottom, 0) != 0) {
                    minRight = x;
                    foundBottom = true;
                    bottom++;
                    break;
                }
                x--;
            }
            bottom--;
        }
        return foundRight(image, writableRaster, top, bottom, width, minRight, left);
    }

    private static BufferedImage foundRight(BufferedImage image, WritableRaster writableRaster, int top, int bottom, int width, int minRight, int left) {
        boolean foundRight = false;
        int right = width - 1;
        while (right > minRight && !foundRight) {
            int y = bottom;
            while (y >= top) {
                if (writableRaster.getSample(right, y, 0) != 0) {
                    foundRight = true;
                    right++;
                    break;
                }
                y--;
            }
            right--;
        }
        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private static BufferedImage cutImage(BufferedImage image) {
        WritableRaster writableRaster = image.getAlphaRaster();
        int width = writableRaster.getWidth(), height = writableRaster.getHeight();
        int minRight = width - 1;
        int bottom = height - 1, minBottom = height - 1;
        int top = 0;
        return foundTop(image, writableRaster, top, bottom, width, minRight, minBottom);
    }

    public void saveScalImage(BufferedImage image, String unicode){
        char unicodeChar = unicode.charAt(0);
        String hex = String.format("%04x", (int) unicodeChar);
        File outputfile = new File(hex + ".png");

        BufferedImage imageAfterScal = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphicAfterScal = imageAfterScal.createGraphics();
        try {
            graphicAfterScal.setRenderingHint(RenderingHintsKey, RenderingHintsValue);
            graphicAfterScal.drawImage(image, 0, 0, 64, 64, null);
        } finally {
            graphicAfterScal.dispose();
        }
        try {
            ImageIO.write(imageAfterScal, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Error during write file.");
        }
    }

    public void scalImage(String unicode, String fontName) {
        if (unicode.equals("") || fontName.equals("")) {
            throw new IllegalArgumentException("unicode and fontName can not be null");
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
                System.out.println("Wrong font format!.");
            }
        } else {
            font = new Font(fontName, Font.PLAIN, 50);
        }

        graphic.setFont(font);
        graphic.dispose();

        FontMetrics fontMetrics = graphic.getFontMetrics();
        int width = fontMetrics.stringWidth(unicode);
        int height = fontMetrics.getHeight() + 4;
        try {
            imageBeforeScal = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }catch (java.lang.IllegalArgumentException e){
            System.out.println("width and height must be > 0");
        }
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
        if (unicode.equals("") || image == null) {
            throw new IllegalArgumentException("unicode and image can not be null");
        }
        image = cutImage(image);
        saveScalImage(image, unicode);
    }
}
