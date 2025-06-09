import lombok.*;


@Getter @Setter
public class SubSubClause {

    private int subSubClauseIdx;
    private SubClause parentSubClause;

    public SubSubClause(int subSubClauseIdx, SubClause parentSubClause) {
        this.subSubClauseIdx = subSubClauseIdx;
        this.parentSubClause = parentSubClause;
    }
    public String toString() {
        return "\n\t\t\t\tSub-sub-clause " + subSubClauseIdx;
    }
}
