package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.viewer.rds.actia.rdsmobileviewer.fragments.DownloadHandlerFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;

public class DownloadActivity extends Activity implements DownloadHandlerFragment.TaskDownloadCallbacks,
        DownloadRDSManager.IRemoteDownloadDataListener{

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        DownloadRDSManager.getInstance().removeListener(this);
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        DownloadRequestSchema downloadRequest = getIntent().getExtras().getParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, DownloadHandlerFragment.newInstance(downloadRequest))
                    //.add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.download, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

    }

    @Override
    public void onDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result) {

        Bundle args = new Bundle();
        args.putParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, requestType);
        args.putParcelable(DownloadRDSManager.DOWNLOAD_DATA_RESULT,result);
        Intent intent = new Intent();
        intent.putExtras(args);
        setResult(DownloadRDSManager.DOWNLOAD_RESULT_OK,intent);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_download, container, false);
            return rootView;
        }
    }
}
