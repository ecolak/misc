package ec.self.whistlelang;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;
import java.util.LinkedList;

import ec.self.whistlelang.model.WordEntity;
import ec.self.whistlelang.sql.Constants;
import ec.self.whistlelang.sql.OrderBy;
import ec.self.whistlelang.sql.OrderByDir;

/**
 * Created by emre on 12/11/16.
 */

public class DBService extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "whistle_lang.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s";

    private static volatile boolean dbCreated = false;

    public DBService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!dbCreated) {
            db.execSQL(getCreateWordsTableSQL());
            dbCreated = true;
        }
    }

    private static String getCreateWordsTableSQL() {
        return new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(WordEntity.WORDS).append(" (").append(WordEntity.WORD).append(" ")
                .append(Constants.TEXT).append(" ").append(Constants.PRIMARY_KEY).append(", ")
                .append(WordEntity.SOUND_FILE_PATH).append(" ").append(Constants.TEXT).append(")")
                .toString();
    }

    private static String getDropWordsTableSQL() {
        return String.format(SQL_DROP_TABLE, WordEntity.WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getDropWordsTableSQL());
        onCreate(db);
    }

    public void createWord(WordEntity wordEntity) {
        getWritableDatabase().insert(WordEntity.WORDS, null, wordEntity.getContentValues());
    }

    public void createWordIfNotExists(WordEntity wordEntity) {
        Cursor c = fetchWord(wordEntity.getWord());
        int rowCount = c.getCount();
        if (rowCount == 0) {
            getWritableDatabase().insert(WordEntity.WORDS, null, wordEntity.getContentValues());
        }
    }

    public Cursor fetchWords() {
        return fetchWords(null);
    }

    public Cursor fetchWords(OrderBy orderBy) {
        StringBuilder qbuf = new StringBuilder("select * from ").append(WordEntity.WORDS);
        if (orderBy != null) {
            qbuf.append(" order by ").append(orderBy.getColumnName()).append(" ")
                    .append(orderBy.getDir().name().toLowerCase());
        }
        return getReadableDatabase().rawQuery(qbuf.toString(), null);
    }

    public Cursor fetchWord(String word) {
        String q = new StringBuilder("select * from ").append(WordEntity.WORDS)
                .append(" where ").append(WordEntity.WORD).append(" = ?").toString();
        return getReadableDatabase().rawQuery(q, new String[]{word});
    }

    public Collection<WordEntity> listWords() {
        Cursor c = fetchWords(new OrderBy(WordEntity.WORD));
        Collection<WordEntity> result = new LinkedList<>();
        while (c.moveToNext()) {
            result.add(new WordEntity(c.getString(c.getColumnIndex(WordEntity.WORD)),
                    c.getString(c.getColumnIndex(WordEntity.SOUND_FILE_PATH))));
        }
        return result;
    }
}
