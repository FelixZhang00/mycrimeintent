package felix.example.crininalintent2.fragment;

import java.util.ArrayList;
import java.util.List;

import felix.example.crininalintent2.R;
import felix.example.crininalintent2.R.id;
import felix.example.crininalintent2.R.layout;
import felix.example.crininalintent2.R.menu;
import felix.example.crininalintent2.R.string;
import felix.example.crininalintent2.activity.CrimePageActivity;
import felix.example.crininalintent2.model.Crime;
import felix.example.crininalintent2.model.CrimeLab;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

	private static final String TAG = "CrimeListFragment";
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisable;

	private ListView mLv;
	private CrimeAdapter adapter;
	private TextView mTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setRetainInstance(true); // 在旋转屏幕是保持fragment实例(不包括actionbar)
		mSubtitleVisable = false;
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

		// ArrayAdapter<Crime> adapter = new ArrayAdapter<Crime>(getActivity(),
		// android.R.layout.simple_list_item_1, mCrimes);
		// setListAdapter(adapter);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisable) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}

		// chapter 16 Challenge:
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		mLv = (ListView) v.findViewById(android.R.id.list);
		mTv = (TextView) v.findViewById(android.R.id.empty);
		adapter = new CrimeAdapter(mCrimes);
		mLv.setAdapter(adapter);
		mLv.setEmptyView(mTv); // 设置此控件，用于系统自动切换（当list为空时）
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// listview设置成多选
			mLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			mLv.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater menuInflater = mode.getMenuInflater();
					menuInflater.inflate(R.menu.crime_list_item_context, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						// for (int pos = 0; pos < adapter.getCount() ; pos++) {
						// //通过debug发现：adapter.getCount()会随着for循环中的deleteCrime方法的调用而变化
						for (int pos = adapter.getCount() - 1; pos >= 0; pos--) {
							if (mLv.isItemChecked(pos)) {
								Crime crime = adapter.getItem(pos);
								CrimeLab.getInstance(getActivity())
										.deleteCrime(crime);
							}
						}
						adapter.notifyDataSetChanged();
						mode.finish();
						return true;
						

					default:
						return false;
					}
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					// TODO Auto-generated method stub

				}
			});
		} else {
			registerForContextMenu(mLv);

		}
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
		Crime c = adapter.getItem(position);
		// Log.d(TAG, c.getTitle() + " is clicked");
		Intent intent = new Intent(getActivity(), CrimePageActivity.class);
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(intent);
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {

		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				// convertView = LayoutInflater.from(getActivity()).inflate(
				// R.layout.list_item_crime, null);
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_crime, null);
			}
			Crime crime = getItem(position);

			TextView tv_title = (TextView) convertView
					.findViewById(R.id.tv_ls_item_title);
			tv_title.setText(crime.getTitle());
			TextView tv_date = (TextView) convertView
					.findViewById(R.id.tv_ls_item_date);
			tv_date.setText(crime.getDate().toString());
			CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_ls_item);
			cb.setChecked(crime.isSolved());

			return convertView;
		}

	}

	@Override
	public void onResume() {
		// ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
		adapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onPause() {
		CrimeLab.getInstance(getActivity()).saveCrimes();
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_list_item, menu);
		MenuItem showItem = menu.findItem(R.id.menu_item_show_subtitle);
		if (showItem != null && mSubtitleVisable) {
			showItem.setTitle(R.string.hide_subtitle);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.getInstance(getActivity()).addCrime(crime);

			Intent intent = new Intent(getActivity(), CrimePageActivity.class);
			intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivityForResult(intent, 0);
			return true;

		case R.id.menu_item_show_subtitle:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
				item.setTitle(R.string.hide_subtitle);
				mSubtitleVisable = true;
			} else {
				getActivity().getActionBar().setSubtitle(null);
				item.setTitle(R.string.show_subtitle);
				mSubtitleVisable = false;
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	// 根据view选择相应的菜单文件
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,
				menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int pos = menuInfo.position;
		Crime c = adapter.getItem(pos);
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.getInstance(getActivity()).deleteCrime(c);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
