package com.sims.topaz.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewSizeHelper {
	public static int getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return 0;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            if(listItem!=null){
	            listItem.measure(0, 0);
	            totalHeight += listItem.getMeasuredHeight();
            }
        }
      //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        DebugUtils.log("height of listItem:"+ String.valueOf(totalHeight));
        return totalHeight;
        
    }

// http://www.androidhub4you.com/2012/12/listview-into-scrollview-in-android.html#ixzz2vjpd6Ax0
}
