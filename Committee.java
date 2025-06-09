import com.fasterxml.jackson.databind.DatabindException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Static Class for organizing global information belonging to the committee
// Key changes to make for adapting to GUI marked as breakpoints
@Setter
public class Committee {

    // Committee Info

    // boolean flag for Security Council
    private static boolean isSC;


    public static void setIsSC(boolean isSC) {
        Committee.isSC = isSC;
    }
    public static boolean isSC() {
        return isSC;
    }

    @Getter
    private static String name = "Not Set";
    private static int speechLength = 60;

    public static void setName(String name) {
        Committee.name = name;
    }

    public static void setSpeechLength(int speechLength) {
        Committee.speechLength = speechLength;
    }

    private static List<ResolutionTopic> resolutionTopics = FXCollections.observableArrayList();

    // ===================
    // DELEGATES
    // ===================

    @Getter
    private static final ObservableList<Delegate> delegates = FXCollections.observableArrayList();

    public static boolean isPermanentMember(String countryName) {
        return switch (countryName) {
            case "United States", "US", "United States Of America",
                 "United Kingdom", "UK",
                 "Russian Federation", "Russia",
                 "France", "French Republic",
                 "China", "PRC", "Peoples Republic Of China",
                 "People's Republic Of China" -> isSC;
            default -> false;
        };
    }

    public static Delegate find(String name) {
        for (Delegate d : delegates) {
            if (d.getName().equals(name)) {
                return d;   
            }
        }
        Main.L.warning("Delegate with name " + name + " not found.");
        // Maybe ask user to create new one, or maybe create new Delegate by default?
        return null;
    }

    public static void addDelegate(Delegate delegate) {
        delegates.add(delegate);
    }

    public static void removeDelegate(Delegate delegate) {
        delegates.remove(delegate);
    }

    public static void printAllDetails() {
        System.out.println("\n========== COMMITTEE DETAILS ==========");
        System.out.println("Committee Name: " + name + " (" + (isSC ? "SC Mode" : "Normal Mode") + ")");
        System.out.println("Speech Length: " + speechLength);
        System.out.println("Number of delegates: " + delegates.size());
        System.out.println("Number of Topics: " + resolutionTopics.size());
        System.out.println("Number of resolutions: " + resolutions.size());

        System.out.println("=======================================\n");
        System.out.println("========== DELEGATE  DETAILS ==========\n");
        printAllDelegateDetails();

        System.out.println("=======================================\n");
        System.out.println("========= RESOLUTION DETAILS ==========");
        printAllResolutionDetails();

    }

    public static void printAllDelegateDetails() {
        for (Delegate delegate : delegates) {
            System.out.println(delegate.getDetails());
        }
    }

    public static void printAllResolutionDetails() {
        for (ResolutionTopic topic : resolutionTopics) {
            System.out.println(topic);
        }
    }

    public static void rollCall() {
        Scanner scanner = new Scanner(System.in);
        for (Delegate delegate : delegates) {
            while (true) {
                System.out.println(delegate.getName() + "? ([P]/[PNV])");
                String presentAndVoting = scanner.nextLine();
                if (presentAndVoting.equalsIgnoreCase("p")) {
                    System.out.println(delegate.getName() + ": Present");
                    break;
                } else if (presentAndVoting.equalsIgnoreCase("pnv")) {
                    System.out.println(delegate.getName() + ": Present and Voting");
                    break;
                }
                System.out.println("Unrecognized: " + presentAndVoting);
            }
        }
    }

    // =======================
    // RESOLUTIONS
    // =======================

    // ===== Simple Resolution addition

    /**
     *
     * @param indices ResolutionTopic #, Resolution #, Clause #, SubClause #, SubSubClause #
     */
    public static void simpleAdd(int... indices) {
        if (indices.length == 0) {
            throw new IllegalArgumentException("At least one index required");
        }

        // Ensure the resolution topic exists
        ResolutionTopic topic = resolutionTopic(indices[0]);

    }

