package ec.self.whistlelang.model;

import android.content.ContentValues;

public class SentenceEntity extends BaseTextEntity {
    public static final String SENTENCE = "SENTENCE";
    public static final String SENTENCES = "SENTENCES";
    private final String sentence;

    public SentenceEntity(String sentence, String soundFilePath) {
        this(sentence, soundFilePath, false);
    }

    public SentenceEntity(String sentence, String soundFilePath, boolean asset) {
        super(soundFilePath, asset);
        this.sentence = sentence;
    }

    public String getText() {
        return this.sentence;
    }

    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();
        values.put(SENTENCE, this.sentence);
        return values;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SentenceEntity [");
        sb.append("sentence = ").append(this.sentence).append(", soundFilePath = ").append(this.soundFilePath).append(", isAsset = ").append(this.asset).append("]");
        return sb.toString();
    }
}
