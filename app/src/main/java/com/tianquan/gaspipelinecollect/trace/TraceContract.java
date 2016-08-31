package com.tianquan.gaspipelinecollect.trace;

import com.tianquan.gaspipelinecollect.BasePresenter;
import com.tianquan.gaspipelinecollect.BaseView;

/**
 * Created by wzq on 16-8-24.
 */
public interface TraceContract {

    interface View extends BaseView<Presenter>{

        void showAddMarker();
    }

    interface Presenter extends BasePresenter {

        /*
         * 手动往地图里添加一个轨迹的标记
         */
        void addMarker();

        /*
         * 开始自动标记轨迹
         */
        void startAutoMarker();

        /*
         * 停止自动标记轨迹
         */
        void stopAutoMarker();

    }
}
