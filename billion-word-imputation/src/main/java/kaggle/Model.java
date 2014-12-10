package kaggle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model implements Serializable {
    private static final Logger logger = LogManager.getLogger(Model.class);

    private static final long serialVersionUID = 1648607850532636804L;
    private Map<Integer, NGramCount> nGramCounts;
    private static final Pattern delimiter = Pattern.compile("\\s+|\\W");
    private int sentencesCount = 0;

    public Model() {
        this(2);
    }

    public Model(int ... nGrams) {
        this.nGramCounts = new HashMap<Integer, NGramCount>(nGrams.length);
        for (int nGram : nGrams) {
            nGramCounts.put(nGram, new NGramCount(nGram));
        }
    }

    public void put(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase());
        scanner.useDelimiter(delimiter);
        logger.trace("Starting sentence: {}", sentence);

        for (NGramCount nGramCount : nGramCounts.values()) {
            nGramCount.newSentence();
        }

        while (scanner.hasNext()) {
            String word = scanner.next();
            if (StringUtils.isBlank(word)) {
                continue;
            }
            putWord(word);
        }
        putWord("_stop_");
        sentencesCount++;
    }

    private void putWord(String word) {
        for (NGramCount nGramCount : nGramCounts.values()) {
            nGramCount.put(word);
        }
    }

    public int count(String... wordSequence) {
        NGramCount nGramCount = nGramCounts.get(wordSequence.length);
        if (nGramCount == null) {
            nGramCount = nGramCounts.values().iterator().next();
        }
        return nGramCount.getCount(wordSequence);
    }


    public int uniqueWordsCount() {
        return nGramCounts.values().iterator().next().rootChildrenCount();
    }

    public int sentencesRead() {
        return sentencesCount;
    }
}
