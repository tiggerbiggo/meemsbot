import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Settings {
    public static final String SETTINGSFILE = "settings.prop";

    public static boolean subsEnabled = true;

    public static void getSettings(){
        try{
            FileInputStream stream = new FileInputStream(SETTINGSFILE);
            Scanner s = new Scanner(stream);

            subsEnabled = Boolean.parseBoolean(s.nextLine());
        }
        catch(IOException | NoSuchElementException ex){
            System.err.println("Invalid or nonexistent settings file.");
        }
    }
}
