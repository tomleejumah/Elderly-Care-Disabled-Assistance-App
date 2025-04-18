package com.example.elderCare.app;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.elderCare.app.models.FBFav;
import com.example.elderCare.app.ui.activities.ChatActivity;
import com.example.elderCare.app.ui.activities.UserListingActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.maps.android.SphericalUtil;
import com.twilio.video.VideoView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MapsFragment extends Fragment
        implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, RoutingListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_ALL = "type_all";

    //Radius for nearby place search
    public static final int RADIUS = 1500;

    private static final String TAG = MapsFragment.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;

    //Used for annotating map
    private static final String CLEAR_CHARACTER = "*";
    private static final String ROUTE_CHARACTER = "=";
    private static final String POINT_SEPERATOR = "!";
    private static final String LAT_LNG_SEPERATOR = ",";
    private static final String USER_TAG = "person";
    private static View fragmentView;

    // A default location (University of Melbourne, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-37.7964, 144.9612);

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Annotate annotations;
    private Annotate assistedRoute;
    private ArrayList<LatLng> waypoints;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;
    private MapView mapView;
    private FirebaseFunctions mFunctions;

    private FloatingActionButton annotateButton;
    private FloatingActionButton undoButton;
    private FloatingActionButton cancelButton;
    private FloatingActionButton clearButton;
    private FloatingActionButton sendButton;
    private FloatingActionButton selectButton;
    private Button requestButton;
    private Button disconnectButton;

    //Communication buttons
    private FloatingActionButton chatButton;
    private FloatingActionButton callButton;

    //Video views
    public static VideoView primaryVideoView;
    public static VideoView thumbnailVideoView;

    //Nearby buttons
    private ImageButton restaurantButton;
    private ImageButton cafeButton;
    private ImageButton taxiButton;
    private ImageButton stationButton;
    private ImageButton atmButton;
    private ImageButton hospitalButton;
    private FloatingActionButton exitNearby;
    private FloatingActionButton startNearby;


    //search bar autocomplete
    private SupportPlaceAutocompleteFragment placeAutoComplete;
    private LatLng destPlace;
    private Place dest;
    private String destAddress;
    private Marker destMarker;
    private ArrayList<Marker> destRouteMarker = new ArrayList<>();
    private Polyline line = null;
    private TextView txtDistance, txtTime;
    private LocationManager locationManager = null;
    //Global flags
    private boolean firstRefresh = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;
    private SupportPlaceAutocompleteFragment autocompleteFragment;

    private boolean isCarer;

    private LatLng connectedUserLocation;
    private Marker connectedUserMarker;
    private String connectedUserName;

    //Receive annotations
    private final BroadcastReceiver mAnnotationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String points = (intent.getExtras()).getString("points");
            awaitingPoints(points);
        }
    };

    //Receive call requests
    private final BroadcastReceiver mCallConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isConnected = intent.getBooleanExtra("isConnected", false);
            if (isConnected) {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.green_500)));
            } else {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
            }
        }
    };

    //Send location to connected user
    private final BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getParcelableExtra("bundle");

            connectedUserLocation = bundle.getParcelable("location");
            connectedUserName = intent.getStringExtra("name");

            if (connectedUserMarker != null) {
                connectedUserMarker.remove();
                connectedUserMarker = null;
            }

            connectedUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(connectedUserLocation)
                    .title(connectedUserName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person))
            );
            connectedUserMarker.setTag(USER_TAG);
        }
    };

    //Receive disconnection requests
    private final BroadcastReceiver mDisconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
            db.collection("users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                disconnectButton.setVisibility(View.GONE);
                if (isCarer) {
                    getEmptyPath();
                    removeDestRouteMarkers();
                }
                if (connectedUserMarker != null) {
                    connectedUserMarker.remove();
                    connectedUserMarker = null;
                }
                hideCommunicationButtons();
                if (!(boolean) Objects.requireNonNull(task.getResult().getData()).get("isCarer")) {
                    requestButton.setVisibility(View.VISIBLE);
                } else {
                    hideAnnotationButtons(getView());
                }
            });
        }
    };

    //Receive connection requests
    private final BroadcastReceiver mConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            db.collection("users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (isCarer) {
                    getEmptyPath();
                    removeDestRouteMarkers();
                }
                requestButton.setVisibility(View.INVISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                showCommunicationButtons();
                if ((boolean) Objects.requireNonNull(task.getResult().getData()).get("isCarer")) {
                    annotateButton.show();
                }
            });
        }
    };

    //Recieve notification when map style is updated in toggle map section and r-edraw
    private final BroadcastReceiver mStyleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            filterMap();
        }
    };

    private ArrayList<SOS> sosList = new ArrayList<>();

    //Receive sos signals
    private final BroadcastReceiver mSOSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getParcelableExtra("bundle");

            String id = intent.getStringExtra("id");
            LatLng location = bundle.getParcelable("location");
            String name = intent.getStringExtra("name");

            for (SOS s : sosList) {
                if (s.id.equals(id)) {
                    return;
                }
            }

            SOS mySos = new SOS(id,
                    location,
                    intent.getStringExtra("name"),
                    mMap.addMarker(new MarkerOptions()
                            .position((location))
                            .title(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_sos_marker))
                    ));
            mySos.marker.setTag(USER_TAG);

            sosList.add(mySos);
        }
    };

    //Receive OK signals after an sos
    private final BroadcastReceiver mOKReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            for (SOS s : sosList) {
                if (s.id.equals(id)) {
                    s.marker.remove();
                    sosList.remove(s);
                    return;
                }
            }
        }
    };

    public static MapsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mFunctions = FirebaseFunctions.getInstance();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int availabilityResult = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (fragmentView == null)
            fragmentView = inflater.inflate(R.layout.maps_fragment, container, false);
        bindViews(fragmentView);

        MapsInitializer.initialize((getActivity()));

        //Get Map if available
        if (availabilityResult == ConnectionResult.SUCCESS) {
            mapView = fragmentView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            if (mapView != null) {
                (mapView).getMapAsync(this);
            }
        } else {
            Toast.makeText(getActivity(), "SERVICE UNAVAILABLE", Toast.LENGTH_LONG).show();
        }

        //Add autocomplete fragment to the view
        placeAutoComplete = new SupportPlaceAutocompleteFragment();
        getChildFragmentManager().beginTransaction().
                replace(R.id.place_autocomplete_container, placeAutoComplete).
                commitAllowingStateLoss();

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                getEmptyPath();
                removeDestRouteMarkers();
                dest = place;
                destPlace = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(place.getLatLng().latitude,
                                place.getLatLng().longitude), DEFAULT_ZOOM));

                //remove old marker
                if (destMarker != null)
                    destMarker.remove();
                removeDestRouteMarkers();
                // add marker to Destination

                ArrayList<String> snipArray = new ArrayList<>();
                snipArray.add(String.format("%,.1f", place.getRating()));
                snipArray.add("Tap to add this place to favrourites!");
                snipArray.add(place.getId());
                snipArray.add((place.getAddress()).toString().replaceAll(",", " "));
                snipArray.add(place.getLatLng().latitude + "");
                snipArray.add(place.getLatLng().longitude + "");


                destAddress = place.getAddress().toString();
                //getPlacePhotos(place.getId());

                destMarker = mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName().toString())
                        .snippet(snipArray.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                firstRefresh = true;
                getRoutingPath();
                sendRoute();
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Initialise annotations
        annotations = new Annotate(mMap);
        assistedRoute = new Annotate(mMap);
        assistedRoute.setStyle("B");

        //Annotation buttons
        annotateButton = fragmentView.findViewById(R.id.addAnnotations);
        undoButton = fragmentView.findViewById(R.id.undo_button);
        cancelButton = fragmentView.findViewById(R.id.cancel_button);
        clearButton = fragmentView.findViewById(R.id.clear_button);
        sendButton = fragmentView.findViewById(R.id.send_button);

        // Communication buttons
        chatButton = fragmentView.findViewById(R.id.chatButton);
        callButton = fragmentView.findViewById(R.id.callButton);

        //Video views
        primaryVideoView = fragmentView.findViewById(R.id.primary_video);
        thumbnailVideoView = fragmentView.findViewById(R.id.thumbnail_video);

        //Nearby buttons
        restaurantButton = fragmentView.findViewById(R.id.Restauarant);
        cafeButton = fragmentView.findViewById(R.id.Cafe);
        taxiButton = fragmentView.findViewById(R.id.Taxi);
        stationButton = fragmentView.findViewById(R.id.Station);
        atmButton = fragmentView.findViewById(R.id.ATM);
        hospitalButton = fragmentView.findViewById(R.id.Hospital);
        exitNearby = fragmentView.findViewById(R.id.closeNearbyButton);
        startNearby = fragmentView.findViewById(R.id.openNearbyButton);
        selectButton = fragmentView.findViewById(R.id.selectButton);
        hideNearbyButtons(getView());

        // hide communication buttons
        hideCommunicationButtons();

        //Sets annotation buttons to invisible
        hideAnnotationButtons(getView());

        //Request carer button
        requestButton = fragmentView.findViewById(R.id.requestCarer);
        requestButton.setVisibility(View.INVISIBLE);

        //Disconnect Button
        disconnectButton = fragmentView.findViewById(R.id.disconnect);
        disconnectButton.setVisibility(View.GONE);

        // Button on click listeners
        annotateButton.setOnClickListener(this::annotateButtonClicked);
        undoButton.setOnClickListener(this::undoButtonClicked);
        cancelButton.setOnClickListener(this::cancelButtonClicked);
        clearButton.setOnClickListener(this::clearButtonClicked);
        sendButton.setOnClickListener(this::sendButtonClicked);
        requestButton.setOnClickListener(this::getCarer);
        disconnectButton.setOnClickListener(this::disconnectUser);

        // Communication button on click listeners
        chatButton.setOnClickListener(this::startChatActivity);
        callButton.setOnClickListener(this::callClickListener);

        //Nearby on click listeners
        restaurantButton.setOnClickListener(v -> getNearby("restaurant"));
        cafeButton.setOnClickListener(v -> getNearby("cafe"));
        taxiButton.setOnClickListener(v -> getNearby("taxi_stand"));
        stationButton.setOnClickListener(v -> getNearby("train_station"));
        atmButton.setOnClickListener(v -> getNearby("atm"));
        hospitalButton.setOnClickListener(v -> getNearby("hospital"));
        exitNearby.setOnClickListener(this::hideNearbyButtons);
        startNearby.setOnClickListener(this::showNearbyButtons);
        selectButton.setOnClickListener(view -> {
            MarkerOptions tempMarker = new MarkerOptions();
            tempMarker
                    .position(destMarker.getPosition())
                    .title(destMarker.getTitle())
                    .snippet(destMarker.getSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.clear();
            mMap.addMarker(tempMarker);

            getRoutingPath();
            selectButton.hide();
        });

        //Shows buttons depending on what type of user
        db.collection("users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
            isCarer = (boolean) task.getResult().getData().get("isCarer");
            if (!(boolean) Objects.requireNonNull(task.getResult().getData()).get("isCarer")) {
                hideAnnotationButtons(getView());
                requestButton.setVisibility(View.VISIBLE);
            }

            if (task.getResult().getData().get("connectedUser") != null) {
                if ((boolean) task.getResult().getData().get("isCarer")) {
                    annotateButton.show();
                }
                disconnectButton.setVisibility(View.VISIBLE);
                requestButton.setVisibility(View.INVISIBLE);
                showCommunicationButtons();
            }
        });

        connectedUserMarker = null;
        connectedUserLocation = null;
        connectedUserName = null;

        // Register broadcast receivers
        LocalBroadcastManager.getInstance((this.getContext())).registerReceiver((mAnnotationReceiver),
                new IntentFilter("annotate")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mLocationReceiver),
                new IntentFilter("location")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mDisconnectReceiver),
                new IntentFilter("disconnect")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mConnectReceiver),
                new IntentFilter("connect")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mStyleReceiver),
                new IntentFilter("style")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mSOSReceiver),
                new IntentFilter("sos")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mOKReceiver),
                new IntentFilter("ok")
        );
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver((mCallConnectReceiver),
                new IntentFilter("call")
        );

        return fragmentView;
    }

    private void bindViews(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerViewAllUserListing = view.findViewById(R.id.recycler_view_all_user_listing);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient((getActivity()), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        LocalBroadcastManager.getInstance((this.getContext())).unregisterReceiver(mAnnotationReceiver);
    }

    @Override
    public void onDestroyView() {
        if (getActivity() != null) {
            getChildFragmentManager().beginTransaction().remove(placeAutoComplete).commitAllowingStateLoss();
        }
        super.onDestroyView();

        // Unregister broadcast receivers
        LocalBroadcastManager.getInstance((this.getContext())).unregisterReceiver((mAnnotationReceiver));
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver((mLocationReceiver));
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver((mDisconnectReceiver));
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver((mConnectReceiver));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        placeAutoComplete.onActivityResult(requestCode, resultCode, data);
    }

    private void awaitingPoints(String pointsAsString) {
        //Prepare annotation for new polyline
        annotations.setMap(mMap);
        annotations.newAnnotation();
        String id = "";
        boolean recievingRoute = false;

        //clear annotations if containing clear character
        if (pointsAsString.contains(CLEAR_CHARACTER)) {
            annotations.clear();
        }
        //otherwise parse string to array list of LatLngs
        else {
            if (pointsAsString.contains(ROUTE_CHARACTER)) {
                recievingRoute = true;
                pointsAsString = pointsAsString.replaceFirst(ROUTE_CHARACTER, "");
                id = pointsAsString.substring(0, pointsAsString.indexOf(ROUTE_CHARACTER)).replaceAll(" ", "");
                pointsAsString = pointsAsString.substring(pointsAsString.indexOf(ROUTE_CHARACTER) + 1, pointsAsString.length() - 1);
            }
            //split string between points
            String[] pointsAsStringArray = pointsAsString.split(POINT_SEPERATOR);
            ArrayList<LatLng> points = new ArrayList<>();

            for (String p : pointsAsStringArray) {
                //split string into latitude and longitude
                String[] latLong = p.split(LAT_LNG_SEPERATOR);
                try {
                    //test for correct format
                    if (p.length() >= 2) {
                        LatLng point = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));
                        points.add(point);
                    }
                } catch (Exception e) {
                    Log.d("Annotation points", pointsAsString);
                }
            }

            //once parsed, draw the lines on map
            if (recievingRoute && connectedUserMarker != null) {
                RouteToConnectedUsersRoute(points, id);
            } else if (!recievingRoute) {
                annotations.drawMultipleLines(points);
            }
        }
    }


    public void RouteToConnectedUsersRoute(ArrayList<LatLng> waypoints, String id) {
        destPlace = waypoints.get(waypoints.size() - 1);

        getMultiRoutingPath(waypoints);

        if (isCarer) {
            assistedRoute.clear();
            assistedRoute.setMap(mMap);
            assistedRoute.drawMultipleLines(waypoints);
        } else {
            sendRoute();
        }

        if (!id.equals("")) {
            final Task<PlaceBufferResponse> placeResponse = mGeoDataClient.getPlaceById(id);
            placeResponse.addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    //dest Place obj is first one
                    if ((task.getResult()).getCount() > 0)
                        dest = task.getResult().get(0);

                    addDestMark(id);
                }

            });
        } else {
            if (!isCarer)
                addFavLocationRouteMarker(waypoints);
        }
    }

    public void RouteToFavouriteLocation() {
        Bundle extras = (getActivity()).getIntent().getExtras();
        if (extras == null || !extras.containsKey("favLat")) {
            Log.d("Map-Fav", "no extra key");
            return;
        }
        Double dLat = Double.parseDouble((getActivity().getIntent().getExtras()).getString("favLat"));
        Double dLng = Double.parseDouble(getActivity().getIntent().getExtras().getString("favLng"));
        String place_id = getActivity().getIntent().getExtras().getString("place_id").replaceAll(" ", "");
        getEmptyPath();
        removeDestRouteMarkers();
        destPlace = new LatLng(dLat, dLng);

        final Task<PlaceBufferResponse> placeResponse = mGeoDataClient.getPlaceById(place_id);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //dest Place obj is first one
                if ((task.getResult()).getCount() > 0) {
                    dest = task.getResult().get(0);
                    firstRefresh = true;
                    getRoutingPath();
                    sendRoute();
                }
            }


        });

        addFavLocationMarker();
    }


    //calc route ,called from main
    public void RouteToFavouriteRoute() {
        Bundle extras = (getActivity()).getIntent().getExtras();
        if (extras == null || !extras.containsKey("favWayPoints")) {
            return;
        }

        List<String> wayPointString = Arrays.asList(((getActivity().getIntent().getExtras()).getString("favWayPoints")).split(","));

        ArrayList<LatLng> waypoints = new ArrayList<>();

        //convert pairs of string value into lat lng
        for (int i = 0; i < wayPointString.size() - 1; i += 2) {
            LatLng newLatLng = new LatLng(Double.parseDouble(wayPointString.get(i)), Double.parseDouble(wayPointString.get(i + 1)));
            waypoints.add(newLatLng);
        }

        destPlace = null;
        addFavLocationRouteMarker(waypoints);

        firstRefresh = true;

        getMultiRoutingPath(waypoints);
        sendRoute();
    }

    private void addDestMark(String id) {

        if (destMarker != null)
            destMarker.remove();
        removeDestRouteMarkers();
        // add marker to Destination
        destMarker = mMap.addMarker(new MarkerOptions()
                .position(destPlace)
                .title(dest.getName().toString())
                .snippet("and snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private void addFavLocationMarker() {
        if (destMarker != null)
            destMarker.remove();
        removeDestRouteMarkers();
        // add marker to Destination
        destMarker = mMap.addMarker(new MarkerOptions()
                .position(destPlace)
                .title(((getActivity()).getIntent().getExtras()).getString("favTitle"))
                .snippet("and snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                destPlace, DEFAULT_ZOOM));
    }

    private void addFavLocationRouteMarker(ArrayList<LatLng> waypoints) {
        if (destMarker != null)
            destMarker.remove();
        removeDestRouteMarkers();
        // add marker to Destination

        int index = 1;
        for (LatLng pt : waypoints) {

            destRouteMarker.add(
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(pt)
                                    .title("The #" + index + " Destination")
                                    .snippet("from your favourite route")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
            );
            index++;
        }
    }

    private void removeDestRouteMarkers() {
        if (destMarker != null)
            destMarker.remove();

        if (destRouteMarker == null)
            return;

        for (Marker m : destRouteMarker) {
            m.remove();
        }
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
        firstRefresh = false;
        //Ensure the GPS is ON and location permission enabled for the application.
        locationManager = (LocationManager) (getActivity()).getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "ERROR: Cannot start location listener", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPause() {
        if (locationManager != null) {
            //Check needed in case of  API level 23.

            if (ContextCompat.checkSelfPermission((getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            try {
                locationManager.removeUpdates((LocationListener) getActivity());
            } catch (Exception ignored) {
            }
        }
        locationManager = null;
        super.onPause();
        mapView.onPause();
    }


    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        } else if (item.getItemId() == R.id.map_to_chat) {
            startActivity(new Intent(getActivity(), MainActivity.class));

        } else if (item.getItemId() == R.id.menu_to_contacts) {
            UserListingActivity.startActivity(getActivity(),
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        filterMap();
        mMap.setOnMarkerClickListener(this);

        // Change the location of MYLocation button to bottom right location
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);


        }

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTag() == USER_TAG) {
                    return null;
                }
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (getView()).findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                String snipData = marker.getSnippet().substring(1, marker.getSnippet().length() - 1);
                List<String> myList = new ArrayList<>(Arrays.asList(snipData.split(",")));

                if (marker.getSnippet() == null || myList.size() < 6) {
                    return infoWindow;
                }

                String ratingNum = myList.get(0);

                TextView snippet = infoWindow.findViewById(R.id.snippet);


                //snippet.setText("Rating: "+ratingNum+"/5.0 for "+dest.getAddress());
                snippet.setText(myList.get(3));


                RatingBar ratingbar = infoWindow.findViewById(R.id.ratingBar);
                ratingbar.setNumStars(5);
                ratingbar.setRating(Float.parseFloat(ratingNum));

                //Temporary location for addition of routes by clicking marker
                destPlace = marker.getPosition();

                return infoWindow;
            }
        });

        //save this place to firestore
        mMap.setOnInfoWindowClickListener(marker -> {
            String snipData = marker.getSnippet().substring(1, marker.getSnippet().length() - 1);
            List<String> myList = new ArrayList<>(Arrays.asList(snipData.split(",")));

            if (marker.getSnippet() == null || myList.size() < 6) {
                return;
            }
            final String placeid = myList.get(2).replace(" ", "");//id of this place;

            FBFav fav = new FBFav(
                    placeid,
                    marker.getTitle(),
                    //destImage,
                    new GeoPoint(Double.parseDouble(myList.get(4)), Double.parseDouble(myList.get(5))),
                    myList.get(3),
                    1,
                    Timestamp.now().getSeconds()
            );

            final DocumentReference reference = FirebaseFirestore.getInstance()
                    .collection("users").document((FirebaseAuth.getInstance().getCurrentUser()).getUid());
            reference.collection("fav").document(placeid).get()
                    .addOnCompleteListener(task0 -> {

                        if (task0.isSuccessful()) {
                            if (!(task0.getResult()).exists()) {

                                //only add to firebase if not exist

                                reference.collection("fav")
                                        .document(placeid)
                                        .set(fav)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Log.d("saveFav", "now added");
                                            }
                                        });
                            }
                        }
                    });
        });

        // Prompt the user for permission.
        Permissions.getPermissions(getContext(), getActivity());

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // On click listener for annotations
        mMap.setOnMapClickListener(arg0 -> {
            if (annotations.isAnnotating()) {
                annotations.setMap(mMap);
                annotations.drawLine(arg0);
            }
        });
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (Permissions.hasLocationPermission(getContext())) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((getActivity()), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        //route to fav place if excist
                        RouteToFavouriteLocation();

                        placeAutoComplete.setBoundsBias(new LatLngBounds(
                                new LatLng(mLastKnownLocation.getLatitude() - 0.1, mLastKnownLocation.getLongitude() - 0.1),
                                new LatLng(mLastKnownLocation.getLatitude() + 0.1, mLastKnownLocation.getLongitude() + 0.1)));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);


                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (Permissions.hasLocationPermission(getContext())) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getCount();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                // Build a list of likely places to show the user.
                                mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                        .getAddress();
                                mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                        .getAttributions();
                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            // Release the place likelihood buffer, to avoid memory leaks.
                            likelyPlaces.release();

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.
                            openPlacesDialog();

                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            Permissions.getPermissions(getContext(), getActivity());
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = mLikelyPlaceLatLngs[which];
            String markerSnippet = mLikelyPlaceAddresses[which];
            if (mLikelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            destPlace = markerLatLng;
            if (destMarker != null)
                destMarker.remove();
            destMarker = mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(mLikelyPlaceNames[which])
                    .snippet("and snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            firstRefresh = true;

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));
            Log.d("Map", "get placccccccccccccccccccc");
            getRoutingPath();
            sendRoute();
        };


        // Display the dialog.
        new AlertDialog.Builder((getActivity()))
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }


    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void togglePlaces(int index) {
        LatLng markerLatLng = mLikelyPlaceLatLngs[index];
        String markerSnippet = mLikelyPlaceAddresses[index];
        if (mLikelyPlaceAttributions[index] != null) {
            markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[index];
        }

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        if (markerLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(mLikelyPlaceNames[index])
                    .snippet("and snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (Permissions.hasLocationPermission(getContext())) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                Permissions.getPermissions(getContext(), getActivity());
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * When a marker is clicked, set it as a destination
     * Allow a user to route to location
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            marker.showInfoWindow();
            destMarker = marker;
            destPlace = marker.getPosition();
            selectButton.show();
        }
        return true;
    }

    /**
     * Method to draw the google routed path.
     */
    private void getRoutingPath() {
        LatLng startingLocation = getStartingLocation();
        if (startingLocation == null || destPlace == null)
            return;

        try {

            //Do Routing
            if (!isCarer || connectedUserLocation == null) {
                Routing routing = new Routing.Builder()
                        .key("AIzaSyCJJY5Qwt0Adki43NdMHWh9O88VR-dEByI")
                        .travelMode(Routing.TravelMode.WALKING)
                        .withListener(this)
                        .waypoints(startingLocation, destPlace)
                        .build();
                routing.execute();

                ArrayList<LatLng> points = new ArrayList<>();
                points = (ArrayList) routing.get().get(0).getPoints();
                points.add(destPlace);
                this.waypoints = points;
            } else {
                ArrayList<LatLng> points = new ArrayList<>();
                points.add(destPlace);
                this.waypoints = points;
            }
        } catch (Exception e) {
            Log.d("Map", "getRoutingPath faillllllllllll");
        }


    }

    /**
     * Method to draw the google routed path that connects multiple waypoints.
     */
    private void getMultiRoutingPath(List<LatLng> wayPoints) {
        if (wayPoints == null)
            return;
        try {
            if (!isCarer || connectedUserLocation == null) {
                //insert current location into array
                LatLng startingLocation = getStartingLocation();
                wayPoints.add(0, startingLocation);

                //Do Routing
                Routing routing = new Routing.Builder()
                        .key("AIzaSyCJJY5Qwt0Adki43NdMHWh9O88VR-dEByI")
                        .travelMode(Routing.TravelMode.WALKING)
                        .withListener(this)
                        .waypoints(wayPoints)
                        .alternativeRoutes(true)
                        .build();
                routing.execute();

                ArrayList<LatLng> points = new ArrayList<>();
                points = (ArrayList) routing.get().get(0).getPoints();
                this.waypoints = points;
            } else {
                this.waypoints = (ArrayList) wayPoints;
            }
        } catch (Exception e) {
            Log.d("Map", "getRoutingPath faillllllllllll");
        }
    }

    /**
     * Method to wipe google routed path
     */
    private void getEmptyPath() {
        try {
            //insert current location into array
            LatLng startingLocation = getStartingLocation();

            //Do Routing
            Routing routing = new Routing.Builder()
                    .key("AIzaSyCJJY5Qwt0Adki43NdMHWh9O88VR-dEByI")
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(startingLocation, startingLocation)
                    .alternativeRoutes(true)
                    .build();
            routing.execute();


            this.waypoints = new ArrayList<>();
            this.waypoints.add(startingLocation);
        } catch (Exception e) {
            Log.d("Map", "getRoutingPath faillllllllllll");
        }
    }

    private LatLng getStartingLocation() {

        if (isCarer && connectedUserMarker != null) {
            return connectedUserLocation;
        } else {
            return new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(getActivity(), "Routing Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> list, int i) {
        try {
            //Get all points and plot the polyLine route.
            List<LatLng> listPoints = list.get(0).getPoints();
            PolylineOptions options = new PolylineOptions().width(15).color(Color.rgb(51, 153, 255)).geodesic(true);
            for (LatLng data : listPoints) {
                options.add(data);
            }

            //If line not null then remove old polyline routing.
            if (line != null) {
                line.remove();
            }
            line = mMap.addPolyline(options);

            //Show distance and duration.
            txtDistance.setText("Distance: " + list.get(0).getDistanceText());
            txtTime.setText("Duration: " + list.get(0).getDurationText());

            //Focus on map bounds
            mMap.moveCamera(CameraUpdateFactory.newLatLng(list.get(0).getLatLgnBounds().getCenter()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            builder.include(destPlace);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        } catch (Exception ignored) {

        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng curLatLng = new LatLng(lat, lng);

        // Removes SOS tokens that are more than 1Km away from the user
        for (SOS mySos : sosList) {
            if (SphericalUtil.computeDistanceBetween(mySos.location, curLatLng) > 1000) {
                mySos.marker.remove();
                sosList.remove(mySos);
            }
        }

        if (firstRefresh && destMarker != null) {
            //Add Start Marker.
            firstRefresh = false;
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
            getRoutingPath();
            if (!isCarer && connectedUserMarker != null)
                sendRoute();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public void getCarer(View v) {
        Toast.makeText(getContext(), "Requesting a carer", Toast.LENGTH_SHORT).show();
        requestCarer().addOnSuccessListener(s -> Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show());
    }

    public void disconnectUser(View v) {
        Toast.makeText(getContext(), "Disconnecting from User", Toast.LENGTH_SHORT).show();
        clearButtonClicked(getView());
        disconnect().addOnSuccessListener(s -> {
            annotations.setAnnotating(false);
            assistedRoute.clear();
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
            disconnectButton.setVisibility(View.GONE);
            if (connectedUserMarker != null)
                connectedUserMarker.remove();
            connectedUserMarker = null;
            hideCommunicationButtons();
            db.collection("users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (!(boolean) Objects.requireNonNull(task.getResult().getData()).get("isCarer")) {
                    requestButton.setVisibility(View.VISIBLE);
                } else {
                    hideAnnotationButtons(getView());
                }
            });
        });
    }

    private void showCommunicationButtons() {
        chatButton.show();
        callButton.show();
    }

    private void hideCommunicationButtons() {
        chatButton.hide();
        callButton.hide();
    }

    //Show nearby UI
    private void showNearbyButtons(View v) {
        restaurantButton.setVisibility((View.VISIBLE));
        cafeButton.setVisibility((View.VISIBLE));
        taxiButton.setVisibility((View.VISIBLE));
        stationButton.setVisibility((View.VISIBLE));
        atmButton.setVisibility((View.VISIBLE));
        hospitalButton.setVisibility((View.VISIBLE));
        exitNearby.show();
        startNearby.hide();
    }

    //Hide nearby UI
    private void hideNearbyButtons(View v) {
        restaurantButton.setVisibility((View.INVISIBLE));
        cafeButton.setVisibility((View.INVISIBLE));
        taxiButton.setVisibility((View.INVISIBLE));
        stationButton.setVisibility((View.INVISIBLE));
        atmButton.setVisibility((View.INVISIBLE));
        hospitalButton.setVisibility((View.INVISIBLE));
        exitNearby.hide();
        startNearby.show();
    }

    //Hide buttons related to annotations
    private void hideAnnotationButtons(View v) {
        annotateButton.hide();
        undoButton.hide();
        cancelButton.hide();
        clearButton.hide();
        sendButton.hide();
    }


    //ANNOTATION BUTTONS

    private void annotateButtonClicked(View v) {
        annotateButton.hide();
        undoButton.show();
        cancelButton.show();
        clearButton.show();
        sendButton.show();
        annotations.setAnnotating(true);
    }

    private void cancelButtonClicked(View v) {
        hideAnnotationButtons(v);
        annotateButton.show();
        annotations.setAnnotating(false);
    }

    private void undoButtonClicked(View v) {
        annotations.undo();
    }

    private void clearButtonClicked(View v) {
        annotations.clear();
        sendClearedAnnotations();

        //failsafe incase user was not connected when clear was sent
        annotations.setUndoHasOccurred(true);
    }

    public void sendButtonClicked(View v) {
        Toast.makeText(getContext(), "Sending annotation", Toast.LENGTH_SHORT).show();
        if (annotations.hasUndoOccurred()) {
            sendClearedAnnotations().addOnSuccessListener(s -> sendAllAnnotations()).addOnFailureListener(f -> Log.d("send button", "failure"));
        } else {
            sendAllAnnotations();
        }
        annotations.newAnnotation();
        annotations.setUndoHasOccurred(false);
    }

    //CLOUD FUNCTION CALLS

    private Task<String> requestCarer() {
        return mFunctions
                .getHttpsCallable("requestCarer")
                .call()
                .continueWith(task -> (String) (task.getResult()).getData());
    }

    /**
     * Take JSON file stored on device and use contained data to toggle map to user preferences
     */
    private void filterMap() {
        if (mMap != null) {
            try {
                //Attempt to open the file from device storage
                FileInputStream stream = (getActivity()).getApplicationContext().openFileInput("toggleMap");
                if (stream != null) {
                    //Read contents of file
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder totalContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalContent.append(line).append('\n');
                    }
                    //Pass JSON style string to maps style to hide components
                    MapStyleOptions style = new MapStyleOptions(totalContent.toString());
                    mMap.setMapStyle(style);
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found", e);
            } catch (IOException e) {
                Log.e(TAG, "File reading error", e);
            }
        }
    }


    /**
     * Create a URL for a request to the Google Places API
     *
     * @param latitude   LatLng describing location of the user
     * @param longitude  Radial distance to confine search
     * @param nearbyType Descriptor for kind of desired location
     * @return
     */
    public String buildUrl(double latitude, double longitude, String nearbyType) {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location=").append(latitude).append(",").append(longitude);
        placeUrl.append("&radius=" + RADIUS);
        placeUrl.append("&type=").append(nearbyType);
        placeUrl.append("&key=AIzaSyCJJY5Qwt0Adki43NdMHWh9O88VR-dEByI");

        System.out.println(placeUrl.toString());

        return placeUrl.toString();
    }

    private Task<String> disconnect() {
        Log.d("Onyx", "DISCONNECTING");
        return mFunctions
                .getHttpsCallable("disconnect")
                .call()
                .continueWith(task -> (String) (task.getResult()).getData());
    }

    //Send all annotations on carer's map to connected user
    private void sendAllAnnotations() {
        ArrayList<ArrayList<GeoPoint>> points = annotations.getAnnotations();
        if (points.size() > 0) {
            for (ArrayList<GeoPoint> p : points) {
                if (p.size() > 0)
                    sendAnnotation(p)
                            .addOnFailureListener(f -> Log.d("send button", "sent annotation failed"))
                            //removes p from list of points to send next time
                            .addOnSuccessListener(f -> annotations.successfulSend(p));
            }

        }
        //send empty list
        else {
            sendAnnotation(new ArrayList<>())
                    .addOnFailureListener(f -> Log.d("send", "failure"));
        }
    }

    private Task<String> sendRoute() {
        Map<String, Object> newRequest = new HashMap<>();
        StringBuilder annotationToString = new StringBuilder(" ");
        annotationToString.append(ROUTE_CHARACTER);
        if (dest != null)
            annotationToString.append(dest.getId());
        annotationToString.append(ROUTE_CHARACTER);

        //encode arraylist as string
        for (LatLng l : waypoints) {
            annotationToString.append(Double.toString(l.latitude)).append(LAT_LNG_SEPERATOR);
            annotationToString.append(Double.toString(l.longitude)).append(POINT_SEPERATOR);
        }


        //call cloud function and send encoded points to connected user
        newRequest.put("points", annotationToString.toString());
        return mFunctions
                .getHttpsCallable("sendAnnotation")
                .call(newRequest)
                .continueWith(task -> (String) task.getResult().getData());
    }

    //Sends an individual annotation (or polyline) to the connected user
    private Task<String> sendAnnotation(ArrayList<GeoPoint> points) {
        Map<String, Object> newRequest = new HashMap<>();
        StringBuilder annotationToString = new StringBuilder(" ");

        //encode arraylist as string
        for (GeoPoint g : points) {
            annotationToString.append(Double.toString(g.getLatitude())).append(LAT_LNG_SEPERATOR);
            annotationToString.append(Double.toString(g.getLongitude())).append(POINT_SEPERATOR);
        }

        //call cloud function and send encoded points to connected user
        newRequest.put("points", annotationToString.toString());
        return mFunctions
                .getHttpsCallable("sendAnnotation")
                .call(newRequest)
                .continueWith(task -> (String) Objects.requireNonNull(task.getResult()).getData());
    }

    //Clears the connected users map of all annotations
    private Task<String> sendClearedAnnotations() {
        //sends coded clear character to connected user
        Map<String, Object> newRequest = new HashMap<>();
        newRequest.put("points", CLEAR_CHARACTER);
        return mFunctions
                .getHttpsCallable("sendAnnotation")
                .call(newRequest)
                .continueWith(task -> (String) Objects.requireNonNull(task.getResult()).getData());
    }


    /**
     * Get all nearby places of given TYPE within a certain RADIUS from a certain LOCATION as
     * specified in the buildUrl method
     * Executes asynchronous function
     *
     * @param type Google Places definition for kind of location
     */
    public void getNearby(String type) {
        mMap.clear();
        hideNearbyButtons(getView());
        String Url = buildUrl(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), type);
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = Url;
        getNearbyPlaces getNearbyPlaces = new getNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
    }

    // onClickListener
    private void startChatActivity(View v) {
        //String id = FirebaseData.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String uid = documentSnapshot.get("connectedUser").toString();
                ChatActivity.startActivity(getActivity(), "Carer", uid);
            }
        });
    }

    private void callClickListener(View v) {

        ((MainActivity) getActivity()).fragChange(0);
    }

}
