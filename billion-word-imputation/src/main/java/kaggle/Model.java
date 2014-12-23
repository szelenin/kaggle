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
import sun.plugin2.message.Message;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Model implements Serializable, KryoSerializable {
    private static final Logger logger = LogManager.getLogger(Model.class);

    private static final long serialVersionUID = 1648607850532636804L;
    private NGramCounts nGramCounts;
    private int sentencesCount = 0;
    private int totalWords = 0;
    private final Map<String, SentenceCounts> countsMap = new HashMap<>();

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

        updateNgramCounts(sentenceWords);
        int wordNumber = missedWordNumber(sentenceWords);
        String mostFrequentWord = missedWord(sentenceWords, wordNumber);

        Sentence sentence = new Sentence(sentenceWords).iterateWords(word -> {});
        sentence.putWord(mostFrequentWord, wordNumber);
        return sentence.toString();
    }

    public String missedWord(String sentenceWords, int wordNumber) {
        List<String> nGramBefore = countsFor(sentenceWords).getWordsBefore(wordNumber);
        List<String> wordsBeforeMissed = nGramBefore.subList(1, nGramBefore.size());
        String mostFrequentWord = nGramCounts.getMaxMostFrequentWordAfter(wordsBeforeMissed);
        logger.trace("Most frequent after {} : {}", wordsBeforeMissed, mostFrequentWord);
        return mostFrequentWord;
    }

    public int missedWordNumber(String sentenceWords) {
        int wordNumber = countsFor(sentenceWords).minLikelihoodWordNumber();
        logger.trace("Min likelihood word number {}", wordNumber);
        return wordNumber;
    }

    public void updateNgramCounts(String sentenceWords) {
        SentenceCounts sentenceCounts = countsFor(sentenceWords);

        Sentence sentence = new Sentence(sentenceWords);
        List<NGram> nGrams = createNGrams(nGramCounts.getN());

        updateSentenceCounts(sentenceCounts, nGrams, sentence);

        logger.trace("Sentence counts: {}", sentenceCounts);

    }

    private SentenceCounts countsFor(String sentence) {
        MessageDigest digest = digest();
        digest.update(sentence.getBytes());
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5Sum = bigInt.toString(Character.MAX_RADIX);
        SentenceCounts sentenceCounts = countsMap.get(md5Sum);
        if (sentenceCounts == null) {
            sentenceCounts = new SentenceCounts(nGramCounts.getN());
        }
        countsMap.put(md5Sum, sentenceCounts);
        return sentenceCounts;
    }

    private MessageDigest digest(){
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
