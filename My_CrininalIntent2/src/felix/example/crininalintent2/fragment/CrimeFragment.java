package felix.example.crininalintent2.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.http.protocol.HTTP;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import felix.example.crininalintent2.R;
import felix.example.crininalintent2.activity.CrimeCameraActivity;
import felix.example.crininalintent2.model.Crime;
import felix.example.crininalintent2.model.CrimeLab;
import felix.example.crininalintent2.model.Photo;
import felix.example.crininalintent2.utils.PictureUtils;

public class CrimeFragment extends Fragment {
	private static final String TAG = "CrimeFragment";

	public final static String EXTRA_CRIME_ID = "felix.example.crininalintent2.crime_id";

	protected static final String DIALOG_DATE = "date";

	protected static final int REQUEST_DATE = 0;

	protected static final String DIALOG_CHOICE = "choice";
	protected static final String DIALOG_IMAGE = "image";

	protected static final int REQUEST_CHOICE = 1;

	protected static final int TAKE_PICTURE = 2;
	protected static final int TAKE_PICTURE_2 = 3;

	/**
	 * 调用联系人应用程序
	 */
	protected static final int PICK_CONTACT = 4;

	private Crime mCrime;

	private EditText mEtCrimeTitle;
	private Button mBtnCrimeDate;
	private CheckBox mCbCrimeSolved;
	private ImageButton mIbtnPicture;
	private ImageView mIvCamera;
	private Button mBtnChooseSus;
	private Button mBtnSend;

	private Uri outFileUri;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// UUID crime_id = (UUID)
		// getActivity().getIntent().getSerializableExtra(
		// EXTRA_CRIME_ID);

		// UUID crime_id = (UUID) getArguments().get(EXTRA_CRIME_ID);
		UUID crime_id = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

		mCrime = CrimeLab.getInstance(getActivity()).getCrime(crime_id);

