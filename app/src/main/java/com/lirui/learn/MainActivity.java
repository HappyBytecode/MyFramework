package com.lirui.learn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lirui.learn.face.FaceDetectActivity;
import com.lirui.learndemo.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.image.ImageWay;
import com.lirui.lib_common.image.MultiPreviewActivity;
import com.lirui.lib_common.net.download.DownloadEvent;
import com.lirui.lib_common.net.download.DownloadService;
import com.lirui.lib_common.version.VersionCheckManager;
import com.lirui.lib_common.view.ItemDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class MainActivity extends AbsBaseActivity {

    private List<String> titles = new ArrayList<>();

    private VersionCheckManager manager;
    private ImageWay imageWay;
    private RecyclerView rv_item;

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        titles.add("多个大图显示");
        titles.add("下载测试");
        titles.add("检测版本更新");
        titles.add("状态布局");
        titles.add("webview");
        titles.add("正在上映");
        titles.add("拍照");
        titles.add("图片选择");
        titles.add("人脸识别");

        imageWay = new ImageWay(this);

        rv_item = (RecyclerView) findViewById(R.id.rv_item);
        rv_item.addItemDecoration(new ItemDivider(1, 0x44444444));
        rv_item.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        TestAdapter adapter = new TestAdapter(R.layout.item_test, titles);
        rv_item.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0) {
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcadu-1y445.jpg");
                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcads-13a4.jpg");

                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcads-1j35.jpg");
                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcadt-15jj.jpg");

                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcadt-dtj.jpg");
                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcadt-14tz.jpg");

                    paths.add("http://img-download.pchome.net/download/1k1/31/4s/ofcadt-w9a.jpg");

                    MultiPreviewActivity.startActivity(MainActivity.this, paths, 3);
                    return;
                }
                if (position == 1) {
                    DownloadService.startService(MainActivity.this, "https://dl-sh-ctc-1.pchome.net/48/d8/com.tencent.tmgp.sgame_u146_1.20.1.21.apk?key=b376c7d36bc7e32da3c97bfe0d201739&tmp=1501233341110");
                    return;
                }
                if (position == 2) {
                    manager = new VersionCheckManager(MainActivity.this);
                    manager.versionUpdate(2, false, "1.更新内容1\n2.更新内容2\n3.更新内容3\n4.更新内容4\n",
                            "https://pro-app-qn.fir.im/7261c8d8b32c04fc33027e5b22295e89f42ffae4.apk?attname=app-release.apk_2.1.apk&e=1501749699&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:e3gU1drzm_64uqQ45NYQCAp9ORA=");
                    return;
                }
                if (position == 3) {
                    startActivity(new Intent(mContext, StatusActivity.class));
                    return;
                }
                if (position == 4) {
                    InTheaterActivity.startActivity(MainActivity.this);
                    return;
                }
                if (position == 5) {
                    imageWay.openCameraWithCrop(true, 1, 1);
                    return;
                }
                if (position == 6) {
                    startActivity(new Intent(MainActivity.this, WebWiewActivity.class));
                    return;
                }
                if (position == 7) {
                    startActivity(new Intent(mContext, SelectPhotoActivity.class));
                    return;
                }
                if (position == 8) {
                    startActivity(new Intent(mContext, FaceDetectActivity.class));
                    return;
                }
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageWay.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //上传完成
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadEvent event) {
        manager.updateDownloadProgress(event);
    }

}
