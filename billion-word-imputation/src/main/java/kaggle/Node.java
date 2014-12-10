package kaggle;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Node {
    private Map<String, Node> children = new HashMap<String, Node>();
    private int count = 0;

    public void put(String ... sentence) {
        Node node = this;
        for (String word : sentence) {
            Node child = node.children.get(word);
            if (child == null) {
                child = new Node();
            }
            child.count ++;
            node.children.put(word, child);
            node = child;
        }
    }


    public int getSequenceCount(String... wordSequence) {
        Node node = this;
        for (String word : wordSequence) {
            Node child = node.children.get(word);
            if (child == null) {
                return 0;
            }
            node = child;
        }
        return node.count;
    }
}
