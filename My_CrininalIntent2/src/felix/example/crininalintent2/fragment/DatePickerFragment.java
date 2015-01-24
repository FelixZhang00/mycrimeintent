package felix.example.crininalintent2.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import felix.example.crininalintent2.R;
import felix.example.crininalintent2.R.id;
import felix.example.crininalintent2.R.layout;
import felix.example.crininalintent2.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {

	public static final String EXTRA_CRIME_DATE = "felix.example.crininalintent2.crime_date";

	private static final String TAG = "DatePickerFragment";

	private Date mDate;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mDate = (Date) getArguments().getSerializable(EXTRA_CRIME_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		Log.d(TAG, "year:" + year + "month:" + month + "day:" + day);

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_date, null);

		DatePicker datePicker = (DatePicker) view.findViewById(R.id.dp_dialog);
		datePicker.init(year, month, day,
				new DatePicker.OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mDate = new GregorianCalendar(year, monthOfYear,
								dayOfMonth).getTime();
						getArguments().putSerializable(EXTRA_CRIME_DATE, mDate);
					}
				});

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.date_picker_title)
				.setView(view)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendResult(Activity.RESULT_OK);
							}

						}).create();
	}

	private void sendResult(int resultOk) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		i.putExtra(EXTRA_CRIME_DATE, mDate);

		getTargetFragment().onActivityResult(getTargetRequestCode(), resultOk,
				i);
	}

	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_DATE, date);
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

}
