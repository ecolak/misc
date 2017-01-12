package ec.self.whistlelang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;

import ec.self.whistlelang.model.BaseTextEntity;
import ec.self.whistlelang.model.SentenceEntity;
import ec.self.whistlelang.model.WordEntity;
import ec.self.whistlelang.sql.Constants;
import ec.self.whistlelang.sql.OrderBy;
import ec.self.whistlelang.sql.OrderByDir;
import ec.self.whistlelang.sql.SQLEntity;

public class DBService extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "whistle_lang.db";
    private static final int DATABASE_VERSION = 3;
    private static final String DB = "DB";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s";

    private Context context;

    public DBService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(DB, "Creating db");
        db.execSQL(getCreateWordsTableSQL());
        db.execSQL(getCreateSentencesTableSQL());
        Log.d(DB, "Loading words");
        loadWords(db);
        Log.d(DB, "Loading sentences");
        loadSentences(db);
    }

    private static String getCreateWordsTableSQL() {
        return "CREATE TABLE IF NOT EXISTS " + WordEntity.WORDS + " (" + WordEntity.WORD + " " +
                Constants.TEXT + " " + Constants.PRIMARY_KEY + ", " + BaseTextEntity.SOUND_FILE_PATH +
                " " + Constants.TEXT + ", " + BaseTextEntity.IS_ASSET + " " + Constants.INTEGER + ")";
    }

    private static String getCreateSentencesTableSQL() {
        return "CREATE TABLE IF NOT EXISTS " + SentenceEntity.SENTENCES + " (" +
                SentenceEntity.SENTENCE + " " + Constants.TEXT + " " + Constants.PRIMARY_KEY + ", " +
                BaseTextEntity.SOUND_FILE_PATH + " " + Constants.TEXT + ", " + BaseTextEntity.IS_ASSET +
                " " + Constants.INTEGER + ")";
    }

    private static String getDropWordsTableSQL() {
        return String.format(SQL_DROP_TABLE, new Object[]{WordEntity.WORDS});
    }

    private static String getDropSentencesTableSQL() {
        return String.format(SQL_DROP_TABLE, new Object[]{SentenceEntity.SENTENCES});
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DB, "Upgrading db");
        db.execSQL(getDropWordsTableSQL());
        db.execSQL(getDropSentencesTableSQL());
        onCreate(db);
    }

    public void createWord(SQLEntity sqlEntity) {
        createSQLEntity(sqlEntity, WordEntity.WORDS);
    }

    public void createWord(SQLiteDatabase db, SQLEntity sqlEntity) {
        createSQLEntity(db, sqlEntity, WordEntity.WORDS);
    }

    public void createSentence(SQLEntity sqlEntity) {
        createSQLEntity(sqlEntity, SentenceEntity.SENTENCES);
    }

    public void createSentence(SQLiteDatabase db, SQLEntity sqlEntity) {
        createSQLEntity(db, sqlEntity, SentenceEntity.SENTENCES);
    }

    private void createSQLEntity(SQLEntity sqlEntity, String tableName) {
        getWritableDatabase().insert(tableName, null, sqlEntity.getContentValues());
    }

    private void createSQLEntity(SQLiteDatabase db, SQLEntity sqlEntity, String tableName) {
        db.insert(tableName, null, sqlEntity.getContentValues());
    }

    public void createOrUpdateWord(WordEntity wordEntity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = wordEntity.getContentValues();
        if (fetchWord(wordEntity.getText()).getCount() == 0) {
            db.insert(WordEntity.WORDS, null, cv);
        } else {
            db.update(WordEntity.WORDS, cv, "WORD = ?", new String[]{wordEntity.getText()});
        }
    }

    public void createOrUpdateSentence(SentenceEntity sentenceEntity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = sentenceEntity.getContentValues();
        if (fetchSentence(sentenceEntity.getText()).getCount() == 0) {
            db.insert(SentenceEntity.SENTENCES, null, cv);
        } else {
            db.update(SentenceEntity.SENTENCES, cv, "SENTENCE = ?", new String[]{sentenceEntity.getText()});
        }
    }

    public void deleteWord(String word) {
        Log.d(DB, "Deleting word " + word);
        getWritableDatabase().delete(WordEntity.WORDS, "WORD = ?", new String[]{word});
    }

    public void deleteSentence(String sentence) {
        Log.d(DB, "Deleting sentence " + sentence);
        getWritableDatabase().delete(SentenceEntity.SENTENCES, "SENTENCE = ?", new String[]{sentence});
    }

    public Cursor fetchWords() {
        return fetchWords(null);
    }

    public Cursor fetchSentences() {
        return fetchSentences(null);
    }

    public Cursor fetchWords(OrderBy orderBy) {
        return fetchSQLEntities(WordEntity.WORDS, orderBy);
    }

    public Cursor fetchSentences(OrderBy orderBy) {
        return fetchSQLEntities(SentenceEntity.SENTENCES, orderBy);
    }

    private Cursor fetchSQLEntities(String tableName, OrderBy orderBy) {
        StringBuilder qbuf = new StringBuilder("select * from ").append(tableName);
        if (orderBy != null) {
            qbuf.append(" order by ").append(orderBy.getColumnName()).append(" collate unicode");
            OrderByDir dir = orderBy.getDir();
            if (dir != null) {
                qbuf.append(" ").append(dir.name().toLowerCase());
            }
        }

        return getReadableDatabase().rawQuery(qbuf.toString(), null);
    }

    public Cursor fetchWord(String word) {
        String q = "select * from " + WordEntity.WORDS + " where " + WordEntity.WORD + " = ?";
        return getReadableDatabase().rawQuery(q, new String[]{word});
    }

    public Cursor fetchSentence(String sentence) {
        String q = "select * from " + SentenceEntity.SENTENCES + " where " + SentenceEntity.SENTENCE + " = ?";
        return getReadableDatabase().rawQuery(q, new String[]{sentence});
    }

    public WordEntity findWord(String word) {
        Cursor c = fetchWord(word);
        if (c.moveToNext()) {
            return getWordEntityFromCursor(c);
        }
        return null;
    }

    public SentenceEntity findSentence(String sentence) {
        Cursor c = fetchSentence(sentence);
        if (c.moveToNext()) {
            return getSentenceEntityFromCursor(c);
        }
        return null;
    }

    public Collection<WordEntity> listWords() {
        Cursor c = fetchWords(new OrderBy(WordEntity.WORD));
        Collection<WordEntity> result = new LinkedList();
        while (c.moveToNext()) {
            result.add(getWordEntityFromCursor(c));
        }
        return result;
    }

    public Collection<SentenceEntity> listSentences() {
        Cursor c = fetchSentences(new OrderBy(SentenceEntity.SENTENCE));
        Collection<SentenceEntity> result = new LinkedList();
        while (c.moveToNext()) {
            result.add(getSentenceEntityFromCursor(c));
        }
        return result;
    }

    private static WordEntity getWordEntityFromCursor(Cursor c) {
        String word = c.getString(c.getColumnIndex(WordEntity.WORD));
        String pathToSoundFile = c.getString(c.getColumnIndex(BaseTextEntity.SOUND_FILE_PATH));
        boolean isAsset = c.getInt(c.getColumnIndex(BaseTextEntity.IS_ASSET)) == 1;
        return new WordEntity(word, pathToSoundFile, isAsset);
    }

    private static SentenceEntity getSentenceEntityFromCursor(Cursor c) {
        String sentence = c.getString(c.getColumnIndex(SentenceEntity.SENTENCE));
        String pathToSoundFile = c.getString(c.getColumnIndex(BaseTextEntity.SOUND_FILE_PATH));
        boolean isAsset = c.getInt(c.getColumnIndex(BaseTextEntity.IS_ASSET)) == 1;
        return new SentenceEntity(sentence, pathToSoundFile, isAsset);
    }

    private void loadWords(final SQLiteDatabase db) {
        loadEntities(new Loader() {
            @Override
            public String getLoaderFile() {
                return "words_dict.txt";
            }

            @Override
            public void loadSQLEntity(String[] tokens) {
                createWord(db, new WordEntity(tokens[0], tokens[1], true));
            }
        });
    }

    private void loadSentences(final SQLiteDatabase db) {
        loadEntities(new Loader() {
            @Override
            public String getLoaderFile() {
                return "sentences_dict.txt";
            }

            @Override
            public void loadSQLEntity(String[] tokens) {
                createSentence(db, new SentenceEntity(tokens[0], tokens[1], true));
            }
        });
    }

    private void loadEntities(Loader loader) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(loader.getLoaderFile())));
            String line;
            while ((line = reader.readLine()) != null) {
                loader.loadSQLEntity(line.split("\\s*,\\s*"));
            }
        } catch (IOException e) {
            Log.e(DB, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    Log.e(DB, ioe.getMessage());
                }
            }
        }
    }

    private interface Loader {
        String getLoaderFile();
        void loadSQLEntity(String[] strArr);
    }
}
