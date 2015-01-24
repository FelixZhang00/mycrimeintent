package felix.example.crininalintent2.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import felix.example.crininalintent2.R;
import felix.example.crininalintent2.R.id;
import felix.example.crininalintent2.fragment.CrimeFragment;
import felix.example.crininalintent2.model.Crime;
import felix.example.crininalintent2.model.CrimeLab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class CrimePageActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		mCrimes = CrimeLab.getInstance(this).getCrimes();

		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentPagerAdapter(fm) {

			@Override
			public int getCount() {
				return mCrimes.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return CrimeFragment.newInstance(mCrimes.get(arg0).getId());
			}
		});

		UUID crimeId = (UUID) getIntent().getSerializableExtra(
				CrimeFragment.EXTRA_CRIME_ID);
		for(int i=0;i<mCrimes.size();i++){
			Crime c=mCrimes.get(i);
			if(c.getId().equals(crimeId)){
				mViewPager.setCurrentItem(i);
				setTitle(c.getTitle());
				break;
			}
		}
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				String title=mCrimes.get(arg0).getTitle();
				if (title!=null) {
					setTitle(title);					
				}else{
					setTitle("");
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
	}

}
