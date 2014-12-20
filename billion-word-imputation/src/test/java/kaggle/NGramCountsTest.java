package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NGramCountsTest {
    @Test
    public void shouldGetMaxWord() {
        NGramCounts nGramCounts = new NGramCounts(3);

        addSentence(nGramCounts, "the", "dog", "barks");
        addSentence(nGramCounts, "the", "dog", "eats");

        assertEquals("dog", nGramCounts.getMaxMostFrequentWordAfter("*", "the"));
    }

    @Test
    public void shouldGetMaxWordOutOfBounds() {
        NGramCounts nGramCounts = new NGramCounts(3);

        addSentence(nGramCounts, "the", "dog", "barks");
        addSentence(nGramCounts, "the", "dog", "eats");

        try {
            nGramCounts.getMaxMostFrequentWordAfter("*", "the", "dog");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    private void addSentence(NGramCounts nGramCounts, String ... words) {
        nGramCounts.newSentence();
        for (String word : words) {
            nGramCounts.put(word);
        }
        nGramCounts.finishSentence();
    }
}