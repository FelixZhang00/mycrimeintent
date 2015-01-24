package felix.example.crininalintent2.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class CrimeLab {

	private static CrimeLab sCrimeLab;
	private Context mAppContext;

	private ArrayList<Crime> mCrimes;

	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	private CrimeIntentJSONSerializer mSerializer;

	private CrimeLab(Context context) {
		this.mAppContext = context;
		// mCrimes = new ArrayList<Crime>();
		// for (int i = 0; i < 100; i++) {
		// Crime c = new Crime();
		// c.setTitle("Crime #" + i);
		// c.setSolved(i % 2 == 0);
		// mCrimes.add(c);
		// }
		mSerializer = new CrimeIntentJSONSerializer(mAppContext, FILENAME);
		try {
			mCrimes = mSerializer.loadCrime();
		} catch (Exception e) {
			Log.d(TAG, "loadCrime failed!");
			mCrimes=new ArrayList<Crime>();
			e.printStackTrace();
		}
		
	}

	public boolean saveCrimes() {

		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "success saving json");
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Erro saving json");
			e.printStackTrace();
		}

		return false;
	}

	public void addCrime(Crime c) {
		mCrimes.add(c);
	}
	public void deleteCrime(Crime c){
		mCrimes.remove(c);
	}

	public static CrimeLab getInstance(Context context) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(context.getApplicationContext());
		}
		return sCrimeLab;
	}

	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}

	public Crime getCrime(UUID id) {
		for (Crime c : mCrimes) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	
	public class CrimeIntentJSONSerializer {

		private Context mContext;
		private String mFilename;

		public CrimeIntentJSONSerializer(Context context, String filename) {
			super();
			mContext = context;
			mFilename = filename;
		}

		public void saveCrimes(ArrayList<Crime> crimes) throws JSONException,
				IOException {

			// Build an array in JSON
			JSONArray array = new JSONArray();
			for (Crime c : crimes) {
				array.put(c.toJSON()); // 将crime对象转化为json对象（需要在crime类中定义）存放在json数组中
			}

			// Write the file to disk
			Writer writer = null;
			try {
				OutputStream out = mContext.openFileOutput(mFilename,
						Context.MODE_PRIVATE);
				writer = new OutputStreamWriter(out);
				writer.write(array.toString());
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}

		public ArrayList<Crime> loadCrime() throws IOException, JSONException {
			ArrayList<Crime> crimes = new ArrayList<Crime>();

			BufferedReader reader = null;
			try {
				// Open and read the file into a StringBuilder
				InputStream in = mContext.openFileInput(mFilename);
				reader = new BufferedReader(new InputStreamReader(in));

				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				// Parse the JSON using JSONTokener
				JSONArray array = (JSONArray) new JSONTokener(sb.toString())
						.nextValue();

				// Build the array of crimes from JSONObjects
				for (int i = 0; i < array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}

			} catch (FileNotFoundException e) {
				// 捕捉当没有文件时的异常，不向外抛出
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
			return crimes;

		}
	}

}
