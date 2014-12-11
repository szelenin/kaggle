package kaggle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model implements Serializable {
    private static final Logger logger = LogManager.getLogger(Model.class);

    private static final long serialVersionUID = 1648607850532636804L;
    private NGramCounts nGramCounts;
    private static final Pattern delimiter = Pattern.compile("\\s+|\\W");
    private int sentencesCount = 0;
    private int totalWords = 0;

    public Model() {
        this(2);
    }

    public Model(int n) {
        nGramCounts = new NGramCounts(n);
    }

    public void put(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase());
        scanner.useDelimiter(delimiter);
        logger.trace("Starting sentence: {}", sentence);

        nGramCounts.newSentence();

        while (scanner.hasNext()) {
            String word = scanner.next();
            if (StringUtils.isBlank(word)) {
                continue;
            }
            nGramCounts.put(word);
            totalWords++;
        }
        nGramCounts.put("_stop_");
        nGramCounts.finishSentence();

        sentencesCount++;
    }


    public int count(String... wordSequence) {
        return nGramCounts.getCount(wordSequence);
    }


    public int uniqueWordsCount() {
        return nGramCounts.getN() > 1 ? nGramCounts.rootChildrenCount() - 1 : nGramCounts.rootChildrenCount();
    }

    public int sentencesRead() {
        return sentencesCount;
    }

    public int totalWords() {
        return totalWords;
    }
}
