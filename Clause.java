import java.util.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor
public class Clause {
    private int clauseIdx;
    private Resolution parentResolution;
    private List<SubClause> subClauses;
    private Delegate mainSubmitter;

    // for Non-SC
    public Clause(int clauseIdx, Resolution parentResolution) {
        this.clauseIdx = clauseIdx;
        this.parentResolution = parentResolution;
        this.subClauses = new ArrayList<>();
        this.mainSubmitter = null;
    }

    public Clause(int clauseIdx, Resolution parentResolution, Delegate mainSubmitter) {
        this.clauseIdx = clauseIdx;
        this.parentResolution = parentResolution;
        this.subClauses = new ArrayList<>();
        if (Committee.isSC())
            this.mainSubmitter = mainSubmitter;
    }

    public SubClause addSubClause() {
        return addSubClause(subClauses.size() + 1);
    }

    public SubClause addSubClause(int idx) {
        return addSubClause(new SubClause(idx, this));
    }

    public SubClause addSubClause(SubClause subClause) {
        subClauses.add(subClause);
        return subClause;
    }

    public void removeSubClause(SubClause subClause) {
        subClauses.remove(subClause);
    }

    public SubClause subClause(int index) {
        for (SubClause subClause : subClauses) {
            if (subClause.getSubClauseIdx() == index) {
                return subClause;
            }
        }
        Main.L.warning("SubClause with index " + index +
                " not found in Clause " + clauseIdx);
        return new SubClause(index, this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder ("\t\tClause " + clauseIdx + (Committee.isSC() ? " submitted by " + mainSubmitter : ""));
        for (SubClause subClause : subClauses) {
            sb.append("\n\t\t").append(subClause.toString());
        }
        return sb.toString();
    }
}
