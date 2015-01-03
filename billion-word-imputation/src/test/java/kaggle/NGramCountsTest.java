package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NGramCountsTest {

    @Test
    public void shouldGetMaxWordWithCount() {
        NGramCounts nGramCounts = new NGramCounts(3);

        addSentence(nGramCounts, "the", "dog", "barks");
        addSentence(nGramCounts, "the", "dog", "eats");

        assertEquals(2, nGramCounts.getMaxMostFrequentWordWithCountAfter("*", "the").getValue1().intValue());
        assertEquals("dog", nGramCounts.getMaxMostFrequentWordWithCountAfter("*", "the").getValue0());
    }

    @Test
    public void shouldGetMaxWordOutOfBounds() {
        NGramCounts nGramCounts = new NGramCounts(3);

        addSentence(nGramCounts, "the", "dog", "barks");
        addSentence(nGramCounts, "the", "dog", "eats");

        try {
            nGramCounts.getMaxMostFrequentWordWithCountAfter("*", "the", "dog");
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