package old.text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * text.Markov
 *
 */
public class Markov {
    private String[] input;
    private Hashtable<String, Vector<String>> index;
    private Random rand = new Random();
    private ArrayList<String> uniqueWords = new ArrayList<>();

    public Markov() {
        index = new Hashtable<>();
    }

    public void addToIndex(String in) {
        in = in.toLowerCase();
        String[] input = in.split(" ");
        for(int i = 0; i < input.length-1; i++) {
            String word = input[i];
            String nextWord = input[i + 1];

            if(!isValid(word) || !isValid(nextWord)) {
                continue;
            }

            word = removeUnwantedStrings(word);
            nextWord = removeUnwantedStrings(nextWord);

            if(!uniqueWords.contains(word)) {
                uniqueWords.add(word);
            }

            Vector<String> chain = index.get(word);
            if(chain == null) {
                chain = new Vector<>();
            }
            chain.add(nextWord);
            index.put(word, chain);
        }
    }

    private String removeUnwantedStrings(String input) {
        input = input.replaceAll("(edited)", "");
        return input;
    }

    private boolean isValid(String input) {
        if(   input.equals("")
                ||input.equals("#")
                ||input.equals("-")
                ||input.equals("(edited)")
                ||input.startsWith("@")
                ||input.startsWith("http://")
                ||input.startsWith("https://"))
            return false;
        else return true;
    }

    public int getNumOfUniqueWords() {
        return uniqueWords.size();
    }

    public String getRandomUniqueWord(){
        return uniqueWords.get(rand.nextInt(uniqueWords.size()));
    }

    public String generateSentence(int length) {
        List<String> sentence = new ArrayList<>();
        int target = length;
        String next;
        String startingPoint = getRandomUniqueWord();
        Vector<String> start = index.get(startingPoint);

        next = start.get(rand.nextInt(start.size()));
        sentence.add(next);
        target -= next.length();

        while(target > 0) {
            if(index.get(next) != null) {
                Vector<String> wordChain = index.get(next);
                next = wordChain.get(rand.nextInt(wordChain.size()));
                sentence.add(next);
                target -= next.length() + 1;
            } else {
                next = getRandomUniqueWord();
            }
        }
        return toString(sentence);
    }

    private String toString(List<String> s) {
        return String.join(" ", s);
    }

}