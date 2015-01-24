package felix.example.crininalintent2.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo implements Serializable {

	public static final String FILE_NAME = "file_name";
	private String mFilename;

	public Photo(String filename) {
		super();
		this.mFilename = filename;
	}

	public Photo(JSONObject jsonObject) throws JSONException {
		 mFilename = jsonObject.getString(FILE_NAME);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(FILE_NAME, mFilename);
		return jsonObject;
	}

	public String getFilename() {
		return mFilename;
	}

	public void setFilename(String filename) {
		this.mFilename = filename;
	}

}
