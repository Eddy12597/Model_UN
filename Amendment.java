import lombok.*;

@Getter
public class Amendment {

    private AmendmentType type;
    private final Delegate submitter;
    private final int id;


    public enum AmendmentType {
        Add, Amend, Strike
    }

    public Amendment(AmendmentType type, Delegate submitter) {
        this.type = type;
        this.submitter = submitter;
        this.id = Committee.getNumTotalAmends();
        Committee.addAmendment(this);
    }

    public void setAmendmentType(AmendmentType type) {
        this.type = type;
        
    }


}
