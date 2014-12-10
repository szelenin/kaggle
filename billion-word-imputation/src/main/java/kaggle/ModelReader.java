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
        Model model = new Model(2, 3);
        BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_v2.txt"));
        String line = reader.readLine();
        while (line != null) {
            model.put(line);
            line = reader.readLine();
            if (model.sentencesRead() % 100 == 0) {
                logger.info("Lines read: {}. Unique words {}", model.sentencesRead(), model.uniqueWordsCount());
            }
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\model.ser")));
        objectOutputStream.writeObject(model);
        objectOutputStream.close();
    }
}
