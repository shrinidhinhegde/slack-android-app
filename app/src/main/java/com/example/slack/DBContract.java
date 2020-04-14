package com.example.slack;

import android.provider.BaseColumns;

public class DBContract {

    private DBContract(){}

    public static final class DBEntry implements BaseColumns{
        public static final String TABLE_NAME = "message";
        public static final String COL_1 = "body";
        public static final String COL_2 = "number";
    }
}
