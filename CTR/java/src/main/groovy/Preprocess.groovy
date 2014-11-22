import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.runtime.ResourceGroovyMethods

/**
 * Created by szelenin on 11/21/2014.
 */
class Preprocess {
    private File inFile;
    private File outFile;

    public static void main(String[] args) {
        def file = new File('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\tmp.csv')
        def columns = [:]
        def uniqueCounts = [:]

        ResourceGroovyMethods.eachLine(file) { line, number ->
            (line as String).split(',').eachWithIndex { Serializable entry, int i ->
                if (number == 1) {
                    columns[i] = entry
                } else {
                    def counts = uniqueCounts[columns[i]]
                    if (!counts) {
                        counts = [entry: [:]]
                    }

                    if (counts) {
                        def valueCount = counts[entry as String]
                        if (valueCount) {
                            counts[entry as String] += 1
                        }
//                        = ++valueCount
                    } else {
                        uniqueCounts[columns[i]] = counts
                    }
                }
            }
        }
        println "uniqueCounts = $uniqueCounts"
        println "columns = $columns"

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
            writer.println(StringUtils.join(outLine, ','))
        }
        writer.close()
        result
    }
}
