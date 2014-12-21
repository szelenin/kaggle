package kaggle;

import org.junit.Test;

import java.util.LinkedList;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


public class SentenceTest {
    @Test
    public void shouldIgnoreNonWords(){
        Sentence sentence = new Sentence("the-dog, likes !(the) # cat.");
        LinkedList<String> words = new LinkedList<>();
        sentence.iterateWords(word->words.addLast(word.getValue0()));

        assertThat(words).containsExactly("the","dog","likes","the","cat");
        assertEquals(5, sentence.wordsCount());
    }

    @Test
    public void shouldIterateOverLowercasedWords(){
        Sentence sentence = new Sentence("the-dog, likes !(the) # cat.");
        LinkedList<String> words = new LinkedList<>();
        sentence.iterateWords(word->words.addLast(word.getValue0()));

        assertThat(words).containsExactly("the","dog","likes","the","cat");
        assertEquals(5, sentence.wordsCount());
    }

    @Test
    public void shouldReconstructSentenceWords() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes");
        sentence.iterateWords(pair->{});

        assertEquals("The,dog . Put!some cakes", sentence.toString());
        assertEquals(5, sentence.wordsCount());
    }

    @Test
    public void shouldReconstructSentenceWordsWithEndingDot() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes .");
        sentence.iterateWords(pair->{});

        assertEquals("The,dog . Put!some cakes .", sentence.toString());
    }

    @Test
    public void shouldInsertWord() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes");
        sentence.iterateWords(pair->{});

        sentence.putWord("beautiful", 4);

        assertEquals("The,dog . Put!some beautiful cakes", sentence.toString());
        assertEquals(6, sentence.wordsCount());
    }

    @Test
    public void shouldInsertWordAtTheBeginning() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes");
        sentence.iterateWords(pair->{});

        sentence.putWord("beautiful", 0);

        assertEquals("beautiful The,dog . Put!some cakes", sentence.toString());
        assertEquals(6, sentence.wordsCount());
    }

    @Test
    public void shouldInsertWordAtTheEnd() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes");
        sentence.iterateWords(pair->{});

        sentence.putWord("beautiful", 5);

        assertEquals("The,dog . Put!some cakes beautiful", sentence.toString());
    }

    @Test
    public void shouldInsertWordAtTheEndWithPunctuation() {
        Sentence sentence = new Sentence("The,dog . Put!some cakes .");
        sentence.iterateWords(pair->{});

        sentence.putWord("beautiful", 5);

        assertEquals("The,dog . Put!some cakes beautiful .", sentence.toString());
    }

    @Test
    public void shouldRemoveWord(){
        Sentence sentence = new Sentence("The,dog . Put!some cakes .");
        sentence.iterateWords(tmp->{});

        String removedWord = sentence.removeWord(2);

        assertEquals("Put", removedWord);
        assertEquals("The,dog!some cakes .", sentence.toString());
    }

    @Test
    public void should(){
        Sentence sentence = new Sentence("The gender wars are important but take a back seat to survival .");
        sentence.iterateWords(word->{});

        assertEquals(12, sentence.wordsCount());
    }
}