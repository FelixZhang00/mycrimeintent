package felix.example.crininalintent2.fragment;

import java.io.Serializable;
import java.util.Date;

import felix.example.crininalintent2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 对话框的界面为listview，实现对话框多项选择的功能
 * @author tmac
 *
 */
public class ChoiseDialogFragment extends DialogFragment {

	public static final String EXTRA_CRIME_DATE = "felix.example.crininalintent2.crime_date";
	protected static final String TAG = "ChoiseDialogFragment";
	protected static final String DIALOG_DATE = "date";

	private ListView mLv;

	private Date mDate;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mDate = (Date) getArguments().getSerializable(EXTRA_CRIME_DATE);

		
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.list_choise, null);
		mLv = (ListView) view.findViewById(R.id.lv_choise);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new String[] {
						"Date Picker", "Time Picker" });

		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					DatePickerFragment dialog = DatePickerFragment
							.newInstance(mDate);
					dialog.setTargetFragment(getTargetFragment(), CrimeFragment.REQUEST_DATE);
					dialog.show(fm, DIALOG_DATE);
				}
			}
		});

		return new AlertDialog.Builder(getActivity()).setTitle("Choise")
				.setView(view).create();
	}

	public static ChoiseDialogFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_DATE, date);
		ChoiseDialogFragment fragment = new ChoiseDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
	

}
