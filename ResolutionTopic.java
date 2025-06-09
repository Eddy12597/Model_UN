import java.util.*;
import lombok.*;

@Setter
public class ResolutionTopic {

    @Getter @Setter
    private String topicName;
    @Getter @Setter
    private int topicNumber;

    private List<Resolution> resolutions;

    public ResolutionTopic(String topicName, int topicNumber, List<Resolution> resolutions) {
        this.topicName = topicName;
        this.topicNumber = topicNumber;
        this.resolutions = resolutions;
    }

    public ResolutionTopic(String topicName, int topicNumber) {
        this.topicName = topicName;
        this.topicNumber = topicNumber;
        this.resolutions = new ArrayList<>();
    }

    // === ADD RESOLUTION ===

    public Resolution addResolution(Delegate mainSubmitter) {
        Resolution temp = new Resolution(resolutions.size() + 1, mainSubmitter);
        this.resolutions.add(temp);
        return temp;
    }

    public Resolution addResolution(int topicNumber, Delegate mainSubmitter) {
        Resolution temp = new Resolution(topicNumber, mainSubmitter);
        this.resolutions.add(temp);
        return temp;
    }

    public Resolution addResolution(Resolution resolution) {
        resolutions.add(resolution);
        return resolution;
    }

    // ======================

    public Resolution resolution(int index) {
        for (Resolution resolution : resolutions) {
            if (resolution.getResolutionNumber() == index) {
                return resolution;
            }
        }
        Main.L.warning("Resolution " + index + " not found");
        return new Resolution(index, new Delegate("Temporary Main Submitter for Resolution " + index + " of Topic " + topicName));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder( "Resolution Topic " + topicNumber + ": " +
                topicName + "\n");
        for (Resolution resolution : resolutions) {
            sb.append(resolution.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

}
