package ec.self.whistlelang.model;

import android.content.ContentValues;

public class WordEntity extends BaseTextEntity {
    public static final String WORD = "WORD";
    public static final String WORDS = "WORDS";
    private final String word;

    public WordEntity(String word, String soundFilePath) {
        this(word, soundFilePath, false);
    }

    public WordEntity(String word, String soundFilePath, boolean asset) {
        super(soundFilePath, asset);
        this.word = word;
    }

    public String getText() {
        return this.word;
    }

    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();
        values.put(WORD, this.word);
        return values;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("WordEntity [");
        sb.append("word = ").append(this.word).append(", soundFilePath = ").append(this.soundFilePath).append(", isAsset = ").append(this.asset).append("]");
        return sb.toString();
    }
}
