package kaggle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by szelenin on 12/11/2014.
 */
public class ModelReader {
    private static final Logger logger = LogManager.getLogger(ModelReader.class);

    public static void main(String[] args) throws IOException {
        DictionaryModel model = new DictionaryModel(3);
        BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_v2.txt"));
        String line = reader.readLine();
        while (line != null) {
            model.put(line);
            line = reader.readLine();
            if (model.sentencesRead() % 1000 == 0) {
                logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
            }
        }
        logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\model.ser")));
        objectOutputStream.writeObject(model);
        objectOutputStream.close();
    }
}
