package de.wasmitnetzen.apps.locat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EntryEdit extends Activity {
	private EditText mTitleText;
    private EditText mBodyText;
    private TextView mLocationText;
    private Long mRowId;
    
    private EntriesDbAdapter mDbHelper;
    
    private EntryEditClickListener mClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new EntriesDbAdapter(this);
        mDbHelper.open();
        
        mClickListener = new EntryEditClickListener(this);
        
        setContentView(R.layout.entry_edit);
        setTitle(R.string.edit_entry);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mLocationText = (TextView) findViewById(R.id.location_text);

        Button setLocationButton = (Button) findViewById(R.id.edit_location);
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(EntriesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(EntriesDbAdapter.KEY_ROWID)
                                    : null;
        }
        
        //if (mRowId != null) Toast.makeText(getBaseContext(), "Editing Entry #"+mRowId, Toast.LENGTH_SHORT).show();
        
        populateFields();

        confirmButton.setOnClickListener(mClickListener);
        setLocationButton.setOnClickListener(mClickListener);
    }
    
    private class EntryEditClickListener implements OnClickListener {
    	private Activity mParent;
    	public EntryEditClickListener(Activity parent) {
    		mParent = parent;
    	}
    	public void onClick(View view) {
    		if (view == findViewById(R.id.confirm)) {
    			setResult(RESULT_OK);
                finish();
    		} else if (view == findViewById(R.id.edit_location)) {
    			saveState();
    			Intent i = new Intent(mParent, LocationSetActivity	.class);
    			i.putExtra(EntriesDbAdapter.KEY_ROWID, mRowId);     
    			startActivityForResult(i, 0);
    		}        	
        }
    };

	private void populateFields() {
		if (mRowId != null) {
	        Cursor entry = mDbHelper.fetchEntry(mRowId);
	        startManagingCursor(entry);
	        mTitleText.setText(entry.getString(
	                    entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_TITLE)));
	        mBodyText.setText(entry.getString(
	                entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_BODY)));
	        mLocationText.setText(entry.getString(
	                    entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_LATITUDE))+":"+entry.getString(
	    	                    entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_LONGITUDE)));
	    }		
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(EntriesDbAdapter.KEY_ROWID, mRowId);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
	
	private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createEntry(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateEntry(mRowId, title, body);
        }
    }

}
