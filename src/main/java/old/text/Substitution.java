package old.text;

public class Substitution {
    String from, to;
    public Substitution(String from, String to){
        this.from = from;
        this.to = to;
    }

    public boolean contains(String toCheck){
        return toCheck.contains(from);
    }

    public String replace(String toReplace) {
        return toReplace.replace(from, to);
    }
}
