package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SentenceCountsTest {
    @Test
    public void shouldCalcMissedWordNumberWhenOneWord() {
        SentenceCounts sentenceCount = new SentenceCounts(3);

        addWord(sentenceCount, 0, "the", 1, 0, 0);

        assertEquals(0, sentenceCount.minLikelihoodWordNumber());
    }

    @Test
    public void shouldCalcMissedWordNumberWhenOneWordByBigramInfo() {
        SentenceCounts sentenceCount = new SentenceCounts(2);

        addWord(sentenceCount, 0, "the", 1, 2);
        addWord(sentenceCount, 1, "dog", 1, 1);
        addWord(sentenceCount, 2, "hates", 1, 0);

        assertEquals(2, sentenceCount.minLikelihoodWordNumber());
    }

    @Test
    public void shouldPutWordWithNgramCount() {
        // --- learning with:
        // "the dog likes the cat"
        // "the cat hates the dog"
        // --- predicting:
        // the hates the dog
        SentenceCounts sentenceCount = new SentenceCounts(3);

        addWord(sentenceCount, 0, "the", 4, 2, 2);
        addWord(sentenceCount, 1, "hates", 1, 0, 0);
        addWord(sentenceCount, 2, "the", 4, 1, 0);
        addWord(sentenceCount, 3, "dog", 2, 2, 0);

        assertEquals(1, sentenceCount.minLikelihoodWordNumber());
    }

    private void addWord(SentenceCounts sentenceCount, int position, String word, int... nGramCounts) {
        for (int i = 0; i < nGramCounts.length; i++) {
            int count = nGramCounts[i];
            sentenceCount.add(position, word, i + 1, count);
        }

    }

}