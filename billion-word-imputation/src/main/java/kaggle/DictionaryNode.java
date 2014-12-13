package kaggle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by szelenin on 12/9/2014.
 */
public class DictionaryNode implements Serializable {

    private static final long serialVersionUID = 9073428157928387951L;
    private Map<Integer, DictionaryNode> children = new HashMap<Integer, DictionaryNode>();
    private int count = 0;

    public void put(List<Integer> words) {
        DictionaryNode node = this;
        for (Integer word : words) {
            DictionaryNode child = (DictionaryNode) node.children.get(word);
            if (child == null) {
                child = new DictionaryNode();
            }
            child.count++;
            node.children.put(word, child);
            node = child;
        }
    }

    public int getSequenceCount(String... wordSequence) {
        DictionaryNode node = this;
        for (String word : wordSequence) {
            DictionaryNode child = node.children.get(Dictionary.getInstance().getWord(word.toLowerCase()));
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
