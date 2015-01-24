package felix.example.crininalintent2.fragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import felix.example.crininalintent2.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {

	private static final String TAG = "CrimeCameraFragment";

	public static final String PICTURE_FILENAME = "picture_filename";

	private Camera mCamera;
	private SurfaceView mSv;
	private Button mBtnTakePicture;
	private View mProgressContainer;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_camera, container,
				false);
		mSv = (SurfaceView) view.findViewById(R.id.sv_camera);
		SurfaceHolder holder = mSv.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// We can no longer display on this surface, so stop the
				// preview.
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// Tell the camera to use this surface as its preview area
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (Exception e) {
					Log.e(TAG, "Error to set preview display", e);
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (mCamera == null) {
					return;
				}

				Camera.Parameters parameters = mCamera.getParameters();
				// 照相预览的尺寸
				Size s = getBestSupportSize(
						parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);
				// 照相保存的尺寸
				s = getBestSupportSize(parameters.getSupportedPictureSizes(),
						width, height);
				parameters.setPictureSize(s.width, s.height);

				// 根据手机的方向设置照片的方向
				if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					parameters.set("orientation", "portrait");
					parameters.set("rotation", 90);
				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					parameters.set("orientation", "landscape");
					parameters.set("rotation", 90);
				}

				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Can't statr preview", e);
					mCamera.release();
					mCamera = null;
				}
			}

		});

		mBtnTakePicture = (Button) view.findViewById(R.id.btn_camera);
		mBtnTakePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					mCamera.takePicture(shutter, null, jpeg);
				}

			}
		});

		mProgressContainer = view
				.findViewById(R.id.fl_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);

		return view;
	}

	private ShutterCallback shutter = new ShutterCallback() {

		@Override
		public void onShutter() {
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};

	private PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 将拍照得来的二进制数据存放到应用的私有目录中
			boolean success = false;
			FileOutputStream fos = null;
			String filename = UUID.randomUUID() + ".jpg";
			try {
				fos = getActivity().openFileOutput(filename,
						Context.MODE_PRIVATE);
				fos.write(data);
				success = true;
			} catch (Exception e) {
				success = false;
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						success = false;
						e.printStackTrace();
					}
				}

			}

			// 根据保存是否成功，回传相应的结果
			if (success) {
				Intent intent = new Intent();
				intent.putExtra(PICTURE_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, intent);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();

		}
	};

	/**
	 * A simple algorithm to get the largest size available.
	 */
	private Size getBestSupportSize(List<Size> supportedPictureSizes,
			int width, int height) {
		Size bestSize = supportedPictureSizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : supportedPictureSizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onResume() {
		if (Build.VERSION.SDK_INT >= 9) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		super.onPause();
	}

}
