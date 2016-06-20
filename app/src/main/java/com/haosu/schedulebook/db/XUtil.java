package com.haosu.schedulebook.db;

import android.os.Environment;
import android.util.Log;

import com.haosu.schedulebook.model.ScheduleItem;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.ex.DbException;

import java.io.File;
import java.io.IOException;

/**
 * Created by haosu on 2016/4/21.
 */
public class XUtil {
    static DaoConfig daoConfig;

    public static DaoConfig getDaoConfig() {
        if (daoConfig == null) {
            daoConfig = new DaoConfig()
                    .setDbName("schedulebook.db")
                    .setDbVersion(2)
                    .setAllowTransaction(true)
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            if (oldVersion == 1 && newVersion == 2) {
                                try {
                                    db.addColumn(ScheduleItem.class, "myorder");
                                } catch (DbException e) {
                                    Log.e(this.getClass().getSimpleName(), "upgrade db error", e);
                                }
                            }
                        }
                    });
        }
        return daoConfig;
    }

}
