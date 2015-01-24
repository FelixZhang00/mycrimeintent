package felix.example.crininalintent2.model;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

public class Crime {

	private String mTitle;
	private UUID mId;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;

	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_DATE = "date";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";

	public Crime() {
		super();
		mId = UUID.randomUUID();
		mDate = new Date();
	}

	public Crime(JSONObject jsonObject) throws JSONException {
		mId = UUID.fromString(jsonObject.getString(JSON_ID));
		mDate = new Date(jsonObject.getLong(JSON_DATE));
		mSolved = jsonObject.getBoolean(JSON_SOLVED);
		mSuspect = jsonObject.getString(JSON_SUSPECT);
		if (jsonObject.has(JSON_TITLE)) {
			mTitle = jsonObject.getString(JSON_TITLE);
		}
		if (jsonObject.has(JSON_PHOTO)) {
			mPhoto = new Photo(jsonObject.getJSONObject(JSON_PHOTO));
		}
		if (jsonObject.has(JSON_SUSPECT)) {
			mSuspect = jsonObject.getString(JSON_SUSPECT);
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(JSON_ID, this.mId.toString());
		jsonObject.put(JSON_DATE, this.mDate.getTime());
		jsonObject.put(JSON_SOLVED, mSolved);
		jsonObject.put(JSON_TITLE, mTitle);
		jsonObject.put(JSON_SUSPECT, mSuspect);

		// 键值对内部再套键值对的情况
		if (mPhoto != null) {
			jsonObject.put(JSON_PHOTO, mPhoto.toJSON());
		}

		return jsonObject;

	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}

	public String getSuspect() {
		return mSuspect;
	}

	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	@Override
	public String toString() {
		return mTitle;
	}

}
