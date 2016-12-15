package ec.self.whistlelang;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ec.self.whistlelang.model.WordEntity;

public class AddWordActivity extends AppCompatActivity {

    private final DBService dbService = new DBService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewWord(View view) {
        EditText editText = (EditText) findViewById(R.id.new_word);
        String newWord = editText.getText().toString();
        dbService.createWordIfNotExists(new WordEntity(newWord, null));
        Toast.makeText(getApplicationContext(), String.format("You added the word '%s'", newWord),
                Toast.LENGTH_LONG).show();
    }
}
