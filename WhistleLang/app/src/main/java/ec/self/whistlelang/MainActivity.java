package ec.self.whistlelang;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import ec.self.whistlelang.fragments.SentencesFragment;
import ec.self.whistlelang.fragments.WordsFragment;

public class MainActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_STORAGE = new String[]{
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private final DBService dbService;
    private final MediaPlayer mediaPlayer;

    public MainActivity() {
        this.dbService = new DBService(this);
        this.mediaPlayer = new MediaPlayer();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        ((TabLayout) findViewById(R.id.sliding_tabs)).setupWithViewPager(viewPager);
        initMediaPlayer();
        verifyStoragePermissions(this);
    }

    private void initMediaPlayer() {
        this.mediaPlayer.setAudioStreamType(3);
        this.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();
            }
        });
    }

    private static void verifyStoragePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WordsFragment(), getString(R.string.words_tab));
        adapter.addFragment(new SentencesFragment(), getString(R.string.sentences_tab));
        viewPager.setAdapter(adapter);
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayer;
    }

    public DBService getDbService() {
        return this.dbService;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_button:
                startActivity(new Intent(this, AddNewActivity.class));
                return true;
            case R.id.about_button:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStop() {
        this.mediaPlayer.release();
        super.onStop();
    }
}
