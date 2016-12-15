package ec.self.whistlelang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ec.self.whistlelang.model.WordEntity;

public class MainActivity extends AppCompatActivity {

    private static final String[] words = new String[] {
            "a", "an", "and", "are", "at", "ate", "ant",
            "b", "be", "bee", "bet", "ban", "bat", "bit",
            "c", "cat", "can", "cut", "cute"
    };

    private static class SearchBoxWatcher implements TextWatcher {

        private final ListView listView;
        private final ArrayAdapter<String> adapter;

        public SearchBoxWatcher(ListView listView, ArrayAdapter<String> adapter) {
            this.listView = listView;
            this.adapter = adapter;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.getFilter().filter(s);
            listView.setAdapter(adapter);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    }

    private final DBService dbService = new DBService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Insert initial words to db
        insertInitialWords();

        final ListView listView = (ListView) findViewById(R.id.list);

        Collection<WordEntity> wordsInDb = dbService.listWords();
        List<String> wordList = new ArrayList<>(wordsInDb.size());
        for (WordEntity we : wordsInDb) {
            wordList.add(we.getWord());
        }

        final ArrayAdapter<String> wordAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, wordList);

        // Assign adapter to ListView
        listView.setAdapter(wordAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        String.format("Position: %d, Item: %s", position, itemValue),
                        Toast.LENGTH_LONG).show();
            }
        });

        final EditText searchBox = (EditText) findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new SearchBoxWatcher(listView, wordAdapter));
    }

    private void insertInitialWords() {
        for (String word : words) {
            dbService.createWordIfNotExists(new WordEntity(word, null));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_button:
                startActivity(new Intent(this, AddWordActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
