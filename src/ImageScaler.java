import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class ImageScaler {
    private static BufferedImage cutImage(BufferedImage image) {
        WritableRaster writableRaster = image.getAlphaRaster();
        int width = writableRaster.getWidth(), height = writableRaster.getHeight();
        int left = 0, top = 0;
        int right = width - 1, minRight = width - 1;
        int bottom = height - 1, minBottom = height - 1;

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
        boolean foundLeft = false;
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

        boolean foundRight = false;
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

    public void scalImage(String unicode, String fontName) throws InvalidValueException {
        if (unicode.equals("")) {
            throw new InvalidValueException("Unicode can not be empty string");
        } else if (fontName.equals("")) {
            throw new InvalidValueException("Font can not be empty string");
        }
        BufferedImage imageBeforeScal = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphic = imageBeforeScal.createGraphics();
        Font font = null;
        String regex = "([A-Z|a-z]:\\\\[^*|\"<>?\\n]*)|(\\\\\\\\.*?\\\\.*)";
        if (Pattern.matches(regex, fontName)) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(fontName)).deriveFont(50f);
                GraphicsEnvironment graphicsEnviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                graphicsEnviroment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontName)));
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
        int height = fontMetrics.getHeight();

        imageBeforeScal = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        graphic = imageBeforeScal.createGraphics();
        graphic.setColor(Color.black);
        graphic.setFont(font);
        FontMetrics fontMetricScaleImage = graphic.getFontMetrics();
        int x = 0;
        int y = fontMetricScaleImage.getAscent();
        graphic.drawString(unicode, x, y);
        graphic.dispose();

        char unicodeChar = unicode.charAt(0);
        String hex = String.format("%04x", (int) unicodeChar);
        File outputfile = new File(hex + ".png");

        BufferedImage imageAfterScal = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphicAfterScal = imageAfterScal.createGraphics();
        try {
            graphicAfterScal.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            imageBeforeScal = cutImage(imageBeforeScal);
            graphicAfterScal.drawImage(imageBeforeScal, 0, 0, 64, 64, null);
        } finally {
            graphicAfterScal.dispose();
        }
        try {
            ImageIO.write(imageAfterScal, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scalImage(String unicode, String fontName, BufferedImage image) throws InvalidValueException {
        if (unicode.equals("")) {
            throw new InvalidValueException("Unicode can not be empty string");
        } else if (fontName.equals("")) {
            throw new InvalidValueException("Font can not be empty string");
        } else if (image == null) {
            throw new InvalidValueException("Image can not be null");
        }
        Graphics2D graphic = image.createGraphics();
        Font font = new Font(fontName, Font.PLAIN, 50);

        graphic.setColor(Color.black);
        graphic.setFont(font);

        char unicodeChar = unicode.charAt(0);
        String hex = String.format("%04x", (int) unicodeChar);
        File outputfile = new File(hex + ".png");

        image = cutImage(image);
        BufferedImage imageAfterScal = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphicAfterScal = imageAfterScal.createGraphics();
        try {
            graphicAfterScal.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphicAfterScal.drawImage(image, 0, 0, 64, 64, null);
        } finally {
            graphicAfterScal.dispose();
        }
        try {
            ImageIO.write(imageAfterScal, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}