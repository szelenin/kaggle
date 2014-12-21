package kaggle;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/21/2014.
 */
public class Sentence {
    private String sentenceWords;
    private static final Pattern delimiter = Pattern.compile("\\s+|\\W");

    public Sentence(String sentenceWords) {

        this.sentenceWords = sentenceWords;
    }

    public void iterateWords(Lambda<Pair<String, Integer>> lambda) {
        Scanner scanner = createScanner(sentenceWords);
        int currentWord = 0;
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (StringUtils.isBlank(word)) {
                continue;
            }
            lambda.invoke(new Pair<>(word, currentWord));
            currentWord++;
        }
    }

    private Scanner createScanner(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase());
        scanner.useDelimiter(delimiter);
        return scanner;
    }

}
