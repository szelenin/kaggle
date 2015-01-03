package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by szelenin on 12/11/2014.
 */
public class TrainMiniModels {
    private static final Logger logger = LogManager.getLogger(TrainMiniModels.class);
    private static final Kryo kryo = new Kryo();

    public static void main(String[] args) throws IOException {
        int totalParts = 12;
        int nGramCount = 5;
        for (int i = 1; i <= totalParts; i++) {
            logger.info("Start processing part {}", i);
            Model model = new Model(nGramCount);
            BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\train_part_" + i + ".txt"));
            String line = reader.readLine();
            while (line != null) {
                model.put(line);
                line = reader.readLine();
                if (model.sentencesRead() % 1000 == 0) {
                    logger.info("Lines read: {}. Unique words: {}, total words: {}", model.sentencesRead(), model.uniqueWordsCount(), model.totalWords());
                }
            }
            writeModel(model, nGramCount, i);
            reader.close();
        }
    }

    private static void writeModel(Model model, int nGramCount, int part) throws IOException {
        logger.info("Writing model part {}", part);
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(
                        "D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\model_" + nGramCount + "train_" + part + ".kryo"));
        Output kryoOut = new Output(out);
        model.write(kryo, kryoOut);
        kryoOut.close();
        logger.info("Model written");
    }
}
