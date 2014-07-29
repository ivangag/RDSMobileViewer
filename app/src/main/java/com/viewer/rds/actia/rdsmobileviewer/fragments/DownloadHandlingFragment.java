package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;

/**
 * Created by igaglioti on 28/07/2014.
 */
public class DownloadHandlingFragment extends Fragment{

    private TaskDownloadCallbacks mCallbacks;
    private DownloadRequestSchema mDownloadRequest;


    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskDownloadCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        return rootView;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        //final DownloadRequestSchema downloadRequest = getArguments().getParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE);
        //startDownloadRequest(downloadRequest);
    }

    public void startDownloadRequest(DownloadRequestSchema downloadRequest) {
        if(downloadRequest != null) {
            DownloadUtility.getInstance().RequireDownloadAsyncTask(null, downloadRequest);
        }
    }


    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskDownloadCallbacks) activity;
        if(activity instanceof DownloadUtility.IRemoteDownloadDataListener)
            DownloadUtility.getInstance().addListener((DownloadUtility.IRemoteDownloadDataListener) activity);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static DownloadHandlingFragment newIstance(DownloadRequestSchema downloadRequestSchema) {

        DownloadHandlingFragment fragment = new DownloadHandlingFragment();
        Bundle args = new Bundle();
        args.putParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, downloadRequestSchema);
        fragment.setArguments(args);
        return fragment;
    }

}