    // ===============================================

    public static void addResolutionTopic(ResolutionTopic topic) {
        resolutionTopics.add(topic);
    }

    public static void addResolutionTopic(String topicName) {
        resolutionTopics.add(new ResolutionTopic(topicName, resolutionTopics.size() + 1));
    }

    /**
     * Finds resolution topic based on index. If not found, creates a new topic
     * @param index the index of the resolution to be found
     * @return either the resolution topic corresponding to it or a new resolution topic with the given index
     */
    public static ResolutionTopic resolutionTopic(int index) {
        for (ResolutionTopic topic : resolutionTopics) {
            if (topic.getTopicNumber() == index) {
                return topic;
            }
        }
        Main.L.info("Resolution Topic " + index + " not found. Creating a new topic.");
        ResolutionTopic newTopic = new ResolutionTopic("(Anonymous) Topic " + index, index);
        resolutionTopics.add(newTopic);
        return newTopic;
    }

    public static String details() {
        return "=== COMMITTEE ===\n" + "- Name: " + name + "\n- Speech Length: " + speechLength + "\n- Number of delegates: " + delegates.size() + "\n- Number of Resolutions: " + resolutions.size() + "\n=================";
    }

    // Thread-safe list implementation
    private static final List<Resolution> resolutions = new CopyOnWriteArrayList<>();

    // Returns an unmodifiable view of the resolutions list
    public static List<Resolution> getResolutions() {
        return Collections.unmodifiableList(resolutions);
    }

    public static int getNumResolutions() {
        return resolutions.size();
    }

    public static synchronized void addResolution(Resolution resolution) {
        Objects.requireNonNull(resolution, "Resolution cannot be null");
        if (resolutions.contains(resolution)) {
            Main.L.warning("Resolution " + resolution.toString() + " already exists");
            return;
        }
        resolutions.add(resolution);
        sortResolutions();
    }

    public static synchronized void removeResolution(Resolution resolution) {
        Objects.requireNonNull(resolution, "Resolution cannot be null");
        if (!resolutions.remove(resolution)) {
            throw new NoSuchElementException("Resolution not found");
        }
        sortResolutions();
    }

