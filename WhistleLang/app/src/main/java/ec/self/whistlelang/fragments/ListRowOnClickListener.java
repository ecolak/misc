package ec.self.whistlelang.fragments;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.IOException;
import java.util.Map;

import ec.self.whistlelang.model.BaseTextEntity;

class ListRowOnClickListener implements OnItemClickListener {
    private static final String MP = "MP";
    private final AssetManager assets;
    private final ListView listView;
    private final MediaPlayer mediaPlayer;
    private final Map<String, BaseTextEntity> textEntityMap;

    public ListRowOnClickListener(ListView listView, MediaPlayer mediaPlayer, AssetManager assets, Map<String, BaseTextEntity> textEntityMap) {
        this.listView = listView;
        this.mediaPlayer = mediaPlayer;
        this.assets = assets;
        this.textEntityMap = textEntityMap;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        BaseTextEntity te = (BaseTextEntity) this.textEntityMap.get((String) this.listView.getItemAtPosition(position));
        try {
            if (this.mediaPlayer.isPlaying()) {
                Log.d(MP, "Media player is still playing...");
                return;
            }
            if (te.isAsset()) {
                AssetFileDescriptor afd = this.assets.openFd(te.getSoundFilePath());
                this.mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } else {
                this.mediaPlayer.setDataSource(te.getSoundFilePath());
            }
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
        } catch (IOException ioe) {
            Log.e(MP, ioe.getMessage());
        }
    }
}
