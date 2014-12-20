package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NGramCountsTest {
    @Test
    public void shouldGetMaxWord() {
        NGramCounts nGramCounts = new NGramCounts(3);

        addSentence(nGramCounts, "the", "dog", "barks");
        addSentence(nGramCounts, "the", "dog", "eats");

        assertEquals("dog", nGramCounts.getMaxMostFrequentWordAfter("*", "the"));
    }

    private void addSentence(NGramCounts nGramCounts, String ... words) {
        nGramCounts.newSentence();
        for (String word : words) {
            nGramCounts.put(word);
        }
        nGramCounts.finishSentence();
    }
}