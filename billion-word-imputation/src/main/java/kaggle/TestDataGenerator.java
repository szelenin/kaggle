package kaggle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by szelenin on 12/21/2014.
 */
public class TestDataGenerator {
    private static final Logger logger = LogManager.getLogger(TestDataGenerator.class);

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 12; i++) {
            BufferedReader reader = createSourceReader(i);
            PrintWriter sentenceWriter = createOutWriter(i, "test");
            PrintWriter removedWriter = createOutWriter(i, "removed");
            String line = reader.readLine();
            while (line != null) {
                Sentence sentence = new Sentence(line);
                sentence.iterateWords();
                logger.trace("Sentence: {} | {}", sentence, sentence.wordsCount());

                int randomWordNumber;
                do {
                    randomWordNumber = (int) (Math.random() * sentence.wordsCount());
                    logger.trace("randomWordNumber = {}", randomWordNumber);
                }
                while (sentence.wordsCount() > 2 & (randomWordNumber == 0 || randomWordNumber == (sentence.wordsCount() - 1)));

                String removedWord = sentence.removeWord(randomWordNumber);
                logger.trace("Removed word: {}", removedWord);
                sentenceWriter.println(sentence);
                removedWriter.println(randomWordNumber + " : " + removedWord);
                line = reader.readLine();
            }
            reader.close();
            sentenceWriter.close();
            removedWriter.close();
        }
    }

    private static BufferedReader createSourceReader(int partNo) throws FileNotFoundException {
        return new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_valid_part_" + partNo + ".txt"));
    }

    private static PrintWriter createOutWriter(int currentPart, String suffix) throws FileNotFoundException {
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\" +
                    suffix +
                    "_part_" + currentPart + ".txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
