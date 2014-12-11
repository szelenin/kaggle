package kaggle;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by szelenin on 12/9/2014.
 */
public class DictionaryModelTest {

    @Test
    public void shouldInsertFromSentence() {
        DictionaryModel model = new DictionaryModel();
        model.put("the dog likes the cat");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("the", "cat"));
    }

    @Test
    public void shouldInsertFromSentenceSpaces() {
        DictionaryModel model = new DictionaryModel();
        model.put("the   dog  likes the   cat");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("the", "cat"));
    }

    @Test
    public void shouldCountWhenFirstWordRequested() {
        DictionaryModel model = new DictionaryModel();
        model.put("the dog likes the cat");

        //* will be a special symbol which denotes beginning of sentence
        assertEquals(1, model.count("*", "the"));
    }

    @Test
    public void shouldCountCaseInsensitive() {
        DictionaryModel model = new DictionaryModel();
        model.put("ThE doG Likes tHe Cat");

        assertEquals(2, model.count("thE"));
        assertEquals(1, model.count("The", "dOg"));
        assertEquals(1, model.count("THe", "cAt"));
    }

    @Test
    public void shouldCountStopWord() {
        DictionaryModel model = new DictionaryModel();
        model.put("the dog likes the cat");
        model.put("the cow hates the cat");

        assertEquals(2, model.count("cat", "_stop_"));
    }

    @Test
    public void shouldSkipNonWords() {
        DictionaryModel model = new DictionaryModel();
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
        DictionaryModel model = new DictionaryModel(3);
        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(2, model.count("*", "*", "the"));
        assertEquals(1, model.count("*", "the", "dog"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }

    @Test
    public void shouldPutSeveralLines() {
        DictionaryModel model = new DictionaryModel(2);
        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(2, model.count("*", "the"));
        assertEquals(3, model.count("the", "cat"));
    }

    @Test
    public void shouldCountSeveralNGramsAtOnce() {
        DictionaryModel model = new DictionaryModel(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(2, model.count("cat", "_stop_"));
        assertEquals(2, model.count("*", "the"));
        assertEquals(2, model.count("*", "*", "the"));
        assertEquals(2, model.count("the", "cat", "_stop_"));
        assertEquals(4, model.count("the"));
        assertEquals(3, model.count("the", "cat"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }

    @Test
    public void shouldProvideStatistic(){
        DictionaryModel model = new DictionaryModel(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(5, model.uniqueWordsCount());
        assertEquals(2, model.sentencesRead());
        assertEquals(5 + 5, model.totalWords());
    }

    @Test
    public void shouldCalcUnigramOnly(){
        DictionaryModel model = new DictionaryModel(1);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(5, model.uniqueWordsCount());
        assertEquals(4, model.count("the"));
        assertEquals(3, model.count("cat"));
        assertEquals(0, model.count("the", "cat"));
    }

    @Test
    public void shouldSerializeModel() throws IOException, ClassNotFoundException {
        DictionaryModel model = new DictionaryModel(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteArrayOut);
        objectOut.writeObject(model);

        DictionaryModel readModel = (DictionaryModel) new ObjectInputStream(new ByteArrayInputStream(byteArrayOut.toByteArray())).readObject();

        assertEquals(5, readModel.uniqueWordsCount());
        assertEquals(2, readModel.sentencesRead());
        assertEquals(3, model.count("the", "cat"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }
}
