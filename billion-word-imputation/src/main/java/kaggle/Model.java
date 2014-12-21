package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Tuple;

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

    public void put(String sentenceWords) {
        logger.trace("Starting sentenceWords: {}", sentenceWords);

        nGramCounts.newSentence();
        Sentence sentence = new Sentence(sentenceWords);
        sentence.iterateWords(word -> {
            nGramCounts.put(word.getValue0());
            totalWords++;
        });
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

    public String predict(String sentenceWords) {
        logger.trace("Predict: {}", sentenceWords);
        int n = nGramCounts.getN();
        SentenceCounts sentenceCounts = new SentenceCounts(n);

        List<NGram> nGrams = createNGrams(n);

        Sentence sentence = new Sentence(sentenceWords);
        updateSentenceCounts(sentenceCounts, nGrams, sentence);
        logger.trace("Sentence counts: {}", sentenceCounts);

        int wordNumber = sentenceCounts.minLikelihoodWordNumber();
        logger.trace("Min likelihood word number {}", wordNumber);
        List<String> nGramBefore = sentenceCounts.getWordsBefore(wordNumber);

        List<String> wordsBeforeMissed = nGramBefore.subList(1, nGramBefore.size());
        String mostFrequentWord = nGramCounts.getMaxMostFrequentWordAfter(wordsBeforeMissed);
        logger.trace("Most frequent after {} : {}", wordsBeforeMissed, mostFrequentWord);
        sentence.putWord(mostFrequentWord, wordNumber);
        return sentence.toString();
    }

    private void updateSentenceCounts(SentenceCounts sentenceCounts, List<NGram> nGrams, Sentence sentence) {
        sentence.iterateWords(pair -> {
            for (NGram nGram : nGrams) {
                String word = pair.getValue0();
                nGram.put(word);
                sentenceCounts.add(pair.getValue1(), word, nGram.getN(), nGramCounts.getCount(nGram.getWords()));
            }
        });
    }

    private List<NGram> createNGrams(int n) {
        List<NGram> nGrams = new ArrayList<>();
        for (int i = n; i > 0; i--) {
            NGram nGram = new NGram(i);
            nGram.newSentence();
            nGrams.add(nGram);
        }
        return nGrams;
    }
}
