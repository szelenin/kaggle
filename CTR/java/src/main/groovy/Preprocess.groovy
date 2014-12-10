import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.runtime.ResourceGroovyMethods

/**
 * Created by szelenin on 11/21/2014.
 */
class Preprocess {
    private File inFile;
    private File outFile;
    private def result
    private int totalRows=0

    public static void main(String[] args) {
        def inFile = new File('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\train.csv')
        def outFile = new File('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\outTrain.csv')
//        def inFile = new File('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\tmp.csv')
//        def outFile = new File('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\outtmp.csv')

        def preprocess = new Preprocess(inFile: inFile, outFile: outFile)
        preprocess.run(['hour', 'C1', 'site_id', 'site_domain', 'site_category', 'app_id', 'app_domain', 'app_category', 'device_id', 'device_ip', 'device_model', 'C14', 'C15', 'C16', 'C20'] as Set)
        def metadataOut = new ObjectOutputStream(new FileOutputStream('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\outTrain.ser'))
        println "$preprocess"
        metadataOut.writeObject(preprocess.result)
        metadataOut.close()
    }

    def run(Set compactColumns = [] as Set) {
        def result = [:]
        def columns = [:]
        def writer = outFile.newPrintWriter()

        ResourceGroovyMethods.eachLine(inFile) { String line, number ->
            def outLine = []
            line.split(',').eachWithIndex { String word, int i ->
                if (number == 1) {
                    if (compactColumns.contains(word)) {
                        result[word] = [:]
                    }
                    columns[i] = word
                    outLine += word
                } else {
                    String columnName = columns[i]
                    if (compactColumns.contains(columnName)) {
                        def countIdxTuple = result[columnName][word]
                        if (!countIdxTuple) {
                            countIdxTuple = [0, result[columnName].size()]
                            result[columnName][word] = countIdxTuple
                        }
                        (countIdxTuple as List)[0] += 1
                        outLine += (countIdxTuple as List)[1]
                    } else {
                        outLine += word
                    }
                }
            }
            totalRows++
            if (totalRows % 1000 == 0) {
                println "Processed: $totalRows"
            }
            writer.println(StringUtils.join(outLine, ','))
        }
        writer.close()
        this.result = result
        result
    }

    @Override
    String toString() {
        "$result\r\ntotal:${totalRows-1}"
    }
}
