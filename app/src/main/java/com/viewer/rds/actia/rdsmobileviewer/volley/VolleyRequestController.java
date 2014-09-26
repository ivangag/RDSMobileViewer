package com.viewer.rds.actia.rdsmobileviewer.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by igaglioti on 05/09/2014.
 */
public class VolleyRequestController {
    private static VolleyRequestController mInstance;
    private Context mCtx;
    private RequestQueue mRequestQueue;

    public static VolleyRequestController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRequestController(context);
        }
        return mInstance;
    }

    private VolleyRequestController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
