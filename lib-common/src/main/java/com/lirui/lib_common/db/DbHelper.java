package com.lirui.lib_common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lirui.lib_common.util.Utils;

/**
 * 数据库
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Common.db";

    public static final String VERSION_TABLE_NAME = "version";
    private static final String CREATE_VERSION_TABLE = "create table version ( "
            + "id integer primary key autoincrement, "
            + "versionCode integer, "
            + "filePath text, "
            + "updateMsg text, "
            + "time text,"
            + "isMustUpdate integer)";

    private static final String DROP_TABLE = "drop table if exists version";

    public DbHelper() {
        /**
         * 参数说明：
         *
         * 第一个参数： 上下文
         * 第二个参数：数据库的名称
         * 第三个参数：null代表的是默认的游标工厂
         * 第四个参数：是数据库的版本号  数据库只能升级,不能降级,版本号只能变大不能变小
         */
        super(Utils.getContext(), DB_NAME, null, 1);
    }


    /**
     * onCreate是在数据库创建的时候调用的，主要用来初始化数据表结构和插入数据初始化的记录
     * <p>
     * 当数据库第一次被创建的时候调用的方法,适合在这个方法里面把数据库的表结构定义出来.
     * 所以只有程序第一次运行的时候才会执行
     * 如果想再看到这个函数执行，必须写在程序然后重新安装这个app
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VERSION_TABLE);
    }


    /**
     * 当数据库更新的时候调用的方法
     * 这个要显示出来得在上面的super语句里面版本号发生改变时才会 打印  （super(context, "itheima.db", null, 2); ）
     * 注意，数据库的版本号只可以变大，不能变小，假设我们当前写的版本号是3，运行，然后又改成1，运行则报错。不能变小
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
