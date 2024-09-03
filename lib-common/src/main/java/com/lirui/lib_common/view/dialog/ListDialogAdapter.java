package com.lirui.lib_common.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lirui.lib_common.R;

import java.util.List;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/11/2 0002
 * 功能：
 */

public class ListDialogAdapter<T> extends BaseAdapter {

    private List<T> items;
    private LayoutInflater inflater;

    public ListDialogAdapter(Context context, List<T> items) {
        super();
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = inflater.inflate(
                    R.layout.item_dialog_list, viewGroup, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.item.setText(items.get(i).toString());
        return view;
    }

    class ViewHolder {
        TextView item;

        public ViewHolder(View view) {
            item = (TextView) view.findViewById(R.id.tv_item);
        }
    }
}
