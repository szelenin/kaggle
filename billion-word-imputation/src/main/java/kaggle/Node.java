package kaggle;

import java.io.Serializable;
import java.util.*;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 9073428157928387951L;
    private Map<String, Node> children;
    private int count = 0;

    public void put(List<String> words) {
        if (children == null) {
            children = new HashMap<String, Node>();
        }
        Node node = this;
        for (String word : words) {
            node = node.addChild(word);
        }
    }

    private Node addChild(String word) {
        Node child = getChild(word);
        if (child == null) {
            child = new Node();
        }
        child.count++;
        if (children == null) {
            children = new HashMap<String, Node>();
        }
        children.put(word, child);
        return child;
    }

    private Node getChild(String word) {
        if (children == null) {
            return null;
        }
        return children.get(word);
    }

    public int getSequenceCount(String... wordSequence) {
        Node node = this;
        for (String word : wordSequence) {
            Node child = node.children == null ? null : node.children.get(word.toLowerCase());
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
