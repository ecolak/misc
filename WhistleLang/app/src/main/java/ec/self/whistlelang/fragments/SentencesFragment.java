package ec.self.whistlelang.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ec.self.whistlelang.DBService;
import ec.self.whistlelang.MainActivity;
import ec.self.whistlelang.R;
import ec.self.whistlelang.model.BaseTextEntity;
import java.util.ArrayList;
import java.util.Map;

public class SentencesFragment extends Fragment {
    private MainActivity ma;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sentences_fragment, container, false);
        this.ma = (MainActivity) getActivity();
        DBService dbService = this.ma.getDbService();
        MediaPlayer mediaPlayer = this.ma.getMediaPlayer();
        Map<String, BaseTextEntity> sentenceToEntityMap = BaseTextEntity.getTextEntityMap(dbService.listSentences());
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter(this.ma,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList(sentenceToEntityMap.keySet())));
        listView.setOnItemClickListener(new ListRowOnClickListener(listView, mediaPlayer, this.ma.getAssets(), sentenceToEntityMap));
        listView.setOnItemLongClickListener(new ListRowOnLongClickListener(this.ma, listView, dbService, true));
        return view;
    }
}
