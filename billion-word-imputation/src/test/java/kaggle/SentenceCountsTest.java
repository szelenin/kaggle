package kaggle;

import org.javatuples.Pair;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertThat(sentenceCount.getWordsBefore(1)).containsExactly("*", "*", "the");
    }

    @Test
    public void shouldUpdateMostFrequentWordWhenNull(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);

        assertNull(sentenceCounts.updateMostFrequentWord(null));
    }

    @Test
    public void shouldUpdateMostFrequentWordWhenFirstCall(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);

        assertEquals(new Pair<>("dog", 3), sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 3)));
    }

    @Test
    public void shouldUpdateMostFrequentWordWhenSecondCallHasLargerAmount(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);
        sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 1));

        assertEquals(new Pair<>("cat", 2), sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 2)));
    }

    @Test
    public void shouldUpdateMostFrequentWordWhenFirstCallHasLargerAmount(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);
        sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 2));

        assertEquals(new Pair<>("dog", 2), sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1)));
    }

    @Test
    public void shouldUpdateMostFrequentWordWhenFirstCallHasLargerAmount2(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);
        sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 2));

        assertEquals(new Pair<>("dog", 3), sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 1)));
    }

    @Test
    public void shouldUpdateMostFrequentWordIncremetally(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);
        sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1));
        sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1));

        assertEquals(new Pair<>("cat", 3), sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1)));
    }

    @Test
    public void shouldUpdateMostFrequentWordIncremetallyDifferentWords(){
        SentenceCounts sentenceCounts = new SentenceCounts(3);
        sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1));
        sentenceCounts.updateMostFrequentWord(new Pair<>("cat", 1));

        assertEquals(new Pair<>("cat", 2), sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 1)));
        assertEquals(new Pair<>("dog", 3), sentenceCounts.updateMostFrequentWord(new Pair<>("dog", 2)));
    }

    @Test
    public void shouldCalcLikelihoodWhenOneWord() {
        SentenceCounts sentenceCount = new SentenceCounts(3);

        assertEquals(0, sentenceCount.minLikelihoodWordNumber());
        assertThat(sentenceCount.getWordsBefore(0)).containsExactly("*", "*", "*");
    }

    private void addWord(SentenceCounts sentenceCount, int position, String word, int... nGramCounts) {
        for (int i = 0; i < nGramCounts.length; i++) {
            int count = nGramCounts[i];
            sentenceCount.add(position, word, i + 1, count);
        }

    }

}