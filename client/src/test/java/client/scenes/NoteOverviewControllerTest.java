package client.scenes;
import client.markdown.MarkdownHandler;
import client.utils.ServerUtils2;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;
public class NoteOverviewControllerTest {

    private NoteOverviewCtrl controller;

    /**
     * Initializes the JavaFX Toolkit for testing.
     * <p>
     * This method is executed once before all tests to ensure the JavaFX Platform is ready.
     *
     * @throws Exception if an error occurs while initializing the toolkit.
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    /**
     * Sets up the test environment before each test method.
     * <p>
     * Initializes the {@code NoteOverviewCtrl} instance and its associated JavaFX components.
     */
    @BeforeEach
    void setUp() {
        controller = new NoteOverviewCtrl(new ServerUtils2(), new MarkdownHandler(), new MainCtrl());
        controller.setDeleteButton(new Button());
        controller.setAddButton(new Button());
        controller.setSearchButton(new Button());
        controller.setSearchBar(new TextField());
    }

    /**
     * Tests that the {@code setLocale} method correctly updates the UI components
     * with English translations.
     */
    @Test
    void testSetLocaleUpdatesUI() {
        Platform.runLater(() -> {
            controller.setLocale(java.util.Locale.ENGLISH);
            assertEquals("Delete", controller.getDeleteButton().getText());
            assertEquals("Add", controller.getAddButton().getText());
            assertEquals("Search", controller.getSearchButton().getText());
            assertEquals("Search...", controller.getSearchBar().getPromptText());
        });
    }

    /**
     * Tests that the {@code switchToDutch} method correctly updates the UI components
     * with Dutch translations.
     */
    @Test
    void testSwitchToDutchUpdatesUI() {
        Platform.runLater(() -> {
            controller.switchToDutch();
            assertEquals("Verwijderen", controller.getDeleteButton().getText());
            assertEquals("Toevoegen", controller.getAddButton().getText());
            assertEquals("Zoeken", controller.getSearchButton().getText());
            assertEquals("Zoeken...", controller.getSearchBar().getPromptText());
        });
    }

    /**
     * Tests that the {@code switchToSpanish} method correctly updates the UI components
     * with Spanish translations.
     */
    @Test
    void testSwitchToSpanishUpdatesUI() {
        Platform.runLater(() -> {
            controller.switchToSpanish();
            assertEquals("Eliminar", controller.getDeleteButton().getText());
            assertEquals("Añadir", controller.getAddButton().getText());
            assertEquals("Buscar", controller.getSearchButton().getText());
            assertEquals("Buscar...", controller.getSearchBar().getPromptText());
        });
    }

    /**
     * Tests that the initial locale is correctly loaded from the configuration file.
     * <p>
     * Verifies that the default locale is English when no saved configuration exists.
     */
    @Test
    void testInitialLocaleIsLoaded() {
        Platform.runLater(() -> {
            Locale initialLocale = controller.loadSavedLocale();
            assertNotNull(initialLocale, "Initial locale should not be null");
            assertEquals("en", initialLocale.getLanguage(), "Default locale should be English");
        });
    }

}