		// 设置actionbar 左边的返回键
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) { // 只有当此activity设置了home才让左上角的返回键有效
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		// 设置当前的fragment拥有菜单
		setHasOptionsMenu(true);

	}

	public static CrimeFragment newInstance(UUID crimeID) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_CRIME_ID, crimeID);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(bundle);
		return fragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		mEtCrimeTitle = (EditText) v.findViewById(R.id.et_crime_title);
		mEtCrimeTitle.setText(mCrime.getTitle());

		mEtCrimeTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mCrime.setTitle(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mBtnCrimeDate = (Button) v.findViewById(R.id.btn_crime_date);
		updateDate();
		// mBtnCrimeDate.setEnabled(false);
		mBtnCrimeDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				// DatePickerFragment dialog = DatePickerFragment
				// .newInstance(mCrime.getDate());
				// dialog.show(fm, DIALOG_DATE);

				ChoiseDialogFragment dialog = ChoiseDialogFragment
						.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOICE);
				dialog.show(fm, DIALOG_CHOICE);
			}
		});

		mCbCrimeSolved = (CheckBox) v.findViewById(R.id.cb_crime_solved);
		mCbCrimeSolved.setChecked(mCrime.isSolved());
		mCbCrimeSolved
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mCrime.setSolved(isChecked);

					}
				});
		mIbtnPicture = (ImageButton) v.findViewById(R.id.ibtn_crime_camera);
		mIbtnPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 隐式启动拍照
				// impliedTakePicture();

				// 启动自定义的拍照界面
				Intent intent = new Intent(getActivity(),
						CrimeCameraActivity.class);
				startActivityForResult(intent, TAKE_PICTURE_2);
			}
		});
		// If camera is not available, disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				&& !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mIbtnPicture.setEnabled(false);
		}

		mIvCamera = (ImageView) v.findViewById(R.id.iv_crime_camera);
		mIvCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 在对话框中展示图片
				if (mIvCamera.getDrawable() != null) {
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					ImageFragment imageFragment = ImageFragment
							.newInstance(mCrime.getPhoto());
					imageFragment.show(fm, DIALOG_IMAGE);
				}
			}
		});
		registerForContextMenu(mIvCamera);

		mBtnChooseSus = (Button) v.findViewById(R.id.btn_choose_suspect);
		mBtnChooseSus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 隐式启动联系人activity
				Intent contactsIntent = new Intent();
				contactsIntent.setAction(Intent.ACTION_PICK);
				// contactsIntent.setData(Uri.parse("content://contacts"));
				contactsIntent.setType(Phone.CONTENT_TYPE);
				startActivityForResult(contactsIntent, PICK_CONTACT);
			}
		});
		if (mCrime.getSuspect() != null && !"".equals(mCrime.getSuspect())) {
			mBtnChooseSus.setText(mCrime.getSuspect());
		}

		mBtnSend = (Button) v.findViewById(R.id.btn_send_crime_report);
		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 找具有分享功能的app，并将信息发送出去
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				sendIntent.setType(HTTP.PLAIN_TEXT_TYPE); // "text/plain" MIME
															// type
				
				if (sendIntent.resolveActivity(getActivity()
						.getPackageManager()) != null) {
					startActivity(sendIntent);
				}
			}

		});
		return v;
	}

	private String getCrimeReport() {

		String inFormat = "EEE,MMM dd";
		String dateString = (String) DateFormat.format(inFormat,
				mCrime.getDate());

		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.report_is_solved);
		} else {
			solvedString = getString(R.string.report_is_not_solved);
		}
		String suspectString = null;
		if (mCrime.getSuspect() != null && !"".equals(mCrime.getSuspect())) {
			suspectString = getString(R.string.report_suspect,
					mCrime.getSuspect());
		} else {
			suspectString = getString(R.string.report_no_suspect);

		}

		String crimeReport = getString(R.string.crime_report,
				mCrime.getTitle(), dateString, solvedString, suspectString);

		return crimeReport;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_DATE) {
			// 目前只更新日期
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_CRIME_DATE);
			mCrime.setDate(date);
			updateDate();
		}
		if (requestCode == TAKE_PICTURE) {
			showPicture1(data);
		}
		if (requestCode == TAKE_PICTURE_2) {
			String filename = data
					.getStringExtra(CrimeCameraFragment.PICTURE_FILENAME);
			Log.d(TAG, "filename" + filename);
			deletePic();
			Photo p = new Photo(filename);
			if (p != null) {
				mCrime.setPhoto(p);
				showPicture2();
			}
		}

		if (requestCode == PICK_CONTACT) {
			// TODO 找到返回的联系人信息
			Uri contactUri = data.getData();

			Cursor cursor = getActivity().getContentResolver()
					.query(contactUri, new String[] { Phone.NUMBER }, null,
							null, null);
			if (cursor.getCount() == 0) {
				cursor.close();
				return;
			}

			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(Phone.NUMBER);
			String number = cursor.getString(columnIndex);
			mBtnChooseSus.setText(number);
			mCrime.setSuspect(number);
			cursor.close();

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showPicture2() {
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String filename = mCrime.getPhoto().getFilename();
			String path = getActivity().getFilesDir().getAbsolutePath() + "/"
					+ filename;

			// 下面一种是经过封装的找到指定文件全路径的方法
			// String path = getActivity()
			// .getFileStreamPath(p.getFilename()).getAbsolutePath();

			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mIvCamera.setImageDrawable(b);
	}

	/**
	 * 用隐式的方法启动系统照相功能后展示图片的方法
	 * 
	 * @param data
	 * @deprecated
	 */
	private void showPicture1(Intent data) {
		// 检查结果是否包含缩略图
		if (data != null) {
			if (data.hasExtra("data")) {
				Log.d(TAG, "包含缩略图");
				Bitmap thombnail = data.getParcelableExtra("data");
				mIvCamera.setImageBitmap(thombnail);
			}
		} else {
			// 如果没有缩略图则图像存放在目标文件中
			Log.d(TAG, "不包含缩略图");
			layPicture();
		}
	}

	/**
	 * 放置图片
	 * 
	 * @deprecated
	 */
	private void layPicture() {

		int width = mIvCamera.getWidth();
		int height = mIvCamera.getHeight();

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(outFileUri.getPath(), opts);

		int imageWidth = opts.outWidth;
		int imageHeight = opts.outHeight;
		// 确定缩放比率
		int scaleFactor = Math.min(imageHeight / height, imageWidth / width);
		// 将图像文件解码为图像大小以填充视图
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scaleFactor;
		opts.inPurgeable = true;

		Bitmap bm = BitmapFactory.decodeFile(outFileUri.getPath(), opts);
		mIvCamera.setImageBitmap(bm);
	}

	/**
	 * 更新时间
	 */
	private void updateDate() {
		// 周一，01，12，2015
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,MM dd,yyyy");
		mBtnCrimeDate.setText(dateFormat.format(mCrime.getDate()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: // 系统提供的左上角的返回菜单

			// Intent intent=new Intent(getActivity(), CrimeListActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			// getActivity().finish();

			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}

			return true;

		case R.id.menu_crime_delete:
			CrimeLab.getInstance(getActivity()).deleteCrime(mCrime);
			getActivity().finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_crime, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_picture, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_delete_pic:
			deletePic();
			mIvCamera.setImageDrawable(null);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * 删除当前的图片，model和文件
	 */
	private void deletePic() {
		// 检查mcrime中是否已有照片，有的话就删除文件
		Photo oldPhoto = mCrime.getPhoto();
		if (oldPhoto != null) {
			String oldfilename = oldPhoto.getFilename();
			String path = getActivity().getFilesDir().getAbsolutePath() + "/"
					+ oldfilename;
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			oldPhoto.setFilename(null);
			mCrime.setPhoto(null);
		}
	}

	/**
	 * 隐式启动拍照
	 */
	private void impliedTakePicture() {
		String filename = UUID.randomUUID() + ".jpg";
		// 外部存储
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);

		// 内部私有存储
		// File file = new File(getActivity().getFilesDir().getAbsolutePath(),
		// filename);
		// outFileUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outFileUri);
		startActivityForResult(intent, TAKE_PICTURE);
	}

	@Override
	public void onStart() {
		showPicture2();
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mIvCamera);
	}

	@Override
	public void onPause() {
		CrimeLab.getInstance(getActivity()).saveCrimes();
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

}
