package ec.self.whistlelang;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ec.self.whistlelang.model.SentenceEntity;
import ec.self.whistlelang.model.WordEntity;

public class AddNewActivity extends AppCompatActivity {
    private EditText addNewWordText;
    private final DBService dbService;
    private Element elem;
    private boolean isEdit;
    private EditText soundFileText;

    private enum Action {
        ADD,
        UPDATE
    }

    private enum Element {
        WORD,
        SENTENCE,
        SOUND_FILE
    }

    public AddNewActivity() {
        this.dbService = new DBService(this);
        this.isEdit = false;
        this.elem = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_add_word);
        this.addNewWordText = (EditText) findViewById(R.id.new_word);
        this.soundFileText = (EditText) findViewById(R.id.path_to_sound_file);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String text = null;
            String soundFilePath = null;
            String word = (String) getIntent().getExtras().get(WordEntity.WORD);
            if (word != null) {
                WordEntity we = this.dbService.findWord(word);
                if (we != null) {
                    text = we.getText();
                    soundFilePath = we.getSoundFilePath();
                    this.elem = Element.WORD;
                }
            } else {
                String sentence = (String) getIntent().getExtras().get(SentenceEntity.SENTENCE);
                if (sentence != null) {
                    SentenceEntity se = this.dbService.findSentence(sentence);
                    if (se != null) {
                        text = se.getText();
                        soundFilePath = se.getSoundFilePath();
                        this.elem = Element.SENTENCE;
                    }
                }
            }
            if (text != null && soundFilePath != null) {
                this.isEdit = true;
                this.addNewWordText.setText(text);
                this.addNewWordText.setKeyListener(null);
                this.soundFileText.setText(soundFilePath);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNewWord(View view) {
        String newWord = this.addNewWordText.getText().toString();
        if (Constants.stringIsBlank(newWord)) {
            errorOnEmpty(Element.WORD);
            return;
        }
        String pathToFile = this.soundFileText.getText().toString();
        if (Constants.stringIsBlank(pathToFile)) {
            errorOnEmpty(Element.SOUND_FILE);
            return;
        }
        if (newWord.matches(".*?\\s+.*?")) {
            this.dbService.createOrUpdateSentence(new SentenceEntity(newWord, pathToFile));
        } else {
            this.dbService.createOrUpdateWord(new WordEntity(newWord, pathToFile));
        }
        displaySuccessMessage(this.elem, this.isEdit ? Action.UPDATE : Action.ADD);
        this.addNewWordText.setText(BuildConfig.FLAVOR);
        this.soundFileText.setText(BuildConfig.FLAVOR);
    }

    public void selectFile(View view) {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.select_sound_file));
        final CharSequence[] fileNames = Constants.listFilesInMusicDir();
        builder.setItems(fileNames, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                soundFileText.setText(Constants.MUSIC_DIR + "/" + fileNames[i]);
            }
        }).show();
    }

    private void errorOnEmpty(Element elem) {
        int resId = -1;
        if (Element.WORD == elem) {
            resId = R.string.word_may_not_be_blank;
        } else if (Element.SENTENCE == elem) {
            resId = R.string.sentence_may_not_be_blank;
        } else if (Element.SOUND_FILE == elem) {
            resId = R.string.sound_path_may_not_be_blank;
        }
        if (resId >= 0) {
            Toast.makeText(getApplicationContext(), getString(resId), Toast.LENGTH_LONG).show();
        }
    }

    private void displaySuccessMessage(Element elem, Action action) {
        int resId = -1;
        if (Element.WORD == elem) {
            if (Action.ADD == action) {
                resId = R.string.word_successfully_added;
            } else if (Action.UPDATE == action) {
                resId = R.string.word_successfully_updated;
            }
        } else if (Element.SENTENCE == elem) {
            if (Action.ADD == action) {
                resId = R.string.sentence_successfully_added;
            } else if (Action.UPDATE == action) {
                resId = R.string.sentence_successfully_updated;
            }
        }
        if (resId >= 0) {
            Toast.makeText(getApplicationContext(), getString(resId), Toast.LENGTH_LONG).show();
        }
    }
}
