import tletters.featureextraction.Zoning;
import tletters.glyph.Glyph;
import tletters.glyph.LanguageType;
import tletters.glyphextracter.GlyphExtracter;
import tletters.imagegeneration.ImageGenerator;
import tletters.imagescaler.ImageScaler;
import tletters.knnclassifier.Classifier;
import tletters.knnclassifier.DistanceMeter;
import tletters.knnclassifier.EuclideanDistanceMeter;
import tletters.knnclassifier.KNNClassifier;
import tletters.test.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariuszbeltowski on 23/04/16.
 */

public class Main {
    public static void main(String[] args) {
        System.out.println(Character.getNumericValue('L'));
        ImageGenerator imageGenerator = new ImageGenerator();
        String text = "abcdefghijklmnoprstuwyzABCDEFGHIJKLMNOPRSTUWYZ";
        Font font = new Font("Time New Roman", Font.PLAIN, 16);
        imageGenerator.generateImage(font, 48, text, 0);
        GlyphExtracter glyphExtracter = new GlyphExtracter();
        glyphExtracter.setImage(imageGenerator.getGeneratedImage());
        List<BufferedImage> lettersList = glyphExtracter.scalpel();
        System.out.println(lettersList.size());
        ImageScaler imageScaler = new ImageScaler();
        List<BufferedImage> scaledLettersList = new ArrayList<>();
        int i = 0;
        for(BufferedImage image : lettersList) {
            BufferedImage scaledImage = imageScaler.scalImage(image);
            scaledLettersList.add(scaledImage);
        }
        Zoning zoning = new Zoning();
        List<double[]> vectorsList = new ArrayList<>();
        List<Glyph> glyphList = new ArrayList<Glyph>();
        for(BufferedImage image : scaledLettersList) {
            vectorsList.add(zoning.extractFeatures(image));
            Character character = text.charAt(i);
            glyphList.add(new Glyph(zoning.extractFeatures(image),
                    LanguageType.GENERAL_PL,
                    character));
            i++;
        }
        System.out.println(glyphList.get(34).getCharacter() + "\n" + Arrays.toString(glyphList.get(34).getFeatureVector()));
        Test test = new Test(new KNNClassifier<Double>(2), new EuclideanDistanceMeter<Double>(), glyphList);
        String lorem = "Lorem Ipsum";
        imageGenerator.generateImage(font, 48, lorem, 0);
        glyphExtracter.setImage(imageGenerator.getGeneratedImage());
        List<BufferedImage> loremList = glyphExtracter.scalpel();
        List<BufferedImage> scaledLorem = new ArrayList<>();
        for(BufferedImage image : loremList) {
            BufferedImage scaledImage = imageScaler.scalImage(image);
            scaledLorem.add(scaledImage);
        }
        List<double[]> loremVectors = new ArrayList<>();
        for(BufferedImage image : scaledLorem) {
            loremVectors.add(zoning.extractFeatures(image));
        }
        System.out.println(Arrays.toString(loremVectors.get(0)));
        for(double[] v : loremVectors) {
            System.out.print(test.classify(v));
        }
    }
}
