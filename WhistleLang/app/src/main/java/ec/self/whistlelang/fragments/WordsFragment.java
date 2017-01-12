package ec.self.whistlelang.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import ec.self.whistlelang.DBService;
import ec.self.whistlelang.MainActivity;
import ec.self.whistlelang.R;
import ec.self.whistlelang.model.BaseTextEntity;
import java.util.ArrayList;
import java.util.Map;

public class WordsFragment extends Fragment {
    private MainActivity ma;

    private static class SearchBoxWatcher implements TextWatcher {
        private final ArrayAdapter<String> adapter;
        private final ListView listView;

        public SearchBoxWatcher(ListView listView, ArrayAdapter<String> adapter) {
            this.listView = listView;
            this.adapter = adapter;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.adapter.getFilter().filter(s);
            this.listView.setAdapter(this.adapter);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void afterTextChanged(Editable editable) {}
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.words_fragment, container, false);
        this.ma = (MainActivity) getActivity();
        DBService dbService = this.ma.getDbService();
        MediaPlayer mediaPlayer = this.ma.getMediaPlayer();
        Map<String, BaseTextEntity> wordToEntityMap = BaseTextEntity.getTextEntityMap(dbService.listWords());
        ArrayAdapter<String> wordAdapter = new ArrayAdapter(this.ma,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList(wordToEntityMap.keySet()));
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(wordAdapter);
        listView.setOnItemClickListener(new ListRowOnClickListener(listView, mediaPlayer, this.ma.getAssets(), wordToEntityMap));
        listView.setOnItemLongClickListener(new ListRowOnLongClickListener(this.ma, listView, dbService, false));
        ((EditText) view.findViewById(R.id.search_box)).addTextChangedListener(new SearchBoxWatcher(listView, wordAdapter));
        return view;
    }
}
