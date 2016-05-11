package featureextraction;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

public class FeaturesExtractor {
    private static final String defaultDir = "defaultProperties";
    private Properties properties;
    private String directory;


    public FeaturesExtractor() throws IOException {
        properties = new Properties();
        directory = defaultDir;
        try (FileInputStream in = new FileInputStream(directory)) {
            properties.load(in);
        } catch (FileNotFoundException e) {
            new File(directory);
            FileInputStream in = new FileInputStream(directory);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FeaturesExtractor(String directory) throws IOException {
        this.directory = directory;
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(directory)) {
            properties.load(in);
        } catch (FileNotFoundException e) {
            new File(directory);
            FileInputStream in = new FileInputStream(directory);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractFeatures(ExtractionAlgorithm algorithm, BufferedImage image, String name) throws IOException {
        Double[] features = algorithm.extractFeatures(image);
        saveFeatures(name, features);
    }

    public void saveFeatures(String name , Double[] features) throws IOException {
        properties.put(name, features);
        FileOutputStream out = new FileOutputStream(directory);
        properties.store(out, null);
        out.close();
    }
}
