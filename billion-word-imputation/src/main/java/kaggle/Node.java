package kaggle;

import java.io.Serializable;
import java.util.*;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 9073428157928387951L;
    private Map<String, Node> children = new HashMap<String, Node>();
    private int count = 0;

    public void put(String ... sentence) {
        put(Arrays.asList(sentence));
    }


    public void put(List<String> words) {
        Node node = this;
        for (String word : words) {
            Node child = node.children.get(word);
            if (child == null) {
                child = new Node();
            }
            child.count++;
            node.children.put(word, child);
            node = child;
        }
    }

    public int getSequenceCount(String... wordSequence) {
        Node node = this;
        for (String word : wordSequence) {
            Node child = node.children.get(word.toLowerCase());
            if (child == null) {
                return 0;
            }
            node = child;
        }
        return node.count;
    }

    public int childrenCount() {
        return children.size();
    }
}
