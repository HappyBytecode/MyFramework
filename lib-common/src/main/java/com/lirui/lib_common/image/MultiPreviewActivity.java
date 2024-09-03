package com.lirui.lib_common.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lirui.lib_common.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.image.loader.ILoader;
import com.lirui.lib_common.image.loader.LoaderManager;
import com.lirui.lib_common.image.loader.OnProgressListener;
import com.lirui.lib_common.util.FormatTools;
import com.lirui.lib_common.util.SizeUtils;
import com.lirui.lib_common.view.CircleProgress;
import com.lirui.lib_common.view.IntensifyImage.IntensifyImageView;

import java.util.ArrayList;

/**
 * 多图展示
 */
public class MultiPreviewActivity extends AbsBaseActivity implements ViewPager.OnPageChangeListener {

    public static String PICTURE_URLS = "PICTURE_URLS";
    public static String POSITION = "POSITION";

    private int width = SizeUtils.dp2px(8);

    private ViewPager mViewPager;
    private LinearLayout mLlIndication;
    private ImagePageAdapter mAdapter;
    private LayoutInflater inflater;

    private ArrayList<String> mPictures = new ArrayList<>();
    private int selectPosition;

    public static void startActivity(Activity activity, ArrayList<String> pictures, int position) {
        Intent intent = new Intent(activity, MultiPreviewActivity.class);
        intent.putStringArrayListExtra(PICTURE_URLS, pictures);
        intent.putExtra(POSITION, position);
        activity.startActivity(intent);
    }

    @Override
    protected void getExtra(Intent intent) {
        mPictures = intent.getStringArrayListExtra(PICTURE_URLS);
        selectPosition = intent.getIntExtra(POSITION, 0);
        if (mPictures == null || mPictures.size() == 0) {
            finish();
        }
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        inflater = LayoutInflater.from(this);
        mViewPager = (ViewPager) findViewById(R.id.vp_pager);
        mLlIndication = (LinearLayout) findViewById(R.id.ll_indication);
        mAdapter = new ImagePageAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mPictures == null ? 0 : mPictures.size());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(selectPosition);
        mLlIndication.post(new Runnable() {
            @Override
            public void run() {
                initIndicationView();
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_multi_preview;
    }

    private class ImagePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPictures == null ? 0 : mPictures.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.item_preview, container, false);
            final IntensifyImageView iiv = (IntensifyImageView) view.findViewById(R.id.iiv);
            final CircleProgress cp = (CircleProgress) view.findViewById(R.id.cp);
            LoaderManager.getLoader().loadPath(iiv, mPictures.get(position)
                    , new ILoader.Options().OnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgressUpdate(String url, final int progress, final DownloadStatusEnum status) {
                            switch (status) {
                                case START:
                                    cp.setVisibility(View.VISIBLE);
                                    break;
                                case SUCCESS:
                                    cp.setVisibility(View.GONE);
                                    break;
                                case ERROR:
                                    cp.setVisibility(View.GONE);
                                    iiv.setImage(FormatTools.Drawable2InputStream(getResources().getDrawable(R.mipmap.ic_load_error)));
                                    break;
                                case LOADING:
                                    cp.setProgress(progress);
                                    break;
                            }

                        }
                    }));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 初始化指示器
     */
    private void initIndicationView() {
        mLlIndication.removeAllViews();
        int size = mPictures == null ? 0 : mPictures.size();
        if (size <= 1) return;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        params.leftMargin = width / 2;
        params.rightMargin = width / 2;
        for (int index = 0; index < size; index++) {
            View view = new View(this);
            view.setBackgroundResource(R.drawable.selector_indication_circle);
            mLlIndication.addView(view, params);
        }
        updateSelected();
    }

    /**
     * 更新选中指示器状态
     */
    private void updateSelected() {
        if (mLlIndication.getChildCount() <= 0) return;
        for (int index = 0; index < mLlIndication.getChildCount(); index++) {
            mLlIndication.getChildAt(index).setSelected(false);
        }
        mLlIndication.getChildAt(selectPosition).setSelected(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectPosition = position;
        updateSelected();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
