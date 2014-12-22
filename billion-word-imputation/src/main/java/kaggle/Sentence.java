package kaggle;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/21/2014.
 */
public class Sentence {
    private String sentenceWords;
    private static final Pattern delimiter = Pattern.compile("\\s+|\\W");
    private static final Pattern wordPattern = Pattern.compile("(\\w+)");
    private LinkedList<Pair<String, Integer>> words;
    private int wordsCount;

    public Sentence(String sentenceWords) {

        this.sentenceWords = sentenceWords;
    }

    public Sentence iterateWords(Lambda<Pair<String, Integer>> lambda) {
        Matcher matcher = wordPattern.matcher(sentenceWords);
        words = new LinkedList<>();

        wordsCount = 0;
        int previousRegionStart = 0;
        while (matcher.find()) {
            String delimiter = sentenceWords.substring(previousRegionStart, matcher.start());
            if (!StringUtils.isEmpty(delimiter)) {
                words.add(new Pair<>(delimiter, -1));
            }
            String word = matcher.group();
            Pair<String, Integer> pair = new Pair<>(word, wordsCount);
            words.add(pair);
            lambda.invoke(pair.setAt0(word.toLowerCase()));
            previousRegionStart = matcher.end();
            wordsCount++;
        }
        String ending = sentenceWords.substring(previousRegionStart, sentenceWords.length());
        if (!StringUtils.isEmpty(ending)) {
            words.add(new Pair<>(ending, -1));
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        words.forEach(pair -> sb.append(pair.getValue0()));
        return sb.toString();
    }

    public void putWord(String word, int position) {
        ListIterator<Pair<String, Integer>> iterator = words.listIterator();
        boolean wordInserted = insertWordInMiddle(word, position, iterator);
        if (!wordInserted) {
            insertWordAtTheEnd(word, iterator);
        }
    }

    private void insertWordAtTheEnd(String word, ListIterator<Pair<String, Integer>> iterator) {
        while (iterator.hasPrevious()) {
            Pair<String, Integer> previous = iterator.previous();
            if (previous.getValue1() >= 0) {
                iterator.next();
                iterator.add(new Pair<>(" ", -1));
                iterator.add(new Pair<>(word, previous.getValue1() + 1));
                break;
            }
        }
    }

    private boolean insertWordInMiddle(String word, int position, ListIterator<Pair<String, Integer>> iterator) {
        boolean foundWord = false;
        while (iterator.hasNext()) {
            Pair<String, Integer> pair = iterator.next();
            if (foundWord && pair.getValue1() >= 0) {
                Pair<String, Integer> updatedPair = pair.setAt1(pair.getValue1() + 1);
                iterator.set(updatedPair);
                continue;
            }
            if (position == pair.getValue1()) {
                foundWord = true;
                iterator.previous();
                iterator.add(new Pair<>(word, position));
                iterator.add(new Pair<>(" ", -1));
            }
        }
        wordsCount++;
        return foundWord;
    }

    public int wordsCount() {
        return wordsCount;
    }

    public String removeWord(int position) {
        ListIterator<Pair<String, Integer>> iterator = words.listIterator();
        String removedWord = null;
        while (iterator.hasNext()) {
            Pair<String, Integer> pair = iterator.next();
            if (pair.getValue1() == position) {
                removedWord = pair.getValue0();
                iterator.remove();
                if (iterator.hasPrevious()) {
                    iterator.previous();
                    iterator.remove();
                }
                wordsCount--;
                break;
            }

        }
        return removedWord;
    }
}
