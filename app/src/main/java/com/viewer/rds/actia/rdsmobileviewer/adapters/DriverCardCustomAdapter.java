package com.viewer.rds.actia.rdsmobileviewer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igaglioti on 11/07/2014.
 */
public class DriverCardCustomAdapter extends BaseAdapter implements Filterable {

    private List<DriverCardData> mItems;

    public DriverCardCustomAdapter() {
        mItems = new ArrayList<DriverCardData>();
    }

    public void add(DriverCardData item) {

        this.mItems.add(item);
        notifyDataSetChanged();
    }

    public void addRange(List<DriverCardData> items) {

        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mItems.size();
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

        final DriverCardData cardItem = (DriverCardData)getItem(position);

        if(convertView==null) {
            holder = new ViewHolder();
            // TODO - Inflate the View for this ToDoItem
            // from todo_item.xml.
            // inflate the layout

            LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();

            convertView = inflater.inflate(R.layout.fragment_main, parent, false);

            TextView txtVehicleInfo = (TextView) convertView.findViewById(R.id.section_label);
            txtVehicleInfo.setText(cardItem.toString());

            holder.txtDriverID = (TextView) convertView.findViewById(R.id.section_label);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public void clear() {
        mItems.clear();
    }

    static class ViewHolder {
        TextView txtDriverID;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
