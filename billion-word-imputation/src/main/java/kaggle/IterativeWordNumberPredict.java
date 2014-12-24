package kaggle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by szelenin on 12/23/2014.
 */
public class IterativeWordNumberPredict {

    private static Kryo kryo;
    private static final Logger logger = LogManager.getLogger(IterativeWordNumberPredict.class);
    public static void main(String[] args) throws IOException {

        Model model = new Model();
        kryo = new Kryo();
        int modelPartNo = 1;
        Input modelReader = createModelReader(modelPartNo);
        logger.info("reading train model part {}...", modelPartNo);
        model.read(kryo, modelReader);
        logger.info("train model part {} read", modelPartNo);
        for (int currentPart = 1; currentPart <= 12; currentPart++) {
            BufferedReader reader = createTestPartReader(currentPart);
            PrintWriter outWriter = createOutWriter(modelPartNo, currentPart);
            logger.info("predicting word number and missed word in part {}", currentPart);
            String line = reader.readLine();
            while (line != null) {
                int sentenceNo = model.updateNgramCounts2(line);
                int missedWordNumber = model.missedWordNumber(sentenceNo);
                String missedWord = model.missedWord(sentenceNo, missedWordNumber);
                logger.trace("{} : {} -> {}", missedWordNumber, missedWord, line);
                outWriter.println(missedWordNumber+" : "+missedWord);
                line = reader.readLine();
            }
            reader.close();
            outWriter.close();
        }
        modelReader.close();
    }

    private static BufferedReader createTestPartReader(int partNo) throws FileNotFoundException {
        return new BufferedReader(new FileReader("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\test_part_" + partNo + ".txt"));
    }

    private static Input createModelReader(int partNo) throws FileNotFoundException {
        return new Input(new BufferedInputStream(new FileInputStream("D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\model_train_" + partNo + ".kryo")));
    }

    private static PrintWriter createOutWriter(int currentModel, int currentPart) throws FileNotFoundException {
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter("" +
                    "D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\predicted_model_" +currentModel+"_part_"+currentPart + ".txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
