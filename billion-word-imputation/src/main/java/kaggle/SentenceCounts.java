package kaggle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by szelenin on 12/17/2014.
 */
public class SentenceCounts {
    private int maxNgramLength;
    private Map<Integer, WordCount> wordCounts = new LinkedHashMap<>();


    public SentenceCounts(int maxNgramLength) {
        assert maxNgramLength >=2;
        this.maxNgramLength = maxNgramLength;
    }

    public int minLikelihoodWordNumber() {
        int minLikelihoodWordNumber = 0;
        double minLogLikelihood = Double.MAX_VALUE;
        int counter = 0;
        for (WordCount wordCount : wordCounts.values()) {
            double logLikelihood = wordCount.nGramLogLikelihood(maxNgramLength);
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
            return Math.log(nGramCounts[n - 1] / nGramCounts[n - 2]);
        }
    }
}
