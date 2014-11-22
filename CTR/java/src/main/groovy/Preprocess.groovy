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

    def run() {
        def result =[:]
        def columns = [:]
        ResourceGroovyMethods.eachLine(inFile){line, number->
            if (number == 1) {
                result[line]=[:]
                columns[0]= line
                return
            }

            result[columns[0]][line] = 1
        }
        result
    }
}
