package kaggle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by szelenin on 12/17/2014.
 */
public class NGram {
    private static final Logger logger = LogManager.getLogger(NGram.class);
    private int n;
    private transient LinkedList<String> words;

    public NGram(int n) {
        this.n = n;
    }

    public void newSentence() {
        words = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            words.add("*");
        }

    }

    public void put(String word) {
        words.removeFirst();
        words.addLast(word);
        logger.trace("NGram #{}:{}", n, word);
        assert words.size() == n;
    }

    public LinkedList<String> getWords() {
        return words;
    }

    public void finishSentence(Lambda<List<String>> lambda) {
        Iterator<String> iterator = this.words.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            lambda.invoke(words);
        }

    }

    public int getN() {
        return n;
    }
}
