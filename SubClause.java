import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter @Setter
public class SubClause {
    private int subClauseIdx;
    private List<SubSubClause> subSubClauses;
    private Clause parentClause;

    public SubClause(int subClauseIdx, List<SubSubClause> subSubClauses, Clause parentClause) {
        this.subClauseIdx = subClauseIdx;
        this.subSubClauses = subSubClauses;
        this.parentClause = parentClause;
    }

    public SubClause(int subClauseIdx, Clause parentClause) {
        this.subClauseIdx = subClauseIdx;
        this.subSubClauses = new ArrayList<>();
        this.parentClause = parentClause;
    }

    public SubSubClause addSubSubClause() {
        return addSubSubClause(subSubClauses.size() + 1);
    }

    public SubSubClause addSubSubClause(int index) {
        SubSubClause newClause = new SubSubClause(index, this);
        addSubSubClause(newClause);
        return newClause;
    }

    public SubSubClause addSubSubClause(SubSubClause subSubClause) {
        subSubClauses.add(subSubClause);
        return subSubClause;
    }

    public void removeSubSubClause(SubSubClause subSubClause) {
        subSubClauses.remove(subSubClause);
    }

    public SubSubClause subSubClause(int subSubClauseIdx) {
        for (SubSubClause subSubClause : subSubClauses) {
            if (subSubClause.getSubSubClauseIdx() == subSubClauseIdx) {
                return subSubClause;
            }
        }
        Main.L.warning("Sub-sub-clause " + subSubClauseIdx + " not found in sub-clause " + subClauseIdx);
        return new SubSubClause(subSubClauseIdx, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\tSub-clause " + subClauseIdx);
        for (SubSubClause subSubClause : subSubClauses) {
            sb.append(subSubClause.toString());
        }
        return sb.toString();
    }

    // TODO
    /*
    public String backTrace() {
        StringBuilder sb = new StringBuilder();

    }

     */


}
