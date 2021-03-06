package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by szelenin on 12/9/2014.
 */
public class ModelTest {

    private Kryo kryo;

    @Before
    public void setUp() throws Exception {
        kryo = new Kryo();
    }

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
        model.put("the cow hates the cat");

        assertEquals(2, model.count("cat", "_stop_"));
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
        Model model = new Model(3);

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
        Model model = new Model(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(5, model.uniqueWordsCount());
        assertEquals(2, model.sentencesRead());
        assertEquals(5 + 5, model.totalWords());
    }

    @Test
    public void shouldCalcUnigramOnly(){
        Model model = new Model(1);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        assertEquals(5, model.uniqueWordsCount());
        assertEquals(4, model.count("the"));
        assertEquals(3, model.count("cat"));
        assertEquals(0, model.count("the", "cat"));
    }

    @Test
    public void shouldSerializeModel() throws IOException, ClassNotFoundException {
        Model model = new Model(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteArrayOut);
        objectOut.writeObject(model);

        Model readModel = (Model) new ObjectInputStream(new ByteArrayInputStream(byteArrayOut.toByteArray())).readObject();

        assertEquals(5, readModel.uniqueWordsCount());
        assertEquals(2, readModel.sentencesRead());
        assertEquals(3, model.count("the", "cat"));
        assertEquals(2, model.count("likes", "the", "cat"));
    }

    @Test
    public void shouldSerializeModelWithKryo() throws IOException, ClassNotFoundException {
        Model model = new Model(3);

        model.put("the dog likes the cat");
        model.put("the cat likes the cat");

        ByteArrayOutputStream byteArrayOut = writeModel(model);
        Model readModel = new Model();
        readModel(byteArrayOut, readModel);

        assertEquals(5, readModel.uniqueWordsCount());
        assertEquals(2, readModel.sentencesRead());
        assertEquals(3, model.count("the", "cat"));
        assertEquals(2, model.count("likes", "the", "cat"));

    }

    @Test
    public void shouldPredict(){
        Model model = new Model(3);
        model.put("the dog likes the cat");
        model.put("the cat hates the dog");

        assertEquals("the cat hates the dog", model.predict("the hates the dog"));
    }

    @Test
    public void shouldCalcMissedWordNumberWhenIncrementedModelLoads(){
        ByteArrayOutputStream model1Out = createAndWriteModel("the cat hates the dog");
        ByteArrayOutputStream model2Out = createAndWriteModel("a dog likes the cat");

        Model model = new Model();

        // updating ngram counts
        readModel(model1Out, model);
        int sentenceNo = model.updateNgramCounts("a hates the dog");
        int missedWordNumber = model.missedWordNumber(sentenceNo);
        assertEquals(0, missedWordNumber);

        readModel(model2Out, model);
        sentenceNo = model.updateNgramCounts("a hates the dog");
        missedWordNumber = model.missedWordNumber(sentenceNo);
        assertEquals(1, missedWordNumber);
        assertEquals(1, model.getSentenceCounts());

        // predicting missing word
        readModel(model1Out, model);
        assertEquals(missedWordNumber, model.missedWordNumber(0));
        assertEquals("", model.missedWord(0, missedWordNumber));

        readModel(model2Out, model);
        assertEquals(missedWordNumber, model.missedWordNumber(0));
//        //this is initial implementation - should be improved to "cat hates"
        assertEquals("dog", model.missedWord(0, missedWordNumber));
    }

    @Test
    public void shouldImproveWordPredictionAfterModelLoad(){
        ByteArrayOutputStream model1Out = createAndWriteModel("the cat loves mouse", "the cat loves cheese");
        ByteArrayOutputStream model2Out = createAndWriteModel("the dog loves the cat");

        Model model = new Model();
        readModel(model1Out, model);
        assertMissedWord(model, "cat", "the loves");

        readModel(model2Out, model);
        assertMissedWord(model, "cat", "the loves");
    }

    private void assertMissedWord(Model model, String expectedMissedWord, String sentenceWords) {
        int sentenceNo = model.updateNgramCounts(sentenceWords);
        int missedWordNumber = model.missedWordNumber(sentenceNo);
        assertEquals(expectedMissedWord, model.missedWord(sentenceNo, missedWordNumber));
    }

    private ByteArrayOutputStream createAndWriteModel(String ... sentences) {
        Model model = new Model(3);
        for (String sentence : sentences) {
            model.put(sentence);
        }
        return writeModel(model);
    }

    @Test
    public void shouldCalcMissedWordNumberWhenSeveralSentencesToPredict(){
        Model model = new Model(3);
        model.put("the dog likes the cat");
        model.put("the cat hates the big dog");

        int sentenceNo = model.updateNgramCounts("the hates the dog");
        int missedWordNumber = model.missedWordNumber(sentenceNo);
        assertEquals(1, missedWordNumber);
        assertEquals("cat", model.missedWord(sentenceNo, missedWordNumber));

        sentenceNo = model.updateNgramCounts("the dog the cat");
        missedWordNumber = model.missedWordNumber(sentenceNo);
        assertEquals(2, missedWordNumber);
        assertEquals("likes", model.missedWord(sentenceNo, missedWordNumber));
    }

    private void readModel(ByteArrayOutputStream byteArrayOut, Model model) {
        Input input = new Input(new ByteArrayInputStream(byteArrayOut.toByteArray()));
        model.read(kryo, input);
        input.close();
    }

    private ByteArrayOutputStream writeModel(Model model) {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOut);
        model.write(kryo, output);
        output.close();
        return byteArrayOut;
    }

}
