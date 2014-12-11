package kaggle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szelenin on 12/11/2014.
 */
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 4412269356806072828L;
    private Map<String, Integer> dictionary = new HashMap<String, Integer>();

    private static Dictionary instance;
    static {
        instance = new Dictionary();
        instance.dictionary.put("*", -1);
        instance.dictionary.put("_stop_", -2);
    }

    public static Dictionary getInstance() {
        return instance;
    }

    public int putWord(String word) {
        Integer key = getWord(word);
        dictionary.put(word, key);
        return key;
    }

    public Integer getWord(String word) {
        Integer key = dictionary.get(word);
        if (key == null) {
            key = dictionary.size();
        }
        return key;
    }
}
