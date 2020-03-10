package zelosin.pack.Data.ScienceWork;

import java.util.HashMap;
import java.util.Map;

public class ScienceWork {

    public Map<String, String> mScienceWorkInformation = new HashMap<String, String>();
    protected String mScienceWorkLink;
    public void parse(){};

    public ScienceWork(String mScienceWorkLink) {
        this.mScienceWorkLink = mScienceWorkLink;
    }

    public ScienceWork() {}

    public String getmScienceWorkLink() {
        return mScienceWorkLink;
    }
}
