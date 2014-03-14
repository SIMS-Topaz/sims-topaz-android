package com.sims.topaz;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.sims.topaz.adapter.DrawerAdapter;
import com.sims.topaz.interfaces.OnMoveCamera;
import com.sims.topaz.interfaces.OnShowUserProfile;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;


public class DrawerActivity extends ActionBarActivity
	implements EditMessageFragment.OnNewMessageListener,
				OnMoveCamera, 
				OnShowUserProfile,
				PreviewListFragment.OnPreviewClickListener,
				UserCommentFragment.OnMessageClickListener
				{
	//see http://developer.android.com/guide/topics/ui/actionbar.html
	//in order 
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mViewsTitles;
    private MapFragment mMapFragment;
    private Fragment mLastFragment;

    private static String FRAGMENT_MAP = "fragment_map";
    private static String FRAGMENT_USER = "fragment_user";
    private static String FRAGMENT_SEARCH = "fragment_search_tag";
    private static String FRAGMENT_ABOUT = "fragment_about";
    private static String FRAGMENT_SETTINGS = "fragment_settings";
    private static String FRAGMENT_COMMENT = "fragment_comment";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_list);
        mLastFragment = mMapFragment = new MapFragment();       
        setDrawer(savedInstanceState);   
    }
    

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
       // Do something for froyo and above versions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {

			public void onDrawerClosed(View view) {
					super.onDrawerClosed(view);
            		getSupportActionBar().setTitle(mTitle);
            		supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()           	
            }

			public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
            		getSupportActionBar().setTitle(mDrawerTitle);
            		supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            		//hide kyboard
            		InputMethodManager mgr = (InputMethodManager) 
            				SimsContext.getContext()
            				.getSystemService(Context.INPUT_METHOD_SERVICE);
            	    mgr.hideSoftInputFromWindow(
            	    		getCurrentFocus().getWindowToken(), 0);
			}
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        if (savedInstanceState == null) {
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	fragmentManager.beginTransaction().remove(mLastFragment);
        	mLastFragment = mMapFragment;
    		fragmentManager
    		.beginTransaction()
    		.replace(R.id.content_frame, mLastFragment)
    		.commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(1, true);
            setTitle(mViewsTitles[1]);
            mDrawerLayout.closeDrawer(mDrawerList);
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
    	if(android.R.id.home == item.getItemId()) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT) == false) {
            	mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            else {
            	mDrawerLayout.closeDrawers();
            }
        }
        
         return super.onOptionsItemSelected(item);
        
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

	private void selectItem(int position) {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	fragmentManager.beginTransaction().remove(mLastFragment);
    	boolean change = true;
    	String TAG = "";
    	switch (position) {
    	case 0:
    		mLastFragment = UserFragment.newInstance(true);
    		TAG = FRAGMENT_USER;
    		break;
		case 1:
			mLastFragment = TagSearchFragment.newInstance(mMapFragment.getMap());
			TAG = FRAGMENT_SEARCH;
			break;
		case 2:
			if(mLastFragment instanceof MapFragment){
				change = false;
			}
			mLastFragment = mMapFragment;	
			TAG = FRAGMENT_MAP;
			break;
		case 3:
			mLastFragment = new SettingsFragment();	
			TAG = FRAGMENT_SETTINGS;
			break;
		case 4:
			mLastFragment = new AboutFragment();
			TAG = FRAGMENT_ABOUT;
			break;
		default:
			break;
		}
    	
    	if(change){
			fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, mLastFragment)
			.addToBackStack(TAG)
			.commit();
    	}

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mViewsTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    

	@Override
	public void onNewMessage(Message message) {
		if(mMapFragment!=null) {
			mMapFragment.onNewMessage(message);
		}
	}
	
	@Override
	public void moveCamera(LatLngBounds bounds) {
		if (mMapFragment != null) {
			mMapFragment.moveCamera(bounds);
		}
	}
	public void onMyLocation(View v){
		if (mMapFragment != null) {
			mMapFragment.onMyLocation();
		}
	}


	@Override
	public void onShowUserProfileFragment(long id) {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	fragmentManager.beginTransaction().remove(mLastFragment);
    	
    	if(AuthUtils.getSessionLongValue
				(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID, (long)0) != id){
    		mLastFragment = UserFragment.newInstance(false, id);
    	}else{
    		mLastFragment = UserFragment.newInstance(true);
    	}
		fragmentManager
		.beginTransaction()
		.replace(R.id.content_frame, mLastFragment)
		.addToBackStack(FRAGMENT_USER)
		.commit();
	}
	
	@Override
	public void onPreviewClick(Preview p) {
		CommentFragment fragment = CommentFragment.newInstance(p.getId());

		//create transaction
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_slide_in_right,
				R.drawable.animation_slide_out_right);
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(FRAGMENT_COMMENT);
		transaction.commit();
	}
	
	public Fragment getLastFragment() {
		return mLastFragment;
	}


	@Override
	public void onMessageClick(Message message) {
		CommentFragment fragment = CommentFragment.newInstance(message.getId());
		//create transaction
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_slide_in_right,
				R.drawable.animation_slide_out_right);
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(FRAGMENT_COMMENT);
		transaction.commit();		
	}


}
