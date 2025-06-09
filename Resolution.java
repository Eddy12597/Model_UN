import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter @AllArgsConstructor
public class Resolution {
    private int topicNumber; // Maybe 'final'?
    private int resolutionNumber;

    private Delegate mainSubmitter;
    private final List<Delegate> coSubmitters;

    private final List<Clause> clauses;

    public Resolution(int topicNumber, Delegate mainSubmitter) {
        this.topicNumber = topicNumber;
        this.resolutionNumber = Committee.getNumResolutions() + 1; // Could cause problems
        Committee.addResolution(this);
        this.mainSubmitter = mainSubmitter;
        this.coSubmitters = new ArrayList<>();
        this.clauses = new ArrayList<>();
    }


    // Swaps resolution numbers
    public synchronized void setResolutionNumber(int newResolutionNumber, int numberToBeSwapped) {
        List<Resolution> allResolutions = Committee.getResolutions();

        // Prevent self-swap
        if (numberToBeSwapped == this.resolutionNumber) {
            this.resolutionNumber = newResolutionNumber;
            return;
        }

        // Find resolution to swap
        Optional<Resolution> resolutionToSwap = allResolutions.stream()
                .filter(res -> res.getResolutionNumber() == numberToBeSwapped)
                .findFirst();

        // Ensure new number is unique
        boolean numberExists = allResolutions.stream()
                .anyMatch(res -> res.getResolutionNumber() == newResolutionNumber);
        if (numberExists) {
            throw new IllegalArgumentException("Resolution number " + newResolutionNumber + " already exists");
        }

        // Perform swap or update
        resolutionToSwap.ifPresent(res -> res.resolutionNumber = this.resolutionNumber);
        this.resolutionNumber = newResolutionNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\tResolution " + resolutionNumber + (!Committee.isSC() ? (" submitted by " + mainSubmitter) : "") + " with " + clauses.size() + " clauses:\n");
        for (Clause clause : clauses) {
            sb.append(clause.toString()).append("\n");
        }
        return sb.toString();
    }

    public Clause addClause(){
        return addClause(clauses.size() + 1);
    }

    public Clause addClause(Delegate mainSubmitter) {
        return addClause(clauses.size() + 1, mainSubmitter);
    }

    public Clause addClause(int index) {
        Clause temp = new Clause(index, this);
        clauses.add(temp);
        return temp;
    }

    public Clause addClause(int index, Delegate mainSubmitter) {
        Clause temp = new Clause(index, this, mainSubmitter);
        clauses.add(temp);
        return temp;
    }

    public void addClause(Clause clause) {
        clauses.add(clause);
    }

    public void addCoSubmitter(Delegate coSubmitter) {
        coSubmitters.add(coSubmitter);
    }

    public void removeCoSubmitter(Delegate coSubmitter) {
        coSubmitters.remove(coSubmitter);
    }

    public Delegate getCoSubmitter(int index) {
        return coSubmitters.get(index);
    }

    public Clause clause(int index) {
        for (Clause clause : clauses) {
            if (clause.getClauseIdx() == index) {
                return clause;
            }
        }
        Main.L.warning("Clause " + index + " not found in resolution " + resolutionNumber + (Committee.isSC() ? " submitted by " + mainSubmitter : ""));
        return new Clause(index, this);
    }

    public int compareTo(Resolution other) {
        return this.resolutionNumber - other.getResolutionNumber();
    }

}