package felix.example.crininalintent2.activity;

import felix.example.crininalintent2.R;
import felix.example.crininalintent2.fragment.CrimeListFragment;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	protected int getLayoutResId() {

		return R.layout.activity_twopane;
	}

}
