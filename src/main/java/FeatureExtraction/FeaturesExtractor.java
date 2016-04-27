package FeatureExtraction;

import java.awt.image.BufferedImage;
import java.util.Properties;

public class FeaturesExtractor {
    private Properties properties;

    public FeaturesExtractor(){
        properties = new Properties();
    }

    public FeaturesExtractor(Properties properties) {
        this.properties = properties;
    }

    public void extractFeatures(ExtractionAlgorithm algorithm, BufferedImage image, String name){
        Double[] features = algorithm.extractFeatures(image);
        saveFeatures(name, features);
    }

    public void saveFeatures(String name , Double[] features){
        properties.put(name, features);
    }
}
