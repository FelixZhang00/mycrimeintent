package felix.example.crininalintent2.fragment;

import java.io.IOException;
import java.util.Date;

import felix.example.crininalintent2.R;
import felix.example.crininalintent2.model.Photo;
import felix.example.crininalintent2.utils.PictureUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	public static final String EXTRA_CRIME_PHOTO = "felix.example.crininalintent2.crime_photo";
	private Photo mPhoto;
	private ImageView mImageView;

	public static ImageFragment newInstance(Photo photo) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_PHOTO, photo);
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_image, container, false);
		mPhoto=(Photo) getArguments().getSerializable(EXTRA_CRIME_PHOTO);
		mImageView = (ImageView) view.findViewById(R.id.iv_picture_detail);
		BitmapDrawable b = null;
		String filename = mPhoto.getFilename();
		String path = getActivity().getFilesDir().getAbsolutePath() + "/"
				+ filename;
		try {
			ExifInterface exif=new ExifInterface(path);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		b = PictureUtils.getScaledDrawable(getActivity(), path);
		mImageView.setImageDrawable(b);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}
}
