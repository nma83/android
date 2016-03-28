package org.owntracks.android.messages;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.location.Geofence;

import org.json.JSONException;
import org.json.JSONObject;
import org.owntracks.android.App;
import org.owntracks.android.db.WaypointIn;
import org.owntracks.android.support.IncomingMessageProcessor;
import org.owntracks.android.support.OutgoingMessageProcessor;
import org.owntracks.android.support.Preferences;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageWaypointIn extends MessageBase {
    public static final String BASETOPIC_SUFFIX = "/waypoint";

    public String getBaseTopicSuffix() {  return BASETOPIC_SUFFIX; }

    private String desc;
    private double lon;
    private double lat;
    private long tst;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Integer getRad() {
        return rad;
    }

    public void setRad(Integer rad) {
        this.rad = rad;
    }

    public long getTst() {
        return tst;
    }

    public void setTst(long tst) {
        this.tst = tst;
    }

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processMessage(this);
    }

    @Override
    public void processOutgoingMessage(OutgoingMessageProcessor handler) {
        handler.processMessage(this);
    }


    public WaypointIn toDaoObject() {
        WaypointIn w = new WaypointIn();

        w.setTopic(getTopic());
        w.setDescription(getDesc());
        w.setGeofenceLatitude(getLat());
        w.setGeofenceLongitude(getLon());
        w.setGeofenceRadius(getRad());
        w.setDate(new Date(TimeUnit.SECONDS.toMillis(getTst())));

        return w;
    }

    public static MessageWaypointIn fromDaoObject(WaypointIn w) {
        MessageWaypointIn message = new MessageWaypointIn();
        message.setDesc(w.getDescription());
        message.setLat(w.getGeofenceLatitude());
        message.setLon(w.getGeofenceLongitude());
        message.setRad(w.getGeofenceRadius());
        message.setTst(TimeUnit.MILLISECONDS.toSeconds(w.getDate().getTime()));
        return message;
    }
}
