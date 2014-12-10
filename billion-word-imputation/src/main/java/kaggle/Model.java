package kaggle;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model {
    private Node rootNode = new Node();
    private final Pattern delimiter = Pattern.compile("\\s+|\\W");

    public void put(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase() + " _stop_");
        scanner.useDelimiter(delimiter);
        String word1 = "*";
        while (scanner.hasNext()) {
            String word2 = scanner.next();
            if (StringUtils.isBlank(word2)) {
                continue;
            }
            rootNode.put(word1, word2);
            word1 = word2;
        }
        rootNode.put(word1, "_stop_");
    }

    public int count(String... wordSequence) {
        return rootNode.getSequenceCount(wordSequence);
    }


}
