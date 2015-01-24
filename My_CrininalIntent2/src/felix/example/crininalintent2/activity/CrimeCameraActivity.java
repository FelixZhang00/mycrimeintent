package felix.example.crininalintent2.activity;

import felix.example.crininalintent2.fragment.CrimeCameraFragment;
import felix.example.crininalintent2.fragment.CrimeFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);
	}

	@Override
	protected Fragment createFragment() {

		return new CrimeCameraFragment();
	}

}
