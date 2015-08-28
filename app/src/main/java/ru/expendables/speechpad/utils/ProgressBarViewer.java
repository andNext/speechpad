package ru.expendables.speechpad.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Сергей Прайм on 26.07.2015.
 */
public class ProgressBarViewer {
    private static ProgressDialog progress;
    public static void view (Activity activity, String message) {
        progress = new ProgressDialog(activity);
        progress.setMessage(message);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public static void hide () {
        if ((progress != null) && (progress.isShowing()))
            progress.dismiss();
    }
}
