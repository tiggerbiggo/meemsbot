package core;



import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//https://xkcd.com/i/info.0.json

public class XKCD {

  public static Comic getComicFromURL(String in){
    try{
      return getComicFromURL(new URL(in));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Comic getComicFromURL(URL in){
    try{
      InputStream stream = in.openStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String raw = reader.readLine();

      Gson gson = new Gson();
      return gson.fromJson(raw, Comic.class);
    }
    catch(IOException | JsonSyntaxException ex){
      ex.printStackTrace();
    }
    return null;
  }
}

class Comic{
  public int day, month, year, num;
  public String title, transcript, img, alt;

  @Override
  public String toString(){
    return "Comic Number " + num + " transcript:\n" + transcript + "\n\n";
  }

  public List<String> getDecriptions(){
    String[] lines = transcript.split("\n");
    List<String> toReturn = new ArrayList<>();
    for(String s : lines){
      if(s.startsWith("[[")){
        toReturn.add(s.substring(2, s.length()-2));
        System.out.println(toReturn.get(toReturn.size()-1));
      }
    }
    return toReturn;
  }

  public List<Speech> getSpeech(){
    String[] lines = transcript.split("\n");
    List<Speech> toReturn = new ArrayList<>();
    for(String s : lines){
      char first = s.charAt(0);
      if(first == '[' || first == '(' || first == '{' || first == '<'){
        continue;
      }
      Character character = Character.getCharFromName(s.substring(0, s.indexOf(":")));

    }
    return null;
  }
}

class Speech{
  Character character;
  String text;

  public Speech(Character character, String text) {
    this.character = character;
    this.text = text;
  }

  public Character getCharacter() {
    return character;
  }

  public String getText() {
    return text;
  }
}

enum Character{
  DEFAULT("Generic"),
  BERET("Beret"),
  CUEBALL("Cueball");

  String name;

  Character(String name){
    this.name = name;
  }

  public static Character getCharFromName(String name){
    name = name.toLowerCase();
    for(Character c : Character.values()){
      if(name.equals(c.name.toLowerCase())) return c;
    }
    return DEFAULT;
  }

  public String getName(){
    return name;
  }
}

/*Key:

[[Description]]
{{Misc}}
<<FX>>

 */
