package com.music.backend.demos.util;

import org.audiveris.proxymusic.*;

import java.lang.String;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author rty
 * @version 1.0
 * @description: 生成ScorePartwise实例的工具类
 * @date 2024/8/19 23:24
 * @date 2024/8/19 23:24
 */
public abstract class DummyGenerator
{
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Not meant to be instantiated.
     */
    private DummyGenerator ()
    {
    }

    //~ Methods ------------------------------------------------------------------------------------
    /**
     * Build a ScorePartwise instance with as many measures as desired.
     *
     * @param title        title for the movement
     * @param measureCount number of measures
     * @return the ScorePartwise instance
     */
    public static ScorePartwise buildScorePartwise (String title,
                                                    int measureCount)
    {
        // Generated factory for all proxymusic elements
        ObjectFactory factory = new ObjectFactory();

        // Allocate the score partwise
        ScorePartwise scorePartwise = factory.createScorePartwise();

        // Movement
        scorePartwise.setMovementTitle(title);

        // Identification
        Identification identification = factory.createIdentification();
        scorePartwise.setIdentification(identification);

        TypedText typedText = factory.createTypedText();
        typedText.setValue("The Composer");
        typedText.setType("composer");
        identification.getCreator().add(typedText);

        // PartList
        PartList partList = factory.createPartList();
        scorePartwise.setPartList(partList);

        // Scorepart in partList
        ScorePart scorePart1 = factory.createScorePart();
        ScorePart scorePart2 = factory.createScorePart();
        partList.getPartGroupOrScorePart().add(scorePart1);
        partList.getPartGroupOrScorePart().add(scorePart2);
        scorePart1.setId("P1");
        scorePart2.setId("P2");

        PartName partName1 = factory.createPartName();
        PartName partName2 = factory.createPartName();
        scorePart1.setPartName(partName1);
        scorePart2.setPartName(partName2);
        partName1.setValue("Music");
        partName2.setValue("Piano");

        // ScorePart in scorePartwise
        ScorePartwise.Part part = factory.createScorePartwisePart();
        scorePartwise.getPart().add(part);
        part.setId(scorePart1);

        // Measure
        ScorePartwise.Part.Measure measure = factory.createScorePartwisePartMeasure();
        part.getMeasure().add(measure);
        measure.setNumber("1");

        // Attributes
        Attributes attributes = factory.createAttributes();
        measure.getNoteOrBackupOrForward().add(attributes);

        // Divisions
        attributes.setDivisions(new BigDecimal(1));

        // Key
        Key key = factory.createKey();
        attributes.getKey().add(key);
        key.setFifths(new BigInteger("0"));

        // Time
        Time time = factory.createTime();
        attributes.getTime().add(time);
        time.getTimeSignature().add(factory.createTimeBeats("4"));
        time.getTimeSignature().add(factory.createTimeBeatType("4"));

        // Clef
        Clef clef = factory.createClef();
        attributes.getClef().add(clef);
        clef.setSign(ClefSign.G);
        clef.setLine(new BigInteger("2"));

        // Note
        Note note = factory.createNote();
        measure.getNoteOrBackupOrForward().add(note);

        // Pitch
        Pitch pitch = factory.createPitch();
        note.setPitch(pitch);
        pitch.setStep(Step.C);
        pitch.setOctave(4);

        // Duration
        note.setDuration(new BigDecimal(4));

        // Type
        NoteType type = factory.createNoteType();
        type.setValue("whole");
        note.setType(type);

        for (int count = 2; count <= measureCount; count++) {
            ScorePartwise.Part.Measure meas = factory.createScorePartwisePartMeasure();
            part.getMeasure().add(meas);
            meas.setNumber("" + count);

            // Note 1 ---
            note = factory.createNote();
            meas.getNoteOrBackupOrForward().add(note);

            // Pitch
            pitch = factory.createPitch();
            note.setPitch(pitch);
            pitch.setStep(Step.E);
            pitch.setOctave(4);

            // Duration
            note.setDuration(new BigDecimal(4));

            // Type
            type = factory.createNoteType();
            type.setValue("whole");
            note.setType(type);
        }

        return scorePartwise;
    }
}
