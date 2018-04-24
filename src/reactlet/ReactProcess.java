package reactlet;

public abstract class ReactProcess {
    public String reaction;

    public ReactProcess(String reaction) {
        this.reaction = reaction;
    }

    protected abstract void onReact();

    public boolean check(String toCheck){
        if(toCheck.equals(reaction)){
            onReact();
            return true;
        }
        return false;
    }
}
