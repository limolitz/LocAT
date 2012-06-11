package de.wasmitnetzen.apps.locat;

import java.security.InvalidParameterException;
import java.util.List;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationSetActivity extends MapActivity {

	MapView mapView;
	LocationSetOverlay mItemizedOverlay;
	GeoPoint mGeoPoint;
	Drawable mIcon; 
	private Long mRowId;
	private EntriesDbAdapter mDbHelper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_set);
		mapView = (MapView) findViewById(R.id.location_set);
		mapView.setBuiltInZoomControls(true);
		mapView.setEnabled(true);
		mapView.setClickable(true);

		mDbHelper = new EntriesDbAdapter(this);
		mDbHelper.open();


		List<Overlay> mapOverlays = mapView.getOverlays();
		MapOverlay mapOverlay = new MapOverlay();

		mIcon = this.getResources().getDrawable(R.drawable.androidmarker);

		mapOverlays.add(mapOverlay);		

		mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(EntriesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(EntriesDbAdapter.KEY_ROWID)
					: null;
		}
		if (mRowId != null && mRowId != 0) {
			Cursor entry = mDbHelper.getLocation(mRowId);
			startManagingCursor(entry);
			Integer latitude = entry.getInt(entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_LATITUDE));
			Integer longitude = entry.getInt(entry.getColumnIndexOrThrow(EntriesDbAdapter.KEY_LONGITUDE));	
			if (latitude != null && longitude != null) mGeoPoint = new GeoPoint(latitude, longitude);
		} else if (mRowId == 0) throw new InvalidParameterException("mRowId is zero.");

		setLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void setLocation() {
		if (mGeoPoint != null) {
			mDbHelper.updateLocation(mRowId, mGeoPoint.getLatitudeE6(), mGeoPoint.getLongitudeE6());
			mapView.getOverlays().remove(mItemizedOverlay);			
			mItemizedOverlay = new LocationSetOverlay(mIcon, this);
			mapView.getOverlays().add(mItemizedOverlay);	
			OverlayItem overlayitem = new OverlayItem(mGeoPoint, "Current Location", "Current Location");
			mItemizedOverlay.addOverlay(overlayitem);
			Toast.makeText(getBaseContext(), mGeoPoint.getLatitudeE6()+":"+mGeoPoint.getLongitudeE6(), Toast.LENGTH_SHORT).show();
			
		}		
	}

	class MapOverlay extends com.google.android.maps.Overlay
	{	
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) 
		{   
			//---when user lifts his finger---
			if (event.getAction() == 1) {
				mGeoPoint = mapView.getProjection().fromPixels(
						(int) event.getX(),
						(int) event.getY());
				setLocation();				
				return true;
			}
			else                
				return false;
		}      
	}
}
