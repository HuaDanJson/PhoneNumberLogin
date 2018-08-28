package cool.phone.number.android;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.mob.MobSDK;

public class CCApplication extends Application {

    private static CCApplication INSTANCE;

    public static CCApplication getInstance() {
        return INSTANCE;
    }

    public CCApplication() {
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Utils.init(this);
        MobSDK.init(this);
    }
}
