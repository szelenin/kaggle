package kaggle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by szelenin on 12/9/2014.
 */
public class ModelTest {

    @Test
    public void shouldInsertFromSentence(){
        Model model = new Model();
        model.put("the dog likes the cat");

        assertEquals(2, model.count("the"));
        assertEquals(1, model.count("the", "dog"));
        assertEquals(1, model.count("the", "cat"));
    }
}
