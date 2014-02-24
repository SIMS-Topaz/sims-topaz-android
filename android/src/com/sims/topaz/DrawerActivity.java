package com.sims.topaz;


import org.w3c.dom.Comment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.adapter.DrawerAdapter;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.utils.SimsContext;

import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class DrawerActivity extends FragmentActivity
					implements EditMessageFragment.OnNewMessageListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mViewsTitles;
    private int mApiVersion = android.os.Build.VERSION.SDK_INT;
    private MapFragment mMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_list);
        SimsContext.setContext(getApplicationContext());
        mMapFragment = new MapFragment();
        setDrawer(savedInstanceState);

    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void setDrawer(Bundle savedInstanceState){

        mTitle = mDrawerTitle = getTitle();
        mViewsTitles = getResources().getStringArray(R.array.views_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, mViewsTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        if (mApiVersion >= 11){
            // Do something for froyo and above versions
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			public void onDrawerClosed(View view) {
            	if (mApiVersion >= 11){
            		getActionBar().setTitle(mTitle);
            		invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	}
            }

            @SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
            	if (mApiVersion >= 11){
            		getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	}
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        if (savedInstanceState == null) {
           selectItem(0);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
         return super.onOptionsItemSelected(item);
        
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void selectItem(int position) {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	switch (position) {
		case 0:
			
			fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, mMapFragment)
			.commit();			
			break;
		case 1:
			fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, mMapFragment)
			.commit();			
			break;
		case 2:
			fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, new SettingsFragment())
			.commit();		
		case 3:
			fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, new AboutFragment())
			.commit();	
		default:
			break;
		}


        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mViewsTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mApiVersion >= 11){
        	getActionBar().setTitle(mTitle);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    

	@Override
	public void onNewMessage(Message message) {
		if(mMapFragment!=null) {
			mMapFragment.onNewMessage(message);
		}
	}
	
	public void moveCamera(LatLng latLng) {
		if (mMapFragment != null) {
			mMapFragment.moveCamera(latLng);
		}
	}
}
