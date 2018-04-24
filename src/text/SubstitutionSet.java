package text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SubstitutionSet {
    List<Substitution> set;

    public SubstitutionSet(File source){
        try{
            FileInputStream str = new FileInputStream(source);
            Scanner s = new Scanner(str);

            set = new ArrayList<>();

            while(s.hasNextLine()){
                String[] arr = s.nextLine().split(",");
                set.add(new Substitution(arr[0], arr[1]));
            }
        }
        catch(IOException | ArrayIndexOutOfBoundsException ex){
            System.err.println("Error in text.SubstitutionSet Constructor: " + ex.getMessage());
            set = new ArrayList<>();
        }
    }

    public SubstitutionSet(){
        set = new ArrayList<>();
    }

    public void addSub(Substitution s){
        set.add(s);
    }
    public void addSub(String from, String to){
        addSub(new Substitution(from, to));
    }

    public boolean contains(String toCheck){
        for(Substitution s : set){
            if(s.contains(toCheck)) return true;
        }
        return false;
    }

    public String replaceAll(String toReplace){
        for(Substitution s : set){
            toReplace = s.replace(toReplace);
        }
        return toReplace;
    }
}
