import javafx.scene.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.geometry.*;

import java.util.*;
import java.nio.file.*;
import java.io.File;

public class GuiManager extends Application{

    public static final int MIN_WIDTH = 600;
    public static final int MIN_HEIGHT = 450;

    private SetupController rollCallController;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        Main.L.info(String.format("Setting up window with min width %d and min height %d", MIN_WIDTH, MIN_HEIGHT));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        // Prompt user to load or create new committee
        File saveFile = new File("committee.json");
        boolean loaded = false;
        if (saveFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Load Committee");
            alert.setHeaderText("Load committee from previous session?");
            alert.setContentText("Choose 'OK' to load, or 'Cancel' to start new.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Committee.loadFromJson(saveFile);
                loaded = true;
            }
        }
        if (!loaded) {
            Committee.setName("Not Set");
            Committee.setSpeechLength(60);
            Committee.setIsSC(false);
            // Optionally clear delegates, topics, etc.
        }

        // Save on close
        primaryStage.setOnCloseRequest(event -> {
            Committee.saveToJson(saveFile);
            Main.L.info("Committee data saved to file.");
        });

        showInitPage();
    }


    // =======================
    // PAGES
    // =======================

    /**
     * Init Page
     * <p>
     * Shows text fields in which the user enters committee name and speech length
     * <p>
     * Buttons:
     * <p>
     *     - Confirm -> gets input from text fields and enters them to committee
     * <p>
     */
    public void showInitPage() {
        Main.L.info("Showing Init Page");

        // Page Logic
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Committee Name");

        TextField lengthField = new TextField();
        lengthField.setPromptText("Enter Speech Length (seconds)");

        // Add CheckBox for Security Council
        CheckBox securityCouncilCheckBox = new CheckBox("Is Security Council?");

        Button confirmButton = new Button("Confirm");

        // Set up UI
        Main.L.info("Setting up UI");

        VBox root = new VBox(10.0, nameField, lengthField, securityCouncilCheckBox, confirmButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Button Logic
        confirmButton.setOnAction(_ -> {
            Main.L.info("Button clicked");
            String name = nameField.getText().trim();
            String lengthText = lengthField.getText().trim();
            int length;
            boolean isSecurityCouncil = securityCouncilCheckBox.isSelected(); // Get checkbox value

            if (name.isEmpty()) {
                showAlert("Name Required", "Enter a committee name");
                nameField.requestFocus();
                return;
            }

            try {
                length = Integer.parseInt(lengthText);
                if (length < 30 || length > 120) {
                    showAlert("Invalid Length", "Speech length must be between 30s ~ 120s");
                    lengthField.requestFocus();
                    return;
                }

                // Both inputs are valid - proceed with logic
                Main.L.info("Committee: " + name + ", Speech Length: " + length + ", Security Council: " + isSecurityCouncil);

                Committee.setName(name);
                Committee.setSpeechLength(length);
                Committee.setIsSC(isSecurityCouncil);

                showSetupPage();

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Enter a valid number for speech length");
                lengthField.requestFocus();
            }
        });
    }


    /**
     * Roll Call Page
     * <p>
     * Shows Text Fields for entering delegates if no file is given
     * <p>
     * Shows buttons in which user can:
     * <p>
     *     - extract from file
     *     - save to file
     *     - confirm input and direct to policy debate page
     * </p>
     * <p>
     *
     * <p>
     *
     */
    public void showSetupPage() {
        Main.L.info("Showing Setup Page");

        rollCallController = new SetupController();
        Scene scene = new Scene(rollCallController.getView(), MIN_WIDTH, MIN_HEIGHT);
        primaryStage.setTitle("Committee Setup");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Page Logic

        // Set up UI

        // Button Logic

    }




    // ======================
    // Helper Methods
    // ======================


    // Helper method for alerts
    public static void showAlert(String title, String message) {
        Main.L.info("Showing alert: title: " + title + ", message: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method for extracting a List of Strings from a file
    private List<String> extractFromFile (String filename) {
        try {
            Main.L.info("Reading file from " + filename);
            return Files.readAllLines(Paths.get(filename));
        } catch (Exception e) {
            Main.L.severe("Error extracting file (" + filename + ")");
            return null;
        }
    }


}
