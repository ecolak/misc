package ec.self.whistlelang.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import ec.self.whistlelang.AddNewActivity;
import ec.self.whistlelang.DBService;
import ec.self.whistlelang.R;
import ec.self.whistlelang.model.SentenceEntity;
import ec.self.whistlelang.model.WordEntity;

class ListRowOnLongClickListener implements OnItemLongClickListener {
    private final Context context;
    private final DBService dbService;
    private final boolean isSentence;
    private final ListView listView;

    public ListRowOnLongClickListener(Context context, ListView listView, DBService dbService, boolean isSentence) {
        this.context = context;
        this.listView = listView;
        this.dbService = dbService;
        this.isSentence = isSentence;
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final String itemValue = (String) this.listView.getItemAtPosition(position);
        PopupMenu popupMenu = new PopupMenu(this.context, view);
        popupMenu.inflate(R.menu.word_popup_menu);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.delete_word) {
                    if (isSentence) {
                        dbService.deleteSentence(itemValue);
                    } else {
                        dbService.deleteWord(itemValue);
                    }
                    ((ArrayAdapter) listView.getAdapter()).remove(itemValue);
                } else if (itemId == R.id.edit_word) {
                    Intent intent = new Intent(ListRowOnLongClickListener.this.context, AddNewActivity.class);
                    intent.putExtra(isSentence ? SentenceEntity.SENTENCE : WordEntity.WORD, itemValue);
                    ListRowOnLongClickListener.this.context.startActivity(intent);
                }
                return true;
            }
        });
        popupMenu.show();
        return true;
    }
}
