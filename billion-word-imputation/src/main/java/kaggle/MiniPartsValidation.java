package kaggle;

import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by szelenin on 12/24/2014.
 */
public class MiniPartsValidation {

    private static String root = "D:\\workspace\\projects\\szelenin\\kaggle\\billion-word-imputation\\data\\";
    private static Pattern pattern = Pattern.compile("(\\d+)\\s+\\:\\s+(.+)");

    public static void main(String[] args) throws IOException {
        int modelNo = 1;
        int totalLines = 0;
        int wordPositionsCorrect = 0;
        int wordsCorrect = 0;
        for (int partNo = 1; partNo <= 12; partNo++) {
            BufferedReader predictedPartReader = createPredictedPartReader(modelNo, partNo);
            BufferedReader validationPartReader = createValidationPartReader(partNo);
            String predictedLine = predictedPartReader.readLine();
            String validationLine = validationPartReader.readLine();
            while (predictedLine != null && validationLine != null) {
                totalLines++;
                Pair<Integer, String> predicted = positionWordPair(predictedLine);
                Pair<Integer, String> validation = positionWordPair(validationLine);
                if (predicted.getValue0().equals(validation.getValue0())) {
                    wordPositionsCorrect++;
                }
                if (predicted.getValue1().equals(validation.getValue1())) {
                    wordsCorrect++;
                }
                predictedLine = predictedPartReader.readLine();
                validationLine = validationPartReader.readLine();
            }
            predictedPartReader.close();
            validationPartReader.close();
        }
        System.out.println(String.format("---Model part %d---\nTotal lines: %d\nCorrect word positions: %d\nCorrect words: %d", modelNo, totalLines, wordPositionsCorrect, wordsCorrect));
    }

    private static BufferedReader createPredictedPartReader(int modelNo, int partNo) throws FileNotFoundException {
        return new BufferedReader(new FileReader(root + "predicted_model_" + modelNo + "_part_" + partNo + ".txt"));
    }

    private static BufferedReader createValidationPartReader(int partNo) throws FileNotFoundException {
        return new BufferedReader(new FileReader(root + "removed_part_" + partNo + ".txt"));
    }

    private static Pair<Integer, String> positionWordPair(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return new Pair<>(Integer.valueOf(matcher.group(1)), matcher.group(2));
        }
        throw new AssertionError("Line '" +line+ "' does not follow pattern");
    }
}
