package kaggle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by szelenin on 12/10/2014.
 */
public class NGramCount implements Serializable {
    private static final Logger logger = LogManager.getLogger(NGramCount.class);

    private static final long serialVersionUID = 1086329126464393222L;
    private Node rootNode = new Node();
    private LinkedList<String> words;
    private int n;

    public NGramCount(int n) {
        this.n = n;
    }

    public void put(String word) {
        words.removeFirst();
        words.addLast(word);
        logger.trace("NGram #{} put word {}", n, word);
        assert words.size() == n;
        rootNode.put(words);
    }

    public int getCount(String... wordSequence) {
        return rootNode.getSequenceCount(wordSequence);
    }

    public void newSentence() {
        words = new LinkedList<String>();
        for (int i = 0; i < n; i++) {
            words.add("*");
        }
    }

    public int rootChildrenCount() {
        return rootNode.childrenCount();
    }
}
