import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by szelenin on 11/22/2014.
 */
class PreprocessTest extends Specification {
    private File dir
    def writer
    def preprocess
    File outFile
    File inFile

    def setup(){
        dir = File.createTempDir()
        outFile = new File(dir, 'out.csv')
        inFile = new File(dir, 'test.csv')
        writer = inFile.newPrintWriter()
        preprocess = new Preprocess(inFile: inFile, outFile: outFile)
    }

    def "simple value replace"() {
        given:
        writeLine('col1')
        writeLine('value')
        when:
        def structure = this.preprocess.run(['col1'] as Set)
        def outLines = outFile.readLines()
        then:
        assert structure.col1.value == [1, 0]
        assert outLines[0] == 'col1'
        assert outLines[1] == '0'
    }

    def "several columns value replace"() {
        given:
        writeLine('col1,col2')
        writeLine('value1,value2')
        when:
        def structure = this.preprocess.run(['col1','col2'] as Set)
        def outLines = outFile.readLines()
        then:
        assert structure.col1.value1 == [1, 0]
        assert structure.col2.value2 == [1, 0]
        assert outLines[0] == 'col1,col2'
        assert outLines[1] == '0,0'
    }

    def "several rows value replace"() {
        given:
        writeLine('col1')
        writeLine('value1')
        writeLine('value2')
        when:
        def structure = this.preprocess.run(['col1'] as Set)
        def outLines = outFile.readLines()
        then:
        assert structure.col1.value1 == [1, 0]
        assert structure.col1.value2 == [1, 1]
        assert outLines[0] == 'col1'
        assert outLines[1] == '0'
        assert outLines[2] == '1'
    }

    def "several columns and duplicated values"() {
        given:
        writeLine('col1,col2')
        writeLine('value1,value2')
        writeLine('value1,value3')
        when:
        def structure = this.preprocess.run(['col1','col2'] as Set)
        def outLines = outFile.readLines()
        then:
        assert structure.col1.value1 == [2, 0]
        assert structure.col2.value2 == [1, 0]
        assert structure.col2.value3 == [1, 1]
        assert outLines[0] == 'col1,col2'
        assert outLines[1] == '0,0'
        assert outLines[2] == '0,1'
    }

    def "preprocess only specified cols"(){
        given:
        writeLine('col1,col2,col3')
        writeLine('value1,value2,value3')
        writeLine('value1,value3,valuex')
        writeLine('value1,value3,value3')
        when:
        def structure = this.preprocess.run(['col2','col3'] as Set)
        def outLines = outFile.readLines()
        then:
        assert structure.col1 == null
        assert structure.col2.value2 == [1, 0]
        assert structure.col2.value3 == [2, 1]
        assert structure.col3.value3 == [2, 0]
        assert structure.col3.valuex == [1, 1]
        assert outLines[0] == 'col1,col2,col3'
        assert outLines[1] == 'value1,0,0'
        assert outLines[2] == 'value1,1,1'
        assert outLines[3] == 'value1,1,0'
        println "$preprocess"
    }

    def "readtmp"() {
        when:
        def object = new ObjectInputStream(new FileInputStream('D:\\workspace\\projects\\szelenin\\kaggle\\CTR\\data\\outTrain.ser')).readObject()

        then:
        println "object = $object"
   }

    def cleanup() {
        writer.close()
        FileUtils.forceDelete(dir)
    }

    private writeLine(String column) {
        writer.println(column)
        writer.flush()
    }

}
