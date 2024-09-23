package com.music.backend.demos.util;

import org.audiveris.proxymusic.Opus;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.mxl.Mxl;
import org.audiveris.proxymusic.mxl.RootFile;
import org.audiveris.proxymusic.util.Marshalling;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class DummyGeneratorTest {
    private static final Logger logger = LoggerFactory.getLogger(DummyGeneratorTest.class);

    /** Temporary area. */
    private static final File TEMP_DIR = new File("target/temp");

    /** Name of the temporary XML file. */
    private static final String FILE_NAME = "hello-world-test.xml";

    @Test
    void buildScorePartwise() throws JAXBException, IOException, Marshalling.MarshallingException {
//        System.out.println("Building contexts ...");
//        Marshalling.getContext(ScorePartwise.class);
//        Marshalling.getContext(Opus.class);
//
//        System.out.println("Marshalling ...");
//
//        Mxl.Output mof = new Mxl.Output(new File("D:\\midiVisualizer\\backend\\src\\main\\resources\\musicxml\\proxymusic.mxl"));
//        OutputStream zos = mof.getOutputStream();
        ScorePartwise scorePartwise = DummyGenerator.buildScorePartwise("First score", 4);
//        System.out.println(new Dumper.Column(scorePartwise).toString());
        // Make sure the temp directory exists
        TEMP_DIR.mkdirs();

        //  Finally, marshal the proxy
        File xmlFile = new File(TEMP_DIR, FILE_NAME);
        OutputStream os = new FileOutputStream(xmlFile);
        long start = System.currentTimeMillis();

        Marshalling.marshal(scorePartwise, os, true, 2);

        logger.info("Marshalling done in {} ms", System.currentTimeMillis() - start);
        logger.info("Score exported to {}", xmlFile);
        os.close();
//        mof.addEntry(new RootFile("myscore.xml", RootFile.MUSICXML_MEDIA_TYPE));
//        Marshalling.marshal(scorePartwise, zos, true, 2);
    }
}