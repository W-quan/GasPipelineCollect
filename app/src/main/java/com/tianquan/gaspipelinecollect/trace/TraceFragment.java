package com.tianquan.gaspipelinecollect.trace;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tianquan.gaspipelinecollect.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 轨迹图
 */
public class TraceFragment extends Fragment implements TraceContract.View, Marker.OnMarkerDragListener {

    @BindView(R.id.map) MapView mMapView;
    private IMapController mMapController;
    private OnFragmentInteractionListener mListener;

    private TraceContract.Presenter mPresenter;
    private MyLocationNewOverlay mLocationOverlay;

    private Polyline mPolyline;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private ArrayList<GeoPoint> mPoints = new ArrayList<>();

    public TraceFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TraceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TraceFragment newInstance() {
        return new TraceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new TracePresenter(this);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onResume() {
        super.onResume();


        mMapController = mMapView.getController();

        mMapView.setMultiTouchControls(true);
        mMapView.setMinZoomLevel(5);

        mMapController.setZoom(5);
        mMapController.setCenter(new GeoPoint(23.13893, 113.36881));

        if (mLocationOverlay == null) {
            mLocationOverlay = new MyLocationNewOverlay(mMapView);
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
            mMapController.setZoom(15);
            mMapView.getOverlays().add(mLocationOverlay);
        }

        if (mPolyline == null) {
            mPolyline = new Polyline(this.getContext());
            mPolyline.setColor(R.color.red);

            mMapView.getOverlays().add(mPolyline);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationOverlay != null) {
            mLocationOverlay.disableMyLocation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mPresenter.unsubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trace, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.trace_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_marker:
                break;
            case R.id.menu_start:
                Snackbar.make(mMapView, "start", Snackbar.LENGTH_LONG).show();
                mPresenter.subscribe();
                break;
            case R.id.menu_pause:
                Snackbar.make(mMapView, "pause", Snackbar.LENGTH_LONG).show();
                mPresenter.stopAutoMarker();
                break;
            case R.id.menu_stop:
                Snackbar.make(mMapView, "stop", Snackbar.LENGTH_LONG).show();
                mPresenter.stopAutoMarker();
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showAddMarker() {
        Marker marker = new Marker(mMapView);
        GeoPoint myLocation = mLocationOverlay.getMyLocation();
        if (myLocation == null) {
            return;
        }
        marker.setPosition(myLocation);
        Float acuracy = mLocationOverlay.getMyLocationProvider().getLastKnownLocation().getAccuracy();
        marker.setSubDescription(Float.toString(acuracy));
        marker.setDraggable(true);
        marker.setOnMarkerDragListener(this);

        mMapView.getOverlays().add(marker);

        mMarkers.add(marker);

        mPoints.add(myLocation);
        mPolyline.setPoints(mPoints);

        mMapView.invalidate();
    }

    @Override
    public void setPresenter(TraceContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Snackbar.make(view, "Add Survey", Snackbar.LENGTH_SHORT) .show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updatePolyline();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    private void updatePolyline() {
        mPoints.clear();
        for (int i = 0; i < mMarkers.size(); i++) {
            mPoints.add(mMarkers.get(i).getPosition());
        }
        mPolyline.setPoints(mPoints);
        mMapView.invalidate();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
