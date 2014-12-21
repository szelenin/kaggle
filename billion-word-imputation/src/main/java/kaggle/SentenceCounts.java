package kaggle;

import java.util.*;

/**
 * Created by szelenin on 12/17/2014.
 */
public class SentenceCounts {
    private int maxNgramLength;

    private Map<Integer, WordCount> wordCounts = new LinkedHashMap<>();
    public SentenceCounts(int maxNgramLength) {
        assert maxNgramLength >= 2;
        this.maxNgramLength = maxNgramLength;
    }


    public int minLikelihoodWordNumber() {
        int minLikelihoodWordNumber = 0;
        double minLogLikelihood = Double.MAX_VALUE;
        int counter = 0;
        for (WordCount wordCount : wordCounts.values()) {
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
        WordCount wordCount = wordCounts.get(position);
        if (wordCount == null) {
            wordCount = new WordCount(word, maxNgramLength);
        }
        wordCount.incrementCount(n, count);
        wordCounts.put(position, wordCount);
    }

    public List<String> getWordsBefore(int wordNumber) {
        LinkedList<String> result = new LinkedList<>();
        int count = maxNgramLength;
        for (int i = wordNumber - 1; i >= 0 & count-- > 0; i--) {
            result.addFirst(wordCounts.get(i).word);
        }
        for (int i = count; i >= 0; i--) {
            result.addFirst("*");
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        wordCounts.entrySet().forEach(entry -> {
            WordCount count = entry.getValue();
            sb.append(count).append(" | ");
        });
        return sb.toString();
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
