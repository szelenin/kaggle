package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by szelenin on 12/11/2014.
 */
public class TrainBigModel {
    private static final Logger logger = LogManager.getLogger(TrainBigModel.class);
    private static final Kryo kryo = new Kryo();
    private static final String ROOT_FOLDER = "D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\5-ngrams";

    public static void main(String[] args) throws IOException {
        int nGramsCount = 5;
        Model model = new Model(nGramsCount);
        int currentPart = 1;
        int totalSentences = 30301028;
        int totalParts = 50;
        BufferedReader reader = new BufferedReader(new FileReader(new File(ROOT_FOLDER, "../train_v2.txt")));
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
                model = new Model(nGramsCount);
            }
        }
        trainPartOut.close();
        testPartOut.close();
        logger.info("Writing model, part {} ...", currentPart + 1);
        writeModel(model, currentPart);
        logger.info("Model written.");
        logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
    }

    private static void writeModel(Model model, int currentPart) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(new File(ROOT_FOLDER,"model" + currentPart + ".kryo")));
        Output kryoOut = new Output(out);
        model.write(kryo, kryoOut);
        kryoOut.close();
    }

    private static PrintWriter createPartOutStream(int currentPart, String prefix) throws FileNotFoundException {
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter(new File(ROOT_FOLDER, prefix + "_part_" + currentPart + ".txt"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
