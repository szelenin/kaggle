package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by szelenin on 12/10/2014.
 */
public class NGramCounts implements Serializable, KryoSerializable {
    private static final Logger logger = LogManager.getLogger(NGramCounts.class);

    private static final long serialVersionUID = 1086329126464393222L;
    private Node rootNode = new Node();
    private transient LinkedList<String> words;
    private int n;

    public NGramCounts(int n) {
        this.n = n;
    }

    public void put(String word) {
        words.removeFirst();
        words.addLast(word);
        logger.trace("NGram #{}:{}", n, word);
        assert words.size() == n;
        rootNode.put(words);
    }

    public int getCount(String... wordSequence) {
        int sequenceCount = rootNode.getSequenceCount(wordSequence);
        logger.trace("NGram #{} {}:{}", n, wordSequence, sequenceCount);
        return sequenceCount;
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

    public void finishSentence() {
        Iterator<String> iterator = words.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            rootNode.put(words);
        }
    }

    public int getN() {
        return n;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeObject(output, rootNode);
        kryo.writeObject(output, n);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        rootNode = kryo.readObject(input, Node.class);
        n = kryo.readObject(input, int.class);
    }
}
