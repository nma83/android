package org.owntracks.android.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import org.owntracks.android.db.Waypoint;
import org.owntracks.android.db.WaypointIn;

import org.owntracks.android.db.WaypointDao;
import org.owntracks.android.db.WaypointInDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig waypointDaoConfig;
    private final DaoConfig waypointInDaoConfig;

    private final WaypointDao waypointDao;
    private final WaypointInDao waypointInDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        waypointDaoConfig = daoConfigMap.get(WaypointDao.class).clone();
        waypointDaoConfig.initIdentityScope(type);

        waypointInDaoConfig = daoConfigMap.get(WaypointInDao.class).clone();
        waypointInDaoConfig.initIdentityScope(type);

        waypointDao = new WaypointDao(waypointDaoConfig, this);
        waypointInDao = new WaypointInDao(waypointInDaoConfig, this);

        registerDao(Waypoint.class, waypointDao);
        registerDao(WaypointIn.class, waypointInDao);
    }
    
    public void clear() {
        waypointDaoConfig.getIdentityScope().clear();
        waypointInDaoConfig.getIdentityScope().clear();
    }

    public WaypointDao getWaypointDao() {
        return waypointDao;
    }

    public WaypointInDao getWaypointInDao() {
        return waypointInDao;
    }

}
