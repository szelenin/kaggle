package kaggle;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model {
    private NGrams nGrams = new NGrams(2);
    private final Pattern delimiter = Pattern.compile("\\s+|\\W");

    public void put(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase());
        scanner.useDelimiter(delimiter);

        while (scanner.hasNext()) {
            String word = scanner.next();
            if (StringUtils.isBlank(word)) {
                continue;
            }
            nGrams.put(word);
        }
        nGrams.put("_stop_");
    }

    public int count(String... wordSequence) {
        return nGrams.rootNode.getSequenceCount(wordSequence);
    }


}
