package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by szelenin on 12/10/2014.
 */
public class NGramCounts implements Serializable, KryoSerializable {
    private static final Logger logger = LogManager.getLogger(NGramCounts.class);

    private static final long serialVersionUID = 1086329126464393222L;
    private transient final NGram nGram;
    private Node rootNode = new Node();
    private int n;

    public NGramCounts(int n) {
        this.n = n;
        nGram = new NGram(n);
    }

    public void put(String word) {
        nGram.put(word);
        rootNode.put(nGram.getWords());
    }

    public int getCount(List<String> wordSequence) {
        int sequenceCount = rootNode.getSequenceCount(wordSequence);
        logger.trace("NGram #{} {}:{}", n, wordSequence, sequenceCount);
        return sequenceCount;
    }

    public int getCount(String... wordSequence) {
        return getCount(Arrays.asList(wordSequence));
    }

    public void newSentence() {
        nGram.newSentence();
    }

    public int rootChildrenCount() {
        return rootNode.childrenCount();
    }

    public void finishSentence() {
        nGram.finishSentence((List<String> words) -> rootNode.put(words));
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

    public String getMaxMostFrequentWordAfter(List<String> wordSequence) {
        if (wordSequence.size() >= n) {
            throw new IllegalArgumentException("Count of words in sequence should be maximum n-1");
        }
        return rootNode.getNode(wordSequence).mostFrequentChild();
    }

    public String getMaxMostFrequentWordAfter(String... wordSequence) {
        return getMaxMostFrequentWordAfter(Arrays.asList(wordSequence));
    }
}
