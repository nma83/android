package org.owntracks.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.owntracks.android.messages.MessageWaypoint;

public  class Dao {
    private static final String NAME = "org.owntracks.android.db";
    private static final String TAG = "Dao";
    private static SQLiteDatabase db;
    private static WaypointDao waypointDao;
    private static WaypointInDao waypointInDao;

    public static void initialize(Context c) {

        DaoMaster.OpenHelper helper = new DaoMaster.OpenHelper(c, NAME, null) {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.v(TAG, "onUpdate from: " + oldVersion + " to:"+newVersion);
                DaoMaster.dropAllTables(db, true);
                onCreate(db);
            }
        };


        db = helper.getWritableDatabase();
        Log.v(TAG, "version: "         + db.getVersion());

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        waypointDao = daoSession.getWaypointDao();
        waypointInDao = daoSession.getWaypointInDao();
    }


    public static SQLiteDatabase getDb() { return db; }

    public static WaypointDao getWaypointDao() {  return waypointDao; }
    public static WaypointInDao getWaypointInDao() {  return waypointInDao; }


}
