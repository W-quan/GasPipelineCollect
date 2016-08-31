package com.tianquan.gaspipelinecollect;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.orhanobut.logger.Logger;
import com.tianquan.gaspipelinecollect.record.FormController;

import java.io.File;

/**
 * Created by wzq on 16-8-25.
 */
public class MyApplication extends Application {
    private static Context sContext;

    //Storage paths
    public static final String ODK_ROOT = Environment.getExternalStorageDirectory()
            + File.separator + "GasPipelineODK";
    public static final String FORMS_PATH = ODK_ROOT + File.separator + "forms";
    public static final String INSTANCES_PATH = ODK_ROOT + File.separator + "instances";
    public static final String CACHE_PATH = ODK_ROOT + File.separator + ".cache";
    public static final String METADATA_PATH = ODK_ROOT + File.separator + "metadata";
    public static final String TMPFILE_PATH = CACHE_PATH + File.separator + "tmp.jpg";
    public static final String TMPDRAWFILE_PATH = CACHE_PATH + File.separator + "tmpDraw.jpg";
    public static final String TMPXML_PATH = CACHE_PATH + File.separator + "tmp.xml";
    public static final String LOG_PATH = ODK_ROOT + File.separator + "log";
    public static final String DEFAULT_FONTSIZE = "21";
    public static final String OFFLINE_LAYERS = ODK_ROOT + File.separator + "OfflineLayers";

    private FormController mFormController = null;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void createODKDirs() throws RuntimeException {
        String cardstatus = Environment.getExternalStorageState();
        if (!cardstatus.equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("cannot access the Storage. Perhaps it is mounted via USB? (Current state: "
                    + cardstatus + ")");
        }

        String[] dirs = {
                ODK_ROOT, FORMS_PATH, INSTANCES_PATH, CACHE_PATH, METADATA_PATH, OFFLINE_LAYERS
        };

        for (String dirName : dirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                Logger.i("Directory Does Not exist");
                if (!dir.mkdirs()) {
                    throw new RuntimeException("ODK reports :: Cannot create directory: " + dirName);
                }
            } else {
                if (!dir.isDirectory()) {
                    Logger.i("Directory exist");
                    throw new RuntimeException("ODK reports :: " + dirName + " exists, but is not a directory");
                }
            }
        }
    }

    public static FormController getFormController() {
        return mFormController;
    }

    public void setFormController(FormController formController) {
        mFormController = formController;
    }
}
