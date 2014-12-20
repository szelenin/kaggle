package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model implements Serializable, KryoSerializable {
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
        Scanner scanner = createScanner(sentence);
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

    private Scanner createScanner(String sentence) {
        Scanner scanner = new Scanner(sentence.toLowerCase());
        scanner.useDelimiter(delimiter);
        return scanner;
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

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeObject(output, nGramCounts);
        kryo.writeObject(output, sentencesCount);
        kryo.writeObject(output, totalWords);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        nGramCounts = kryo.readObject(input, NGramCounts.class);
        sentencesCount = kryo.readObject(input, int.class);
        totalWords = kryo.readObject(input, int.class);

    }

    public String predict(String sentence) {
        Scanner scanner = createScanner(sentence);
        int n = nGramCounts.getN();
        SentenceCounts sentenceCounts = new SentenceCounts(n);

        List<NGram> nGrams = new ArrayList<>();

        int currentWord = 0;

        for (int i = n; i > 0; i--) {
            NGram nGram = new NGram(i);
            nGram.newSentence();
            nGrams.add(nGram);
        }

        while (scanner.hasNext()) {
            String word = scanner.next();
            if (StringUtils.isBlank(word)) {
                continue;
            }
            for (NGram nGram : nGrams) {
                sentenceCounts.add(currentWord, word, nGram.getN(), nGramCounts.getCount(nGram.getWords()));
            }
            currentWord++;
        }
        int wordNumber = sentenceCounts.minLikelihoodWordNumber();
        sentenceCounts.getWordsBefore(wordNumber);
        return null;
    }
}
