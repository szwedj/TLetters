import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;


/**
 * Created by mdomagala on 4/25/16.
 */
public class Font {

    static boolean checkFont(String name) throws IOException, FontFormatException {
        java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new FileInputStream("src/main/resources/fonts/" + name));
        char [] specialChars = {'ą', 'Ą', 'ć', 'Ć', 'ę', 'Ę', 'ł', 'Ł', 'ń', 'Ń', 'ó', 'Ó', 'ś', 'Ś', 'ź', 'Ź', 'ż', 'Ż', 'A', 'a','1'};

        for (char c:specialChars) {
            if(!font.canDisplay(c)){
                return false;
            }
        }
        return true;
    }

    static void checkAllFonts() throws IOException, FontFormatException {
        final String newPath = "src/main/resources/fontsWithPolishCharacters/";
        final String startPath = "src/main/resources/fonts/";
        Path destination, source;
        String fileName;

        if(!Files.exists(Paths.get(newPath))){
            Files.createDirectory(Paths.get(newPath));
        }

        final DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(startPath), new DirectoryStream.Filter<Path>(){
            @Override
            public boolean accept(Path entry) throws IOException {
                return entry.getFileName().toString().endsWith(".ttf") || entry.getFileName().toString().endsWith(".otf");
            }
        });

        for (Path path : paths) {
            fileName = path.getFileName().toString();

            if(checkFont(fileName)){
                source = Paths.get(startPath + fileName);
                destination = Paths.get(newPath + fileName);
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}