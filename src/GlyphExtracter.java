import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
 
public class GlyphExtracter {
    public ArrayList<BufferedImage> lettersAfterCrop;
 
    private BufferedImage image;
    private ArrayList<BufferedImage> letters;
    private ArrayList<BufferedImage> line;
 
    public GlyphExtracter(BufferedImage image) throws IOException {
        this.image = image;
        scalpel();
    }
 
    public ArrayList<BufferedImage> scalpel() throws IOException {
        cutLineWithText();
        cutLettersWithLines();
        trim();
        return lettersAfterCrop;
    }
 
    private void cutLineWithText() throws IOException{
        line = new ArrayList<>();
        int point = 0;
 
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
 
                if (existBlackPixel(image, x, y)) {
                    break;
                }
                else if(image.getWidth()-1==x && y!=0) {
                    if(y-point>1)
                        line.add(image.getSubimage(0, point, image.getWidth(), y - point+1));
 
                    point = y;
                }
            }
        }
    }
 
    private void cutLettersWithLines() throws IOException{
        letters = new ArrayList<>();
 
        for (int i = 0; i < line.size(); i++) {
            int point = 0;
            for (int x = 0; x < line.get(i).getWidth(); x++) {
                for (int y = 0; y < line.get(i).getHeight(); y++) {
 
                    if (existBlackPixel(line.get(i), x, y)) {
                        break;
                    } else if (x != 0 && line.get(i).getHeight() - 1 == y) {
                        if (x - point > 1)
                            letters.add(line.get(i).getSubimage(point+1, 0, x - point-1, line.get(i).getHeight()));
 
                        point = x;
                    }
                }
            }
        }
    }
 
    private void trim() throws IOException{
        lettersAfterCrop = new ArrayList<>();
 
        int up;
        int down;
 
        for (int i = 0; i < letters.size(); i++) {
            up = 0;
            down = 0;
 
            for (int y = 0; y < letters.get(i).getHeight(); y++) {
                for (int x = 0; x < letters.get(i).getWidth(); x++) {
                    if (existBlackPixel(letters.get(i), x, y)) {
                        down = y;
                    }
                }
            }
 
            for (int y = letters.get(i).getHeight()-1; y >=0; y--) {
                for (int x = 0; x < letters.get(i).getWidth(); x++) {
                    if (existBlackPixel(letters.get(i), x, y)) {
                        up = y;
                    }
                }
            }
            lettersAfterCrop.add(letters.get(i).getSubimage(0, up , letters.get(i).getWidth(), down - up));
        }
    }
 
    private boolean existBlackPixel(BufferedImage image, int x, int y) {
        //If pixel have a black color
        if (image.getRGB(x, y) <= -16350000 && image.getRGB(x, y) > -17000000)
            return true;
        else
            return false;
    }
}