    public static Resolution getResolution(int idx) {
        if (idx < 0 || idx >= resolutions.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + idx);
        }
        return resolutions.get(idx);
    }

    private static void insertionSort(Comparator<Resolution> comp) {
        for (int i = 1; i < resolutions.size(); i++) {
            Resolution key = resolutions.get(i);
            int j = i - 1;
            while (j >= 0 && comp.compare(resolutions.get(j), key) > 0) {
                resolutions.set(j + 1, resolutions.get(j));
                j--;
            }
            resolutions.set(j + 1, key);
        }
    }

    public static void sortResolutions() {
        if (resolutions.size() < 10) { // Small list → InsertionSort
            insertionSort(Resolution::compareTo);
        } else {
            resolutions.sort(Resolution::compareTo); // Default TimSort
        }
    }

    // Additional utility methods for resolutions
    public static Optional<Resolution> findResolutionByNumber(int number) {
        return resolutions.stream()
                .filter(r -> r.getResolutionNumber() == number)
                .findFirst();
    }

    public static boolean containsResolution(Resolution resolution) {
        return resolutions.contains(resolution);
    }

    public static void clearResolutions() {
        resolutions.clear();
    }

    // ====================
    // Amendments
    // ====================

    private static final List<Amendment> amendmentList = Collections.synchronizedList(new ArrayList<>());

    public static int getNumTotalAmends() {
        return amendmentList.size();
    }

    public static void addAmendment(Amendment amendment) {
        amendmentList.add(amendment);
    }

    public static void removeAmendment(Amendment amendment) {
        amendmentList.remove(amendment);
    }

    public static Amendment find(int id) {
        for (Amendment amendment : amendmentList) {
            if (amendment.getId() == id) {
                return amendment;
            }
        }
        System.out.printf("Amendment with id %d not found\n", id);
        return null;
    }


    // ========================
    // UTILITY METHODS
    // ========================

    public static class Utils {

        // Note that subtracting 1 is needed if using this to access sub-sub-clauses using an index
        // No subtraction needed if using the classes' built in accessors

        public static int romanToInt(@NotNull String roman) {
            return switch (roman) {
                case "I" -> 1; case "II" -> 2; case "III" -> 3; case "IV" -> 4; case "V" -> 5;
                case "VI" -> 6; case "VII" -> 7; case "VIII" -> 8; case "IX" -> 9; case "X" -> 10;
                case "XI" -> 11; case "XII" -> 12; case "XIII" -> 13; case "XIV" -> 14; case "XV" -> 15;
                case "XVI" -> 16; case "XVII" -> 17; case "XVIII" -> 18; case "XIX" -> 19; case "XX" -> 20;
                case "XXI" -> 21; case "XXII" -> 22; case "XXIII" -> 23; case "XXIV" -> 24; case "XXV" -> 25;

                default -> throw new IllegalArgumentException(
                        roman.contains("X") || roman.contains("I") || roman.contains("V") ||
                                roman.contains("M") || roman.contains("C")?
                            "Invalid roman number: " + roman + ". Please check valid sub-sub-clause indices" :
                            "Roman number out of range: " + roman + ". Ensure sub-sub-clause indices are in range");
            };
        }

        public static String intToRoman(int number) {
            return switch (number) {
                case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
                case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII"; case 9 -> "IX"; case 10 -> "X";
                case 11 -> "XI"; case 12 -> "XII"; case 13 -> "XIII"; case 14 -> "XIV"; case 15 -> "XV";
                case 16 -> "XVI"; case 17 -> "XVII"; case 18 -> "XVIII"; case 19 -> "XIX"; case 20 -> "XX";
                case 21 -> "XXI"; case 22 -> "XXII"; case 23 -> "XXIII"; case 24 -> "XXIV"; case 25 -> "XXV";

                default -> throw new IllegalArgumentException(
                        number < 0 ?
                                "Invalid number: " + number + ". Please check valid sub-sub-clause indices" :
                                "Number out of range: " + number + ". Ensure sub-sub-clause indices are in range");
            };
        }
    }

    // Add this inner DTO class for serialization
    private static class CommitteeData {
        public boolean isSC;
        public String name;
        public int speechLength;
        public List<Delegate> delegates;
        public List<ResolutionTopic> resolutionTopics;
        public List<Resolution> resolutions;
        public List<Amendment> amendmentList;
    }

    // Save committee info to JSON file
    public static void saveToJson(File file) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        CommitteeData data = new CommitteeData();
        data.isSC = isSC;
        data.name = name;
        data.speechLength = speechLength;
        data.delegates = new ArrayList<>(delegates);
        data.resolutionTopics = new ArrayList<>(resolutionTopics);
        data.resolutions = new ArrayList<>(resolutions);
        data.amendmentList = new ArrayList<>(amendmentList);
        try {
            mapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Failed to save committee data: " + e.getMessage());
        }
    }

    // Load committee info from JSON file
    public static void loadFromJson(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            CommitteeData data = mapper.readValue(file, CommitteeData.class);
            isSC = data.isSC;
            name = data.name;
            speechLength = data.speechLength;
            delegates.setAll(data.delegates != null ? data.delegates : new ArrayList<>());
            resolutionTopics.clear();
            if (data.resolutionTopics != null) resolutionTopics.addAll(data.resolutionTopics);
            resolutions.clear();
            if (data.resolutions != null) resolutions.addAll(data.resolutions);
            amendmentList.clear();
            if (data.amendmentList != null) amendmentList.addAll(data.amendmentList);
        } catch (IOException e) {
            System.err.println("Failed to load committee data: " + e.getMessage());
        }
    }

}
