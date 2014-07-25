package com.viewer.rds.actia.rdsmobileviewer.adapters;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ivan on 12/07/2014.
 */
public interface IAdapterViewInflater<T> {

    public View inflate(BaseInflaterAdapter<T> adapter, int pos, View convertView, ViewGroup parent);
}
