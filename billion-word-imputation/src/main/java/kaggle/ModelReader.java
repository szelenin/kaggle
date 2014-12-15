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
        Model model = new Model(3);
        int currentPart = 1;
        int totalSentences = 30301028;
        int totalParts = 12;
        BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_v2.txt"));
        PrintWriter trainPartOut = createPartOutStream(currentPart, "train");
        PrintWriter testPartOut = createPartOutStream(currentPart, "test");
        String line = reader.readLine();
        while (line != null) {
            model.put(line);
            line = reader.readLine();
            if (Math.random() > 0.1) {
                trainPartOut.println(line);
            }else{
                testPartOut.println(line);
            }
            if (model.sentencesRead() % 1000 == 0) {
                logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
            }
            if (model.sentencesRead() > totalSentences / totalParts && currentPart < totalParts) {
                trainPartOut.close();
                testPartOut.close();
                logger.info("Writing model, part {} ...", currentPart);
                writeModel(model, currentPart);
                logger.info("Model written.");
                currentPart++;
                trainPartOut = createPartOutStream(currentPart, "train");
                testPartOut = createPartOutStream(currentPart, "test");
                model = new Model(3);
            }
        }
        trainPartOut.close();
        testPartOut.close();
        logger.info("Writing model, part {} ...", currentPart+1);
        writeModel(model, currentPart+1);
        logger.info("Model written.");
        logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
/*
        Model model = new Model(3);
        int currentPart = 1;
        int totalSentences = 30301028;
        int totalParts = 10;
        BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_part_4.txt"));
        String line = reader.readLine();
        while (line != null) {
            model.put(line);
            line = reader.readLine();
            if (model.sentencesRead() % 1000 == 0) {
                logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
            }
        }
        writeModel(model, 4);
        logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
*/
    }

    private static void writeModel(Model model, int currentPart) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream(
                        "D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\model" + currentPart + ".ser")));
        objectOutputStream.writeObject(model);
        objectOutputStream.close();
    }

    private static PrintWriter createPartOutStream(int currentPart, String suffix) throws FileNotFoundException {
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
