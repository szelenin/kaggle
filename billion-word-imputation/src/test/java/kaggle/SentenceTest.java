package kaggle;

import org.junit.Test;

import java.util.LinkedList;

import static org.fest.assertions.Assertions.assertThat;


public class SentenceTest {
    @Test
    public void shouldIgnoreNonWords(){
        Sentence sentence = new Sentence("the-dog, likes !(the) # cat.");
        LinkedList<String> words = new LinkedList<>();
        sentence.iterateWords(word->words.addLast(word.getValue0()));

        assertThat(words).containsExactly("the","dog","likes","the","cat");
    }

    @Test
    public void shouldIterateOverLowercasedWords(){
        Sentence sentence = new Sentence("the-dog, likes !(the) # cat.");
        LinkedList<String> words = new LinkedList<>();
        sentence.iterateWords(word->words.addLast(word.getValue0()));

        assertThat(words).containsExactly("the","dog","likes","the","cat");
    }
}