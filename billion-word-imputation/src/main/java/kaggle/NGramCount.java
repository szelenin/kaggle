package kaggle;

import java.util.LinkedList;

/**
 * Created by szelenin on 12/10/2014.
 */
public class NGramCount {
    private Node rootNode = new Node();
    private LinkedList<String> words;
    private int n;

    public NGramCount(int n) {
        this.n = n;
    }

    public void put(String word) {
        words.removeFirst();
        words.addLast(word);
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
}
