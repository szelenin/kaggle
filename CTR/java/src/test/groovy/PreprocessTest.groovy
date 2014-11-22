import org.apache.commons.io.FileUtils
import spock.lang.Specification

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
        def structure = this.preprocess.run()
        def outLines = outFile.readLines()
        then:
        assert structure.col1.value == 1
        assert outLines[0] == 'col1'
        assert outLines[1] == '1'
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
