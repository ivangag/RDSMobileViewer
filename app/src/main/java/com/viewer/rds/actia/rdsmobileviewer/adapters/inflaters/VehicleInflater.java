package com.viewer.rds.actia.rdsmobileviewer.adapters.inflaters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.adapters.BaseInflaterAdapter;
import com.viewer.rds.actia.rdsmobileviewer.adapters.IAdapterViewInflater;

/**
 * Created by Ivan on 12/07/2014.
 */
public class VehicleInflater implements IAdapterViewInflater<VehicleCustom> {


    @Override
    public View inflate(BaseInflaterAdapter<VehicleCustom> adapter, int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final VehicleCustom vehicleItem = adapter.getTItem(pos);

        if(convertView==null) {
            holder = new ViewHolder();
            // TODO - Inflate the View for this ToDoItem
            // from todo_item.xml.
            // inflate the layout

            LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();

            convertView = inflater.inflate(R.layout.fragment_main, parent, false);

            TextView txtVehicleInfo = (TextView) convertView.findViewById(R.id.section_label);
            //txtVehicleInfo.setText(vehicleItem.toString());

            holder.txtVIN = (TextView) convertView.findViewById(R.id.section_label);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtVIN;
    }
}
