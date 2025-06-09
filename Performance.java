import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for organizing Delegate performance
 * <p>
 * Includes number of POIs, Speeches, and Amendments
 * <p>
 * Includes a double[] weights, which stores the weights for the three numbers,
 * in the same order
 * <p>
 *     Constructors:
 *     <code>
 *         <p>
 *         public Performance (int numPOIs, int numSpeeches, int numAmendments, double[] weights)
 *         <p>
 *         public Performance (int numPOIs, int numSpeeches, int numAmendments)
 *         <p>
 *         public Performance (double[] weights)
 *         <p>
 *         public Performance
 *     </code>
 * </p>
 */
@Setter
@Getter
public class Performance {

    // Getters and Setters
    private int numPOIs;
    private int numSpeeches;
    private int numAmendments;
    private double[] weights;
    
    // For compatibility with JSON
    private double score;

    private static final double[] defaultWeights = {0.4, 1, 2};

    // Constructors - if weights is not given, it is set to default

    public Performance(int numPOIs, int numSpeeches, int numAmendments, double[] weights) {
        this.numPOIs = numPOIs;
        this.numSpeeches = numSpeeches;
        this.numAmendments = numAmendments;
        this.weights = weights;
    }

    public Performance(int numPOIs, int numSpeeches, int numAmendments) {
        this.numPOIs = numPOIs;
        this.numSpeeches = numSpeeches;
        this.numAmendments = numAmendments;
        weights = new double[3];
        System.arraycopy(defaultWeights, 0, weights, 0, defaultWeights.length);
    }

    public Performance(double [] weights) {
        this.weights = weights;
    }

    public Performance() {
        numPOIs = 0;
        numSpeeches = 0;
        numAmendments = 0;
        weights = new double[3];
        System.arraycopy(defaultWeights, 0, weights, 0, defaultWeights.length);
    }

    // Get score by multiplying numbers with weights
    public double getScore() {
        try{
            return weights[0] * numPOIs + weights[1] * numSpeeches + weights[2] * numAmendments;
        } catch (NullPointerException e){
            System.out.println("Score Weight List not initialized!");
            return -1;
        }
    }

    // Weight setting
    public void setPOIWeight (final int weight) {
        weights[0] = weight;
    }
    public void setSpeechWeight (final int weight) {
        weights[1] = weight;
    }
    public void setAmendmentWeight (final int weight) {
        weights[2] = weight;
    }

    @Override
    public String toString() {
        return "Num. POIs: " + numPOIs + "\nNum. Speeches: " + numSpeeches + "\nNum. Amendments: " + numAmendments;
    }

    // Incrementers

    // Increments number of POIs
    public void incPOI() {
        numPOIs++;
    }

    // Increments number of speeches
    public void incSpeech() {
        numSpeeches++;
    }

    // Increments number of amendments
    public void incAmendment() {
        numAmendments++;
    }

    @JsonIgnore
    public void setScore(double score) {
        this.score = score;
    }

}
