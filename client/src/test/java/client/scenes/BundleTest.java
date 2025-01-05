package client.scenes;

import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class BundleTest {
    @Test
    public void testResourceBundle() {
        URL resourceUrl = getClass().getClassLoader().getResource("bundle.properties");
        if (resourceUrl == null) {
            System.out.println("Resource not found!");
        } else {
            System.out.println("Resource found at: " + resourceUrl);
        }
        Locale locale=Locale.ENGLISH;
        try {
            ResourceBundle rb = ResourceBundle.getBundle("bundle", locale);
            System.out.println("Successfully loaded bundle for locale: " + locale);
        } catch (MissingResourceException e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            e.printStackTrace();
        }
    }

}
