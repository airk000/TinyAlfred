package com.airk.tool.tinyalfred;

import android.app.Activity;
import android.view.View;
import android.util.Log;

/**
 * Created by kevin on 15/3/17.
 */
public class TinyAlfred {
    private static final String TAG = TinyAlfred.class.getSimpleName();

    public static final String SUFFIX = "$$Alfred";
    private static boolean debug = false;

    /**
     * Switch debug mode
     *
     * @param debug true to print Logcat, otherwise false
     */
    public static void setDebug(boolean debug) {
        TinyAlfred.debug = debug;
    }

    /**
     * Process annotation in an Activity.
     *
     * @param activity the Activity instance
     */
    public static void process(Activity activity) {
        processInternal(activity, activity);
    }

    /**
     * Process annotation in a Fragment or a custom Holder class.
     *
     * @param belong the Fragment or Holder instance
     * @param view   the content View
     */
    public static void process(Object belong, View view) {
        processInternal(belong, view);
    }

    @SuppressWarnings("unchecked")
    private static void processInternal(Object belong, Object root) {
        try {
            Class<?> clazz = Class.forName(belong.getClass().getName() + SUFFIX);
            Alfred alfred = (Alfred) clazz.newInstance();
            alfred.handleViews(belong, root);
            if (debug) {
                Log.d(TAG, clazz.getName() + ".handleViews(...) has been invoked.");
            }
        } catch (ClassNotFoundException ignore) {
            if (debug) {
                Log.e(TAG, "Class " + belong.getClass().getName() + " doesn't have any TinyAlfred's annotation.");
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static View findView(Object root, int id, String desc) {
        if (root instanceof Activity) {
            return ((Activity) root).findViewById(id);
        } else if (root instanceof View) {
            return ((View) root).findViewById(id);
        } else {
            throw new IllegalArgumentException("Only Activity or View can findView, now is " + root.getClass().getSimpleName());
        }
    }

}
