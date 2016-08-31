package com.tianquan.gaspipelinecollect.trace;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wzq on 16-8-25.
 */
public class TracePresenter implements TraceContract.Presenter {

    @NonNull private TraceContract.View mTraceView;
    @NonNull private CompositeSubscription mSubscription;

    public TracePresenter(@NonNull TraceContract.View traceView) {
        mTraceView = traceView;

        mSubscription = new CompositeSubscription();
        mTraceView.setPresenter(this);
    }

    @Override
    public void addMarker() { }

    @Override
    public void subscribe() {
        startAutoMarker();
    }

    @Override
    public void startAutoMarker() {
        Subscription autoMarkerSub = Observable.interval(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Logger.d("addMarkerTimer: %d", aLong);
                        mTraceView.showAddMarker();
                    }
                });
        mSubscription.add(autoMarkerSub);
    }

    @Override
    public void stopAutoMarker() {
        mSubscription.clear();
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

}
