package hcmute.edu.vn.mssv18110328.utils;

import android.app.Application;
import com.google.gson.Gson;

public class App extends Application {
    private static App mSelf;

    public static App self() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
    }
}