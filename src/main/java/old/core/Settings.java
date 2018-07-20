package old.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Settings {
    public static final String SETTINGSFILE = "settings.prop";
    public static final String TOKENFILE = "TOKEN.tok";

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

    public static String getToken(){
        try{
            FileInputStream stream = new FileInputStream(TOKENFILE);
            Scanner s = new Scanner(stream);

            return s.nextLine();
        }
        catch(IOException ex){
            System.err.println(
                    "Invalid or nonexistent token file, " +
                    "token file (TOKEN.tok) should be in same directory as " +
                    "jar or in the root directory of the project.");
        }
        return "ERR";
    }
}
