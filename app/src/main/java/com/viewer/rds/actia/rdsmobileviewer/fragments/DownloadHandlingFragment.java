package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;

/**
 * Created by igaglioti on 28/07/2014.
 */
public class DownloadHandlingFragment extends Fragment implements DownloadManager.IRemoteDownloadDataListener{

    private TaskDownloadCallbacks mCallbacks;
    private DownloadRequestSchema mDownloadRequest = null;
    private int mDownloadRequestCountPending = 0;

    @Override
    public void onDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result) {
        --mDownloadRequestCountPending;
    }

    public int getDownloadRequestCountPending() {
        return mDownloadRequestCountPending;
    }


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
        super.onCreateView(inflater,container,savedInstanceState);
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
        DownloadManager.getInstance().addListener(this);
    }

    public void startDownloadRequest(DownloadRequestSchema downloadRequest) {
        mDownloadRequest = downloadRequest;
        if(mDownloadRequest != null) {

            setDownloadMsg();

            DownloadManager.getInstance().RequireDownloadAsyncTask(null, mDownloadRequest);
            ++mDownloadRequestCountPending;
        }
    }

    private void setDownloadMsg() {
        TextView txtLoading = (TextView) getActivity().findViewById(R.id.txt_progress_loading);
        if((txtLoading != null) && (mDownloadRequest != null)){
            txtLoading.setText(String.format(getResources().getString(R.string.progress_loading_text),
                    mDownloadRequest.getDownloadRequestType().getLocalizedName(getActivity())));
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDownloadMsg();
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
        /*
        if(activity instanceof DownloadManager.IRemoteDownloadDataListener)
            DownloadManager.get().addListener((DownloadManager.IRemoteDownloadDataListener) activity);
            */
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance().removeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

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
