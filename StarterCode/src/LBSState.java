import java.util.Stack;

public class LBSState{

    private LBSLayout stateLayout;
    public Stack<Integer> moves;
    private Boolean savingGraceUsed;

    
    public LBSState(LBSLayout stateLayout){
        this.stateLayout = stateLayout;
        this.moves = new Stack<Integer>();
        this.savingGraceUsed = false;
    }

    public LBSState(LBSLayout stateLayout, Stack<Integer> moves){
        this.stateLayout = stateLayout;
        this.moves = moves;
        this.savingGraceUsed = false;
    }

    public LBSLayout getStateLayout(){
        return this.stateLayout;
    }

    public void setStateLayout(LBSLayout stateLayout){
        this.stateLayout = stateLayout;
    }

    public Boolean getSavingGrace(){
        return this.savingGraceUsed;
    }

    public void useSavingGrace(){
        this.savingGraceUsed = true;
    }

}