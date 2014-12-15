package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.Serializable;
import java.util.*;

/**
 * Created by szelenin on 12/9/2014.
 */
public class Node implements Serializable, KryoSerializable {

    private static final long serialVersionUID = 9073428157928387951L;
    private Map<String, Node> children;
    private Integer count;

    public void put(List<String> words) {
        Node node = this;
        for (String word : words) {
            node = node.addChild(word.intern());
        }
    }

    private Node addChild(String word) {
        Node child = getChild(word);
        if (child == null) {
            child = new Node();
            child.count = 0;
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

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeObjectOrNull(output, children, HashMap.class);
        kryo.writeObjectOrNull(output, count, int.class);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        children = kryo.readObjectOrNull(input, HashMap.class);
        count = kryo.readObjectOrNull(input, int.class);
    }
}
