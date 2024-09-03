package com.lirui.lib_common.view.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lirui.lib_common.R;

import java.util.List;

/**
 * 只有列表的Dialog
 */
public class ListDialog<T> {

    private Context context;

    private AlertDialog mDialog;
    private ListView listview;

    private List<T> items;
    private ListDialogAdapter adapter;

    public ListDialog(Context context) {
        this.context = context;
        mDialog = new AlertDialog.Builder(context, R.style.dialog).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog_list, (ViewGroup) null);
        listview = (ListView) view.findViewById(R.id.lv_menu);

        mDialog.show();
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        mDialog.cancel();
    }

    public void setItems(List<T> items) {
        this.items = items;
        adapter = new ListDialogAdapter<T>(context,items);
        listview.setAdapter(adapter);
    }

    public void show() {
        mDialog.show();
    }

    public void setItemClickListener(OnItemClickListener l) {
        listview.setOnItemClickListener(l);
    }
}

