package org.owntracks.android.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Animatable;
import android.animation.ObjectAnimator;
import com.mikepenz.materialdrawer.Drawer;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.BR;
import org.owntracks.android.db.Waypoint;
import org.owntracks.android.db.WaypointIn;
import org.owntracks.android.databinding.ActivityClockBinding;
import org.owntracks.android.model.FusedContact;
import org.owntracks.android.model.GeocodableLocation;
import org.owntracks.android.services.ServiceLocator;
import org.owntracks.android.services.ServiceProxy;
import org.owntracks.android.support.ContactImageProvider;
import org.owntracks.android.support.DrawerProvider;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.Toasts;
import org.owntracks.android.support.WaypointCollection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.owntracks.android.support.RecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;
import me.tatarka.bindingcollectionadapter.factories.BindingRecyclerViewAdapterFactory;
import com.wnafee.vector.compat.ResourcesCompat;

public class ActivityClock extends ActivityBase
    implements RecyclerViewAdapter.ClickHandler, RecyclerViewAdapter.LongClickHandler, BindingRecyclerViewAdapterFactory {
    private static final String TAG = "ActivityClock";
    private ActivityClockBinding binding;
    private Bundle intentExtras;
    private float currAngle;
    private String activeContact;
    public static final int CLOCK_FACE_MAX_LABELS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_clock);
        binding.setVariable(BR.adapterFactory,this );
        binding.setViewModel(App.getContactsViewModel());

        toolbar = (Toolbar) findViewById(R.id.fragmentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getTitle());
        Drawer drawer = DrawerProvider.buildDrawer(this, toolbar);

        // Inflate compat anim drawable
        Drawable clockAnim = ResourcesCompat.getDrawable(this, R.drawable.avd);
        ImageView clockImg = (ImageView)findViewById(R.id.clock_face_img);
        clockImg.setImageDrawable(clockAnim);

        clockImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rotateClockHand((ImageView)v);
                }
            });

        // Load waypoint text
        if (activeContact != null)
            refreshClockFace(getDefaultContact());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        de.greenrobot.event.EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        de.greenrobot.event.EventBus.getDefault().unregister(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_showcontacts:
            Intent gotoContacts = new Intent(this, ActivityContacts.class);
            startActivity(gotoContacts);
            return true;
        case android.R.id.home:
            finish();
            return true;
        }

        return false;
    }
    
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v, Object viewModel) {
        String topic = ((FusedContact)viewModel).getTopic();
        Log.v(TAG, "onClick for topic=" + topic);

        refreshClockFace(topic);
    }
    
    @Override
    public void onLongClick(View v, Object viewModel) {

    }

    @Override
    public <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg) {
        return new RecyclerViewAdapter<>(this, this, arg);
    }

    private String getDefaultContact() {
        ArrayMap<String, WaypointCollection> allWayps;
        allWayps = App.getContactWaypoints();
        if (allWayps.isEmpty()) {
            return null;
        } else {
            return allWayps.keyAt(0);
        }
    }
    
    // Load labels into clock face
    private void refreshClockFace(String contact) {
        activeContact = contact;
        labelClockFace(contact);
    }
    
    private void labelClockFace(String contact) {
        WaypointCollection wayps;
        int numLabels;
        String [] labels;

        blankClockFaceLabels();
        
        if (App.getContactWaypoints().containsKey(contact))
            wayps = App.getContactWaypoints().get(contact);
        else {
            Log.v(TAG, "labelClockFace got no waypoints for " + contact);
            return;
        }
        
        if (wayps.size() < 2)
            numLabels = 0;
        if (wayps.size() < CLOCK_FACE_MAX_LABELS)
            numLabels = 2;
        else
            numLabels = CLOCK_FACE_MAX_LABELS;

        Log.v(TAG, "labelClockFace " + contact + "with " + numLabels + " labels");
        labels = new String[CLOCK_FACE_MAX_LABELS];
        
        if (numLabels == 0) {
            TextView log = (TextView)findViewById(R.id.clock_face_log);
            log.setText("Contact " + contact + " has too few waypoints defined");
        } else if (numLabels == 2) {
            labels[0] = wayps.keyAt(0);
            labels[2] = wayps.keyAt(1);
        } else {
            for (int l = 0; l < CLOCK_FACE_MAX_LABELS; l++)
                labels[l] = wayps.keyAt(l);
        }

        setClockFaceLabels(labels);
    }

    private void blankClockFaceLabels() {
        String blank = getResources().getString(R.string.empty_label);
        setClockFaceLabels(new String [] { blank, blank, blank, blank });
    }
    
    private void setClockFaceLabels(String [] labels) {
        int [] labelIds = { R.id.clock_face_0,
                         R.id.clock_face_3,
                         R.id.clock_face_6,
                         R.id.clock_face_9 };

        for (int l = 0; l < CLOCK_FACE_MAX_LABELS; l++)
            if (labels[l] != null)
                ((TextView)findViewById(labelIds[l])).setText(labels[l]);
    }
    
    // Clock hand animation
    private void rotateClockHand(ImageView v) {
        Drawable drawable = v.getDrawable();
        if (drawable instanceof Animatable) {
            float nextAngle = (currAngle + 30.0f);
            ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", currAngle, nextAngle);
            anim.setDuration(3000);
            anim.start();
            currAngle = nextAngle;
        }
    }

    // Event listeners
    public void onEventMainThread(Events.WaypointTransition waypTrans) {
        Waypoint wayp = waypTrans.getWaypoint();
        Log.v(TAG, "waypoint trans " + wayp.getDescription());
    }

    public void onEventMainThread(Events.WaypointInUpdated waypUpd) {
        WaypointIn wayp = waypUpd.getWaypointIn();
        Log.v(TAG, "waypoint-in add " + wayp.getDescription());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FusedContact e) {
        Log.v(TAG, "location update " + e.getFusedName() + "@" + e.getLatLng().toString());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.CurrentLocationUpdated e) {
        Log.v(TAG, "my location update " + e.getGeocodableLocation());
    }
}
