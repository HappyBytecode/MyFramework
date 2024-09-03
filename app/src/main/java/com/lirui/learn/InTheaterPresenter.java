package com.lirui.learn;

import com.lirui.learn.bean.InTheaters;
import com.lirui.learn.net.API;
import com.lirui.learn.net.ApiService;
import com.lirui.lib_common.base.BasePresenter;
import com.lirui.lib_common.base.BaseView;
import com.lirui.lib_common.net.callback.NetSubscriber;

/**
 * Created by lirui on 2017/8/8.
 */

public class InTheaterPresenter extends BasePresenter {

    public InTheaterPresenter(BaseView view) {
        super(view);
    }

    public void inTheaters() {
        ApiService.inTheaters()
                .subscribe(new NetSubscriber<InTheaters>(API.in_theaters, this));
    }

}
