import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * Created by szelenin on 11/22/2014.
 */
class PreprocessTest extends Specification {
    private File dir
    def writer

    def setup(){
        dir = File.createTempDir()
        writer = new File(dir, 'test.csv').newPrintWriter()
    }

    def cleanup() {
        writer.close()
        FileUtils.forceDelete(dir)
    }

    def "simple value replace"() {
        given:
        writer.println('col1')
        writer.println('value')
        writer.flush()
        when:
        def preprocess = new Preprocess(inFile: new File(dir, 'test.csv'), outFile: new File(dir, 'out.csv'))
        def structure = preprocess.run()
        then:
        assert structure.col1.value == 1
    }

}
