package kaggle;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model {
    private Node rootNode = new Node();
    private final Pattern delimiter = Pattern.compile("\\s+");

    public void put(String sentence) {
        Scanner scanner = new Scanner(sentence);
        scanner.useDelimiter(delimiter);
        String word1 = scanner.next();
        while (scanner.hasNext()) {
            String word2 = scanner.next();
            rootNode.put(word1, word2);
            word1 = word2;
        }
    }

    public int count(String ... wordSequence) {
        return rootNode.getSequenceCount(wordSequence);
    }


}
