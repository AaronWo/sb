package com.haosu.schedulebook.db;

import android.os.Environment;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;

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
                    .setDbVersion(1)
                    .setAllowTransaction(true)
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                        }
                    });
        }
        return daoConfig;
    }

}
