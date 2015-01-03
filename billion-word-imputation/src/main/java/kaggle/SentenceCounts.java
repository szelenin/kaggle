package kaggle;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import org.javatuples.Pair;

import java.util.*;

/**
 * Created by szelenin on 12/17/2014.
 */
public class SentenceCounts {
    private int maxNgramLength;

    private WordCount[] wordCounts = new WordCount[1];
    private LinkedList<Pair<String, Integer>> mostFrequentWords = new LinkedList<>();

    public SentenceCounts(int maxNgramLength) {
        assert maxNgramLength >= 2;
        this.maxNgramLength = maxNgramLength;
    }


    public int minLikelihoodWordNumber() {
        int minLikelihoodWordNumber = 0;
        double minLogLikelihood = Double.MAX_VALUE;
        int counter = 0;
        for (WordCount wordCount : wordCounts) {
            if (wordCount == null) {
                return 0;
            }
            double logLikelihood = wordCount.weighedNGramLogLikelihood(maxNgramLength);
            if (logLikelihood < minLogLikelihood) {
                minLogLikelihood = logLikelihood;
                minLikelihoodWordNumber = counter;
            }
            counter++;
        }

        return minLikelihoodWordNumber;
    }

    public void add(int position, String word, int n, int count) {
        if (wordCounts.length < position + 1) {
            WordCount[] newWordCounts = new WordCount[wordCounts.length + 1];
            System.arraycopy(wordCounts, 0, newWordCounts, 0, wordCounts.length);
            wordCounts = newWordCounts;
        }
        WordCount wordCount = wordCounts[position];
        if (wordCount == null) {
            wordCount = new WordCount(word, maxNgramLength);
        }
        wordCount.incrementCount(n, count);
        wordCounts[position] = wordCount;
    }

    public List<String> getWordsBefore(int wordNumber) {
        LinkedList<String> result = new LinkedList<>();
        int count = maxNgramLength;
        for (int i = wordNumber - 1; i >= 0 & count-- > 0; i--) {
            result.addFirst(wordCounts[i].word);
        }
        for (int i = count; i >= 0; i--) {
            result.addFirst("*");
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (WordCount wordCount : wordCounts) {
            sb.append(wordCount).append(" | ");
        }
        return sb.toString();
    }

    public Pair<String, Integer> updateMostFrequentWord(Pair<String, Integer> mostFrequentWord) {
        if (mostFrequentWord == null) {
            return null;
        }

        ListIterator<Pair<String, Integer>> iterator = mostFrequentWords.listIterator();
        boolean found = false;
        int maxCount = 0;
        Pair<String, Integer> result = mostFrequentWord;
        while (iterator.hasNext()) {
            Pair<String, Integer> next = iterator.next();
            if (next.getValue0().equals(mostFrequentWord.getValue0())) {
                next = next.setAt1(mostFrequentWord.getValue1() + next.getValue1());
                iterator.set(next);
                result = next;
                found = true;
            }
            if (next.getValue1() > maxCount) {
                maxCount = next.getValue1();
                result = next;
            }
        }
        if (!found) {
            mostFrequentWords.addLast(mostFrequentWord);
            if (mostFrequentWord.getValue1() > maxCount) {
                return mostFrequentWord;
            }
        }
        return result;
    }

    private class WordCount {
        private String word;
        private int[] nGramCounts;

        private WordCount(String word, int n) {
            this.word = word;
            nGramCounts = new int[n];

        }

        public void incrementCount(int n, int count) {
            nGramCounts[n - 1] += count;
        }

        public double nGramLogLikelihood(int n) {
            if (nGramCounts[n - 1] == 0 || nGramCounts[n - 2] == 0) {
                return -Double.MAX_VALUE;
            }
            return Math.log((double) nGramCounts[n - 1] / nGramCounts[n - 2]);
        }

        public double weighedNGramLogLikelihood(int n) {
            double weighedNGramLogLikelihood = 0;
            for (int i = n; i >= 2; i--) {
                weighedNGramLogLikelihood += nGramLogLikelihood(i);
            }
            return weighedNGramLogLikelihood / (maxNgramLength - 1);
        }

        @Override
        public String toString() {
            return word + " : " + Arrays.toString(nGramCounts);
        }
    }
}
