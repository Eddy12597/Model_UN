import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.time.*;
import java.util.*;

/**
 * Holds Delegate in a Model UN Conference
 * Includes Speech and Amendment features
 * Keeps track of performance
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Delegate {
    private final int REMINDER_1 = 15;
    private final int REMINDER_2 = 5;

    // Changed fields to JavaFX properties
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Performance> performance = new SimpleObjectProperty<>();
    private boolean presentAndVoting;
    private boolean isMainSubmitter; // <-- Add this for Jackson serialization

    @JsonIgnore
    private final BooleanProperty isMainSubmitterProperty = new SimpleBooleanProperty();

    // Constructors
    public Delegate(String name, Performance performance, boolean presentAndVoting, boolean isMainSubmitter) {
        this.name.set(name);
        this.performance.set(performance);
        this.presentAndVoting = presentAndVoting;
        this.isMainSubmitter = isMainSubmitter;
        this.isMainSubmitterProperty.set(isMainSubmitter);
        // Committee.addDelegate(this); // REMOVE this line to prevent double-add
    }

    public Delegate(String name, Performance performance) {
        this(name, performance, false, false);
    }

    public Delegate(String name) {
        this(name, new Performance());
    }

    public Delegate(String name, boolean presentAndVoting) {
        this(name, new Performance(), presentAndVoting, false);
    }

    public Delegate() {
        this(UUID.randomUUID().toString());
    }

    // Property accessors (needed for TableView binding)
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<Performance> performanceProperty() { return performance; }
    public BooleanProperty presentAndVotingProperty() { return new SimpleBooleanProperty(presentAndVoting); }
    public BooleanProperty isMainSubmitterProperty() { return isMainSubmitterProperty; }

    // Regular getters (compatible with Lombok)
    public String getName() { return name.get(); }
    public Performance getPerformance() { return performance.get(); }
    public boolean isPresentAndVoting() { return presentAndVoting; }
    public boolean isIsMainSubmitter() { return isMainSubmitter; }

    // Regular setters (compatible with Lombok)
    public void setName(String name) { this.name.set(name); }
    public void setPerformance(Performance performance) { this.performance.set(performance); }
    public void setPresentAndVoting(boolean presentAndVoting) { this.presentAndVoting = presentAndVoting; }
    public void setIsMainSubmitter(boolean isMainSubmitter) { 
        this.isMainSubmitter = isMainSubmitter;
        this.isMainSubmitterProperty.set(isMainSubmitter);
    }

    // HELPER METHODS
    public static void pressEnterToProceed() {
        System.out.println("Press Enter to proceed...");
        try {
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // DELEGATE ACTIONS (keep your existing methods)
    public void giveSpeech() throws IOException, InterruptedException {
        giveSpeech(60);
    }

    public void giveSpeech(int seconds) throws IOException, InterruptedException {
        giveSpeech(seconds, REMINDER_1, REMINDER_2);
    }

    public void giveSpeech(int seconds, int reminder1, int reminder2) throws IOException, InterruptedException {
        pressEnterToProceed();
        getPerformance().incSpeech();
        System.out.println("Delegate of " + name + " giving a speech");
        final Instant start = Instant.now();
        int lastDisplayed = 0;
        boolean reminder1Shown = false;
        boolean reminder2Shown = false;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            Instant now = Instant.now();
            long elapsed = Duration.between(start, now).toMillis() / 1000;
            long remaining = seconds - elapsed;

            if (remaining <= 0) {
                System.out.println("\nTime's up! Total duration: " + elapsed + "s");
                break;
            }

            // Display time remaining at 1-second intervals or last 5 seconds
            if (elapsed - lastDisplayed >= 5 || remaining <= 5) {
                System.out.print("\rTime remaining: " + remaining + "s / " + seconds + "s");
                System.out.flush();
                lastDisplayed = (int)elapsed;
            }

            // Show reminders only once
            if (!reminder1Shown && remaining <= reminder1) {
                System.out.println("\nReminder 1: " + reminder1 + " seconds remaining");
                reminder1Shown = true;
            }
            if (!reminder2Shown && remaining <= reminder2) {
                System.out.println("\nReminder 2: " + reminder2 + " seconds remaining");
                reminder2Shown = true;
            }

            // Check for early finish (Java doesn't have _kbhit equivalent)
            if (System.in.available() > 0) {
                char input = (char)System.in.read();
                if (input == 'f') {
                    System.out.println("\nSpeech finished early at " + elapsed + " seconds");
                    break;
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("\nSpeech interrupted");
                return;
            }
        }

        System.out.println(); // Ensure new line after timer

        System.out.println("POI Session:");
        System.out.println("Enter number of POIs the delegate would open to: ");
        int num_pois = scanner.nextInt();

        if (num_pois <= 0) {
            System.out.println("No POIs");
        } else {
            List<Delegate> poiDelegates = new ArrayList<>();

            for (int i = 0; i < num_pois; i++) {
                System.out.println("Enter country name for POI #" + (i + 1) + " (exit to break)");
                String countryName = scanner.next();
                if (countryName.equals("exit")) break;

                Delegate poiDelegate = Committee.find(countryName);
                if (poiDelegate != null) {
                    poiDelegates.add(poiDelegate);
                } else {
                    System.out.println("Delegate from " + countryName + " not found. Skipping this POI.");
                }
            }

            for (Delegate delegate : poiDelegates) {
                delegate.raisePOI();
            }
        }
    }

    // TODO
    public void raiseAmendment() {
        Scanner scanner = new Scanner(System.in);
        getPerformance().incAmendment();
        System.out.println("Enter Amendment Type: ");
        String amendmentType = scanner.next();
    }

    public void raisePOI() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Delegate of " + name + " raising POI...");
        while (!scanner.nextLine().equals("f")) {
            Thread.sleep(100);
        }
        scanner.nextLine();
        System.out.println("POI from " + name + " complete.");
        System.out.println("Follow up from " + name + "?");
        String followup = scanner.nextLine();
        if (followup.equalsIgnoreCase("yes") || followup.equalsIgnoreCase("y") || followup.equals("1")) {
            getPerformance().incPOI();
            System.out.println("Follow up from " + name);
            while (!scanner.nextLine().equalsIgnoreCase("f")) {
                Thread.sleep(200);
            }
            System.out.println("Follow up from delegate of " + name + " complete");
        }


    }


    // =======================
    // MUN specific methods
    // =======================



    public String getDetails() {
        return "Country: " + name.get() + "\n"
                + "- Number of POIs: " + getPerformance().getNumPOIs() + "\n"
                + "- Number of Speeches: " + getPerformance().getNumSpeeches() + "\n"
                + "- Number of Amendments: " + getPerformance().getNumAmendments() + "\n"
                + "- Present and Voting: " + presentAndVoting + "\n"
                + "- Is Main Submitter: " + isMainSubmitter + "\n"
                + "- Is Permanent Member (can Veto): " + Committee.isPermanentMember(String.valueOf(name)) + "\n\n";
    }

    // ==============================


    @Override
    public String toString() {
        return "Delegate of " + name.get() + (presentAndVoting ? " (Present and Voting)" : " (Present)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delegate)) return false;
        Delegate other = (Delegate) o;
        return Objects.equals(name.get(), other.name.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.get());
    }

    public double getScore() {
        return getPerformance().getScore() *
                (Boolean.parseBoolean(String.valueOf(presentAndVoting)) ? 1.1 : 1) *
                (Boolean.parseBoolean(String.valueOf(isMainSubmitter)) ? 1.1 : 1)
        ;
    }




    public boolean isMainSubmitter() {
        return isMainSubmitter;
    }

    public void setMainSubmitter(boolean mainSubmitter) {
        setIsMainSubmitter(mainSubmitter);
    }
}
