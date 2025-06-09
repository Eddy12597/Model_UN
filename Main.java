import javafx.application.Application;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.*;

public class Main {
    public static final Logger L = Logger.getLogger(Main.class.getName());

    public static FileHandler fileHandler;

    public static void setupLogging() {
        try {
            Locale.setDefault(Locale.US);

            fileHandler = new FileHandler("app.log", false);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(
                            "%1$tH:%1$tM:%1$tS [%2$-7s] %3$s.%4$s(): %5$s %n",
                            record.getMillis(),
                            record.getLevel().getName(),
                            record.getSourceClassName(),
                            record.getSourceMethodName(),
                            record.getMessage()
                    );
                }
            });
            L.addHandler(fileHandler);

            // Remove default console handler (optional)
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }
        } catch (Exception e) {
            L.severe("Failed to set up file logging: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        setupLogging();

        L.info("Program Started");

        // Prompt user to load or create new committee
        Scanner scanner = new Scanner(System.in);
        System.out.print("Load committee from file? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        File saveFile = new File("committee.json");
        if (choice.equals("y") && saveFile.exists()) {
            Committee.loadFromJson(saveFile);
            L.info("Loaded committee from file.");
        } else {
            L.info("Starting new committee.");
        }

        // Register shutdown hook to save committee data on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Committee.saveToJson(saveFile);
            L.info("Committee data saved to file.");
        }));

        Application.launch(GuiManager.class, args);

        L.info("Program Finished");
    }
}