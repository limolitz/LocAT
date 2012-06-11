package de.wasmitnetzen.apps.locat;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListEntriesActivity extends ListActivity {

	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private EntriesDbAdapter mDbHelper;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entries_list);
		mDbHelper = new EntriesDbAdapter(this);
		mDbHelper.open();
		fillEntryList();
		registerForContextMenu(getListView());
	}

	private void fillEntryList() {
		// Get all of the rows from the database and create the item list
		Cursor entryCursor = mDbHelper.fetchAllEntries();
		startManagingCursor(entryCursor);

		// Create an array to specify the fields we want to display in the list
		String[] from = new String[]{EntriesDbAdapter.KEY_TITLE};

		// and an array of the fields we want to bind those fields to
		int[] to = new int[]{R.id.text1};

		SimpleCursorAdapter entries = 
				new SimpleCursorAdapter(this, R.layout.entries_row, entryCursor, from, to);
		setListAdapter(entries);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createEntry();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mDbHelper.deleteEntry(info.id);
			fillEntryList();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createEntry() {
		Intent i = new Intent(this, EntryEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, EntryEdit.class);
		i.putExtra(EntriesDbAdapter.KEY_ROWID, id);        
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillEntryList();
	}


}

