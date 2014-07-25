package com.viewer.rds.actia.rdsmobileviewer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class CRDSCustomAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return mItems.size();
    }
    // Add a NetworkInfoItem to the adapter
    // Notify observers that the data set has changed
    public void add(CRDSCustom item) {

        this.mItems.add(item);
        //this.getFilter().filter(mLastFilter);
//        mDataNotifierCallBack.OnDataItemChanged();
        notifyDataSetChanged();
    }

    public void addRange(List<CRDSCustom> items) {

        this.mItems.addAll(items);
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Object)(mItems.get(position))).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final CRDSCustom crdsItem = (CRDSCustom)getItem(position);

        if(convertView == null) {
            holder = new ViewHolder();
            // TODO - Inflate the View for this ToDoItem
            // from todo_item.xml.
            // inflate the layout

            LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();

            convertView = inflater.inflate(R.layout.fragment_main, parent, false);

            TextView txtVehicleInfo = (TextView) convertView.findViewById(R.id.section_label);
            txtVehicleInfo.setText(crdsItem.toString());

            holder.txtCRDSGuid = (TextView) convertView.findViewById(R.id.section_label);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
    static class ViewHolder {
        TextView txtCRDSGuid;
    }

    public void clear() {
        mItems.clear();
    }

    private List<CRDSCustom> mItems;

    public CRDSCustomAdapter() {
        mItems = new ArrayList<CRDSCustom>();
    }
}
