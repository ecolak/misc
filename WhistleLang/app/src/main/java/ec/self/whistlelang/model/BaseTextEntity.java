package ec.self.whistlelang.model;

import android.content.ContentValues;
import ec.self.whistlelang.sql.SQLEntity;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseTextEntity implements SQLEntity {
    public static final String IS_ASSET = "IS_ASSET";
    public static final String SOUND_FILE_PATH = "SOUND_FILE_PATH";
    protected final boolean asset;
    protected final String soundFilePath;

    public abstract String getText();

    public BaseTextEntity(String soundFilePath, boolean asset) {
        this.soundFilePath = soundFilePath;
        this.asset = asset;
    }

    public String getSoundFilePath() {
        return this.soundFilePath;
    }

    public boolean isAsset() {
        return this.asset;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(SOUND_FILE_PATH, this.soundFilePath);
        values.put(IS_ASSET, Boolean.valueOf(this.asset));
        return values;
    }

    public static Map<String, BaseTextEntity> getTextEntityMap(Collection<? extends BaseTextEntity> coll) {
        Map<String, BaseTextEntity> result = new LinkedHashMap();
        for (BaseTextEntity te : coll) {
            result.put(te.getText(), te);
        }
        return result;
    }
}
