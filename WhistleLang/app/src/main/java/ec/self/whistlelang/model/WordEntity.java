package ec.self.whistlelang.model;

import android.content.ContentValues;

import ec.self.whistlelang.sql.SQLEntity;

/**
 * Created by emre on 12/11/16.
 */

public class WordEntity implements SQLEntity {

    public static final String WORDS = "WORDS";
    public static final String WORD = "WORD";
    public static final String SOUND_FILE_PATH = "SOUND_FILE_PATH";

    private final String word;
    private final String soundFilePath;

    public WordEntity(String word, String soundFilePath) {
        this.word = word;
        this.soundFilePath = soundFilePath;
    }

    public String getWord() {
        return word;
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(WORD, word);
        values.put(SOUND_FILE_PATH, soundFilePath);
        return values;
    }

    @Override
    public String toString() {
        return new StringBuilder("WordEntity [word = ").append(word).append(", soundFilePath = ")
                .append(soundFilePath).append("]").toString();
    }

}
