package ec.self.whistlelang;

import android.os.Environment;
import java.io.File;

public class Constants {
    public static final String MUSIC_DIR;
    public static final File MUSIC_DIR_FILE;

    private Constants() {
    }

    static {
        MUSIC_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
        MUSIC_DIR_FILE = new File(MUSIC_DIR);
    }

    public static final String getPathToFileInMusicDir(String filename) {
        return MUSIC_DIR + "/" + filename;
    }

    public static final String[] listFilesInMusicDir() {
        File[] soundFiles = MUSIC_DIR_FILE.listFiles();
        String[] fileNames = new String[soundFiles.length];
        for (int i = 0; i < soundFiles.length; i++) {
            fileNames[i] = soundFiles[i].getName();
        }
        return fileNames;
    }

    public static boolean stringIsBlank(String s) {
        return s == null || s.trim().length() == 0;
    }
}
