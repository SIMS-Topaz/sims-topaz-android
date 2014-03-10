package com.sims.topaz;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sims.topaz.adapter.PreviewListAdapter;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.DebugUtils;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PreviewListFragment extends Fragment implements
		AbsListView.OnItemClickListener {


	private List<Preview> previews;

	private OnPreviewClickListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;

	public static PreviewListFragment newInstance(List<Preview> param1) {
		PreviewListFragment fragment = new PreviewListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		//order preview by date descendant
		Collections.sort(param1, new PreviewDateComparator());
		fragment.setPreviews(param1);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PreviewListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(previews==null) DebugUtils.logException(
				new Exception("preview list is null"));

		mAdapter = new PreviewListAdapter(getActivity(),
				R.layout.adapter_preview_item, 
				previews);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_preview, container,
				false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPreviewClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener
					.onPreviewClick(previews.get(position));
		}
	}

	public void setPreviews(List<Preview> previews) {
		this.previews = previews;
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnPreviewClickListener {
		public void onPreviewClick(Preview preview);
	}
	
	/**
	 * Comparator of Previews
	 * order by Date descendant
	 * newest preview should be first
	 */
	protected static class PreviewDateComparator implements Comparator<Preview> {

		/**
		 * returns 0 if lhs and rhs have the same date
		 * returns > 0 if lhs is older than rhs
		 * returns < 0 is lhs is newer than rhs
		 */
		@Override
		public int compare(Preview lhs, Preview rhs) {
			Long diff = rhs.getTimestamp() - lhs.getTimestamp();
			return Long.signum(diff); 
		}
		
	}
}
