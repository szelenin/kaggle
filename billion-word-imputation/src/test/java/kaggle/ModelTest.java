package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by szelenin on 12/9/2014.
 */
public class ModelTest {

    @Test
    public void shouldInsertFromSentence() {
        Model model = new Model();
        model.put("the dog likes the cat");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("the", "cat"));
    }

    @Test
    public void shouldInsertFromSentenceSpaces() {
        Model model = new Model();
        model.put("the   dog  likes the   cat");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("the", "cat"));
    }

    @Test
    public void shouldCountWhenFirstWordRequested() {
        Model model = new Model();
        model.put("the dog likes the cat");

        //* will be a special symbol which denotes beginning of sentence
        assertEquals(1, model.count("*", "the"));
    }

    @Test
    public void shouldCountCaseInsensitive() {
        Model model = new Model();
        model.put("ThE doG Likes tHe Cat");

        assertEquals(2, model.count("thE"));
        assertEquals(1, model.count("The", "dOg"));
        assertEquals(1, model.count("THe", "cAt"));
    }

    @Test
    public void shouldCountStopWord() {
        Model model = new Model();
        model.put("the dog likes the cat");

        assertEquals(1, model.count("cat", "_stop_"));
    }

    @Test
    public void shouldSkipNonWords() {
        Model model = new Model();
        model.put("the-dog, likes (the) # cat.");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("*", "the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("dog", "likes"));
        assertEquals(1, model.count("likes", "the"));
        assertEquals(1, model.count("the", "cat"));
        assertEquals(1, model.count("cat", "_stop_"));
    }

    @Test
    public void shouldSupportNgrams() {
        Model model = new Model(3);
        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(2, model.count("*", "*", "the"));
        assertEquals(1, model.count("*", "the", "dog"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }

    @Test
    public void shouldPutSeveralLines() {
        Model model = new Model(2);
        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(2, model.count("*", "the"));
        assertEquals(3, model.count("the", "cat"));
    }

    @Test
    public void shouldCountSeveralNGramsAtOnce() {
        Model model = new Model(2, 3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(3, model.count("the", "cat"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }

    @Test
    public void shouldProvideStatistic(){
        Model model = new Model(2, 3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(5, model.uniqueWordsCount());
        assertEquals(2, model.sentencesRead());
    }
}
