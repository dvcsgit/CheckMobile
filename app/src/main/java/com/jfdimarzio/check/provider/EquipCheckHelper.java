package com.jfdimarzio.check.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.jfdimarzio.check.Database.DatabaseContext;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.dao.rform.RFormMaterialResult;
import com.jfdimarzio.check.model.CheckDateFormatItem;
import com.jfdimarzio.check.model.DoneTotalItem;
import com.jfdimarzio.check.model.JobItem;
import com.jfdimarzio.check.model.UnPatrolRecordItem;
import com.jfdimarzio.check.model.rform.RFormCheckItem;
import com.jfdimarzio.check.model.rform.RFormMaterialItem;
import com.jfdimarzio.check.model.rform.RFormWorkingHour;
import com.jfdimarzio.check.model.rform.SelectListItem;
import com.jfdimarzio.check.util.AppUtils;
import com.jfdimarzio.check.util.DateUtils;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.StringUtils;
import com.jfdimarzio.check.util.consts.SystemConstants;
import com.jfdimarzio.check.dao.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jfdimarzio.check.util.LogUtils.*;

public class EquipCheckHelper extends SQLiteOpenHelper {
    private static final String TAG=makeLogTag(EquipCheckHelper.class);
    private static final String DATABASENAME = SystemConstants.SQLITE_EQUIPCHECK;
    private static final int DB_VERSION = 1;
    private final Context mContext;
    private PreferenceUtils mPreferences;
    private String mDatabaseFullPath;
    private static EquipCheckHelper mInstance = null;
    private LOGDBHelper mLOGDBHelper;

    private EquipCheckHelper(Context context) {
        super(new DatabaseContext(context, PreferenceUtils.getInstance(context).getFilePath()), DATABASENAME, null, DB_VERSION);
        this.mContext = context;
        mPreferences = PreferenceUtils.getInstance(mContext);
        mDatabaseFullPath = mPreferences.getFilePath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized static EquipCheckHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EquipCheckHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public boolean copyTempTable(String attachFilePath, boolean isForBase) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();

        //取出來源端的所有table名稱
        HashMap<String, String> tableMap = new HashMap<>();
        Cursor cursor = null;
        try {
            final String[] selectionArgs = new String[]{
                    attachFilePath
            };

            //忽略大小寫
            String sql_attach_db = "ATTACH DATABASE ? as BASE";
            LOGD(TAG,"ATTACH DATABASE:" + attachFilePath);
            wDb.execSQL(sql_attach_db, new String[]{attachFilePath});
            //NOTE ATTACH 不能再 Transcation裡面

            wDb.beginTransaction();

            String sql_query_all_tables = "SELECT name,sql FROM BASE.sqlite_master WHERE type='table' AND name !='android_metadata';";
            cursor = wDb.rawQuery(sql_query_all_tables, null);

            ArrayList<String> uploadTableList = uploadTableList();
            HashMap<String, String> uploadSet = new HashMap<>();
            for (String table : uploadTableList) {
                uploadSet.put(table, table);
            }


            if (cursor.moveToFirst()) {
                do {
                    String table_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String table_sql = cursor.getString(cursor.getColumnIndexOrThrow("sql"));

                    if (isForBase) {
                        if (!uploadSet.containsKey(table_name)) {
                            tableMap.put(table_name, table_sql);
//                            LOGD(TAG,"下載 TABLE NAME:" + table_name);
                        }

                    } else {
                        if (uploadSet.containsKey(table_name)) {
                            tableMap.put(table_name, table_sql);
//                            LOGD(TAG,"上傳 TABLE NAME:" + table_name);
                        }
                    }
                } while (cursor.moveToNext());
            }

//            LOGD(TAG,"重建Table中...");
            for (Map.Entry<String, String> entry : tableMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                //刪除掉舊的Table
                String sql_drop_if_exist = "DROP TABLE IF EXISTS main." + key;
                wDb.execSQL(sql_drop_if_exist);
//                LOGI(TAG,"sql_drop_if_exist:"+ sql_drop_if_exist);

                //執行新增Table語法
                String sql_create_table = value;
                wDb.execSQL(sql_create_table + ";");

                //DUMP資料
                String sql_copy_data = "INSERT INTO main." + key + " SELECT * FROM BASE." + key + " ;";
                wDb.execSQL(sql_copy_data);
//                LOGD(TAG,"TABLE :" + key + ",Has been created");
            }

            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
//            LOGE(TAG,"copyTempTable="+ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        try {
            String sql_detach_db = "DETACH DATABASE 'BASE'";
//            LOGD(TAG,"DETACH DATABASE");
            wDb.execSQL(sql_detach_db);
        } catch (SQLException e) {
//            LOGE(TAG,"DETACH Error="+e.getMessage());
        }

        return result;
    }

    private ArrayList<String> uploadTableList() {
        return new ArrayList<String>() {{
            add(DBColumn.ArriveRecord.TABLE_NAME);
            add(DBColumn.ArriveRecordPhoto.TABLE_NAME);
            add(DBColumn.CheckResult.TABLE_NAME);
            add(DBColumn.CheckResultAbnormalReason.TABLE_NAME);
            add(DBColumn.CheckResultHandlingMethod.TABLE_NAME);
            add(DBColumn.CheckResultPhoto.TABLE_NAME);
            add(DBColumn.OverTimeRecord.TABLE_NAME);
            add(DBColumn.OverTimeRecord.TABLE_NAME);
            add(DBColumn.UploadDefine.TABLE_NAME);
        }};
    }

    public boolean checkIfUploadTableExist() {
        boolean result = false;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> exist_table_list = new ArrayList<>();
        try {

            ArrayList<String> expect_table_list = uploadTableList();

            String sql_query_all_tables = "SELECT name FROM sqlite_master WHERE type='table' AND name IN (" + makePlaceholders(expect_table_list.size()) + ")";

            final ArrayList<String> table_name_selectionArgs = new ArrayList<>();
            for (String table_name : expect_table_list) {
                table_name_selectionArgs.add(table_name);
            }

            cursor = rDb.rawQuery(sql_query_all_tables, table_name_selectionArgs.toArray(new String[table_name_selectionArgs.size()]));
            if (cursor.moveToFirst()) {
                do {
                    String table_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    exist_table_list.add(table_name);
//                    LOGD(TAG, "EXIST TABLE NAME:" + table_name);

                } while (cursor.moveToNext());

            }

            if (exist_table_list.size() == expect_table_list.size()) {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public Person getPerson(String pId) {
        Person result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            final String[] selectionArgs = new String[]{pId};
            String query = "SELECT*FROM " + DBColumn.People.TABLE_NAME +
                    " WHERE " + DBColumn.People.LoginId + " = ? " +
                    "COLLATE NOCASE";
            cursor = rDb.rawQuery(query, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                result = new Person(
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.PersonId)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.LoginId)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.Name)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.Password)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.Title)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.People.Section))
                );
            }
        } catch (Exception ex) {
//            LOGE(TAG,"getUser="+ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    //Get protal and repair jobitem
    public ArrayList<JobItem> getJobItems() {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<JobItem> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            final String[] fetch_columns = new String[]{
                    DBColumn.Job.UniqueID,
                    DBColumn.Job.Description,
                    DBColumn.Job.Remark,
                    DBColumn.Job.BeginTime,
                    DBColumn.Job.EndTime,
                    DBColumn.Job.IsCheckBySeq,
                    DBColumn.Job.IsShowPrevRecord,
                    DBColumn.Job.TimeMode
            };
            cursor = rDb.query(DBColumn.Job.TABLE_NAME, fetch_columns, null, null, null, null, DBColumn.Job.BeginTime + " ASC" + "," + DBColumn.Job.Description + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    boolean isCheckBySeq = AppUtils.convertStringToBool(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.IsCheckBySeq)));
                    boolean isShowPrevRecord = AppUtils.convertStringToBool(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.IsShowPrevRecord)));

                    JobItem newItem = new JobItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.UniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.Description)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.Remark)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.BeginTime)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.Job.EndTime)),
                            isCheckBySeq,
                            isShowPrevRecord,
                            false
                    );

                    int timeModeCheckIdx = cursor.getColumnIndex(DBColumn.Job.TimeMode);
                    if (timeModeCheckIdx != -1) {
                        String timeMode = cursor.getString(timeModeCheckIdx);
                        newItem.setTimeMode(timeMode);
                    }


                    result.add(newItem);
                } while (cursor.moveToNext());
            }

            //定保單
            final String[] fetch_mform_columns = new String[]{
                    DBColumn.MForm.UniqueID,
                    DBColumn.MForm.VHNO,
                    DBColumn.MForm.Description,
                    DBColumn.MForm.Remark,
                    DBColumn.MForm.EquipmentID,
                    DBColumn.MForm.EquipmentName,
                    DBColumn.MForm.PartDescription,
                    DBColumn.MForm.BeginDate,
                    DBColumn.MForm.EndDate,

            };

            cursor = rDb.query(DBColumn.MForm.TABLE_NAME, fetch_mform_columns, null, null, null, null, DBColumn.MForm.VHNO + " ASC");

            if (cursor.moveToFirst()) {
                do {

                    String begDate = StringUtils.toBeautyDate(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.BeginDate)));
                    String endDate = StringUtils.toBeautyDate(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.EndDate)));
                    JobItem newItem = new JobItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.UniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.VHNO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.Description)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.Remark)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.EquipmentID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.EquipmentName)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MForm.PartDescription)),
                            begDate,
                            endDate
                    );

                    result.add(newItem);

                } while (cursor.moveToNext());

            }

            //修復單
            final String[] fetch_repair_columns = new String[]{
                    DBColumn.RepairForm.UniqueID,
                    DBColumn.RepairForm.VHNO,
                    DBColumn.RepairForm.RepairFormType,
                    DBColumn.RepairForm.Equipment,
                    DBColumn.RepairForm.Description,
                    DBColumn.RepairForm.Subject,
                    DBColumn.RepairForm.BeginDate,
                    DBColumn.RepairForm.EndDate
            };

            cursor = rDb.query(DBColumn.RepairForm.TABLE_NAME, fetch_repair_columns, null, null, null, null, DBColumn.RepairForm.VHNO + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    String begDate = StringUtils.toBeautyDate(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.BeginDate)));
                    String endDate = StringUtils.toBeautyDate(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.EndDate)));

                    JobItem newItem = new JobItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.UniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.VHNO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.Equipment)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.Subject)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.Description)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RepairForm.RepairFormType)),
                            begDate,
                            endDate
                    );

                    result.add(newItem);

                } while (cursor.moveToNext());

            }
        } catch (Exception ex) {
//            Timber.e("JobItem Error:%s",ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    /**
     * 取得派工裡面有沒有電話
     *
     * @return
     */
    public HashMap<String, String> getJobEmgTelMap() {

        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String, String> result = new HashMap<>();
        Cursor cursor = null;
        try {

            final String[] selectionArgs = new String[]{

            };

            String query = "SELECT DISTINCT " + DBColumn.EmgContact.JobUniqueID +
                    " FROM " + DBColumn.EmgContact.TABLE_NAME + "";


            cursor = rDb.rawQuery(query, selectionArgs);


            if (cursor.moveToFirst()) {
                do {

                    String jobUniqueID = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EmgContact.JobUniqueID));
                    result.put(jobUniqueID, jobUniqueID);


                } while (cursor.moveToNext());

            }

        } catch (Exception ex) {
//            LOGE(TAG,"getJobEmgTelMap 發生錯誤:"+ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public ArrayList<RFormMaterialItem> getRFormMaterialItems(String mARG_mFormUniqueID) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<RFormMaterialItem> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT \n" +
                    "\n m.*, " +
                    "\n mr.Quantity ResultQuantity " +
                    "\n FROM  RFormMaterial m " +
                    "\n LEFT JOIN RFormMaterialResult mr ON " +
                    "\n m.RFormUniqueID = mr.RFormUniqueID AND " +
                    "\n m.MaterialUniqueID = mr.MaterialUniqueID" +
                    "\n WHERE m.RFormUniqueID = ? " +
                    "\n ORDER BY m.MaterialID";

            String[] selectionArgs = new String[]{
                    mARG_mFormUniqueID,
            };

            cursor = rDb.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {

                    RFormMaterialItem newItem = new RFormMaterialItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormMaterial.RFormUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormMaterial.MaterialUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormMaterial.MaterialID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormMaterial.MaterialName)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.RFormMaterial.Quantity)));

                    if (!cursor.isNull(cursor.getColumnIndexOrThrow("ResultQuantity"))) {
                        newItem.setResultQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("ResultQuantity")));
                    }

                    result.add(newItem);

                } while (cursor.moveToNext());

            }

        } catch (Exception ex) {
            Timber.e("getMaterialItems Error:%s", ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public RFormWorkingHour getRFormWorkingHour(String uniqueID) {
        RFormWorkingHour result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            //default value
            CheckDateFormatItem checkDateFormatItem = new CheckDateFormatItem();
            String str_date = checkDateFormatItem.getFORMAT_DIS_YYYYMMDD();

            result = new RFormWorkingHour("", str_date, str_date, null);

            final String[] selectionArgs = new String[]{
                    uniqueID
            };

            String query = "SELECT * FROM " + DBColumn.RFormWorkingHour.TABLE_NAME + " " +
                    "WHERE " + DBColumn.RFormWorkingHour.RFormUniqueID + " = ? ";


            cursor = rDb.rawQuery(query, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                String beg_date_str = null;
                String end_date_str = null;
                Float working_hour = null;
                String userID = null;
                if (!cursor.isNull(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.BeginDate))) {

                    String temp_str_beg_date = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.BeginDate));
                    Date checkDate = DateUtils.converStringToDate(temp_str_beg_date, DateUtils.FORMAT_YYYYMMDD);
                    beg_date_str = DateUtils.format(checkDate, DateUtils.FORMAT_DIS_YYYYMMDD);

                }

                if (!cursor.isNull(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.EndDate))) {

                    String temp_str_end_date = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.EndDate));
                    Date checkDate = DateUtils.converStringToDate(temp_str_end_date, DateUtils.FORMAT_YYYYMMDD);
                    end_date_str = DateUtils.format(checkDate, DateUtils.FORMAT_DIS_YYYYMMDD);

                }

                if (!cursor.isNull(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.Hour))) {
                    working_hour = cursor.getFloat(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.Hour));
                }

                if (!cursor.isNull(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.UserID))) {
                    userID = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormWorkingHour.UserID));
                }

                result = new RFormWorkingHour(userID, beg_date_str, end_date_str, working_hour);
            }

        } catch (Exception ex) {

            Timber.e("getRFormWorkingHour Error:%s", ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public ArrayList<RFormCheckItem> getRFormCheckItems(String mARG_mFormUniqueID) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<String> jobIDArrayList = new ArrayList<>();
        HashMap<String, String> hasOptionMap = new HashMap<>();
        ArrayList<RFormCheckItem> result = new ArrayList<>();
        Cursor cursor = null;
        try {

            String query = "SELECT * FROM RFormColumnValue\n" +
                    "WHERE RFormUniqueID = ?\n" +
                    "ORDER BY Seq";

            String[] selectionArgs = new String[]{
                    mARG_mFormUniqueID,
            };

            cursor = rDb.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {

                    RFormCheckItem newItem = new RFormCheckItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.RFormUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnDescription)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnOptionUniqueID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.Seq)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.Value)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnOptionUniqueID))
                    );

                    jobIDArrayList.add(newItem.getColumnUniqueID());
//                    if(!cursor.isNull(cursor.getColumnIndexOrThrow("ResultQuantity")))
//                    {
//                        newItem.setResultQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("ResultQuantity")));
//                    }

                    result.add(newItem);

                } while (cursor.moveToNext());

            }

            // second query
            if (result.size() > 0) {
                String sub_query = "SELECT r.ColumnUniqueID ColumnUniqueID,count(*) COUNT FROM RFormColumnValue r\n" +
                        "\nJOIN RFormColumnOption o ON " +
                        "\nr.ColumnUniqueID = o.ColumnUniqueID" +
                        "\nWHERE r.RFormUniqueID =? " +
                        "\nGROUP BY r.ColumnUniqueID";

                cursor = rDb.rawQuery(sub_query, new String[]{mARG_mFormUniqueID});

                if (cursor.moveToFirst()) {
                    do {
                        String columnUniqueID = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnUniqueID));

                        hasOptionMap.put(columnUniqueID, columnUniqueID);

                    } while (cursor.moveToNext());

                }

                // third query
                String third_query = "SELECT r.ColumnUniqueID , o.[Description] FROM RFormColumnValue r\n" +
                        "\nJOIN RFormColumnOption o ON " +
                        "\nr.ColumnUniqueID = o.ColumnUniqueID AND " +
                        "\nr.ColumnOptionUniqueID = o.UniqueID " +
                        "\nWHERE r.RFormUniqueID = ? " +
                        "\nGROUP BY r.ColumnUniqueID";

                cursor = rDb.rawQuery(third_query, new String[]{mARG_mFormUniqueID});

                if (cursor.moveToFirst()) {
                    do {
                        String columnUniqueID = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnValue.ColumnUniqueID));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RFormColumnOption.Description));

                        if (hasOptionMap.containsKey(columnUniqueID)) {
                            hasOptionMap.put(columnUniqueID, description);
                        }
                    } while (cursor.moveToNext());
                }

                //loop
                for (RFormCheckItem item : result) {
                    if (hasOptionMap.containsKey(item.getColumnUniqueID())) {
                        item.setOption(true);
                        item.setUIOptionResult(hasOptionMap.get(item.getColumnUniqueID()));
                    }
                }
            }
        } catch (Exception ex) {
            Timber.e("getRFromCheckItems Error:%s", ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public boolean insertRFormResultData(String mFormUniqueID, ArrayList<RFormCheckItem> checkResults, ArrayList<RFormMaterialResult> mRFormMaterialResult, String userID, RFormWorkingHour mRFormWorkingHour) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        Cursor cursor = null;
        try {
            wDb.beginTransaction();

            Timber.d("process RFormCheckItem");

            int result_update_count = 0;
            for (RFormCheckItem checkResult : checkResults) {
                final ContentValues update_values = new ContentValues(2);
                update_values.put(DBColumn.RFormColumnValue.Value, checkResult.getResultValue());
                update_values.put(DBColumn.RFormColumnValue.ColumnOptionUniqueID, checkResult.getResultColumnOptionUniqueID());

                result_update_count = wDb.update(
                        DBColumn.RFormColumnValue.TABLE_NAME,
                        update_values,
                        DBColumn.RFormColumnValue.RFormUniqueID + " =? AND " + DBColumn.RFormColumnValue.ColumnUniqueID + " =?",
                        new String[]{mFormUniqueID, checkResult.getColumnUniqueID()}
                );
                Timber.d("updateCount : %s", result_update_count);
            }

            Timber.d("process material");
            String delete_RFormMaterialResult = "DELETE FROM " + DBColumn.RFormMaterialResult.TABLE_NAME + " \n" +
                    "\n WHERE " + DBColumn.RFormMaterialResult.RFormUniqueID + " =?";
            wDb.execSQL(delete_RFormMaterialResult, new String[]{mFormUniqueID});
            for (RFormMaterialResult item : mRFormMaterialResult) {
                if (item.getQuantity() != -1) {
                    final ContentValues values = new ContentValues(3);
                    values.put(DBColumn.RFormMaterialResult.RFormUniqueID, item.getRFormUniqueID());
                    values.put(DBColumn.RFormMaterialResult.MaterialUniqueID, item.getMaterialUniqueID());
                    values.put(DBColumn.RFormMaterialResult.Quantity, item.getQuantity());
                    long insert_row_id = wDb.insert(DBColumn.RFormMaterialResult.TABLE_NAME, null, values);
                    Timber.d("insert RFormMaterialResult ok insert_row_id:%s", insert_row_id);
                }
            }

            Timber.d("process working hour");
            String delete_RFormWorkingHour = "DELETE FROM " + DBColumn.RFormWorkingHour.TABLE_NAME + " \n" +
                    "\n WHERE " + DBColumn.RFormWorkingHour.RFormUniqueID + " =?";
            wDb.execSQL(delete_RFormWorkingHour, new String[]{mFormUniqueID});
            {
                String beg_date_str = null;
                String end_date_str = null;
                if (!TextUtils.isEmpty(mRFormWorkingHour.getBeginDate())) {
                    Date checkDate = DateUtils.converStringToDate(mRFormWorkingHour.getBeginDate(), DateUtils.FORMAT_DIS_YYYYMMDD);
                    beg_date_str = DateUtils.format(checkDate, DateUtils.FORMAT_YYYYMMDD);
                }
                if (!TextUtils.isEmpty(mRFormWorkingHour.getEndDate())) {
                    Date checkDate = DateUtils.converStringToDate(mRFormWorkingHour.getEndDate(), DateUtils.FORMAT_DIS_YYYYMMDD);
                    end_date_str = DateUtils.format(checkDate, DateUtils.FORMAT_YYYYMMDD);
                }

                final ContentValues hour_values = new ContentValues(5);
                hour_values.put(DBColumn.RFormWorkingHour.RFormUniqueID, mFormUniqueID);
                hour_values.put(DBColumn.RFormWorkingHour.UserID, mRFormWorkingHour.getUserID());
                hour_values.put(DBColumn.RFormWorkingHour.BeginDate, beg_date_str);
                hour_values.put(DBColumn.RFormWorkingHour.EndDate, end_date_str);
                hour_values.put(DBColumn.RFormWorkingHour.Hour, mRFormWorkingHour.getWorkingHour());
                long insert_row_id = wDb.insert(DBColumn.RFormWorkingHour.TABLE_NAME, null, hour_values);
                Timber.d("insert RFormWorkingHour ok insert_row_id:%s", insert_row_id);
            }

            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
//            LOGE(TAG,"insertRFormResultData="+ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            wDb.endTransaction();
        }

        return result;
    }

    public ArrayList<SelectListItem> getColumnOptions(String column_unique_id) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<SelectListItem> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            result.add(new SelectListItem(mContext.getString(R.string.lb_hint_choice), ""));

            final String[] fetch_columns = new String[]{
                    DBColumn.RFormColumnOption.UniqueID,
                    DBColumn.RFormColumnOption.ColumnUniqueID,
                    DBColumn.RFormColumnOption.Description,
                    DBColumn.RFormColumnOption.Seq,
            };

            String selection = DBColumn.RFormColumnOption.ColumnUniqueID + " =?";
            final String[] selectionArgs = new String[]{
                    column_unique_id
            };
            cursor = rDb.query(DBColumn.RFormColumnOption.TABLE_NAME, fetch_columns, selection, selectionArgs, null, null, DBColumn.RFormColumnOption.Seq);

            if (cursor.moveToFirst()) {
                do {

                    result.add(new SelectListItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.OverTimeReason.Description)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.OverTimeReason.UniqueID))
                    ));

                } while (cursor.moveToNext());

            }

        } catch (Exception ex) {
//            LOGE(TAG,"getColumnOptions 發生錯誤:"+ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public HashMap<String, DoneTotalItem> getJobDoneMap(String jobUniqueID) {
        HashMap<String, DoneTotalItem> result = new HashMap<>();

        final SQLiteDatabase rDb = getReadableDatabase();

        Cursor cursor = null;
        try {

            final String[] base_selectionArgs;

            String query = "";
            String queryTemplate = "SELECT A.JobUniqueID, A.CNT AS CheckItemCount, B.CNT AS CheckedCount\n" +
                    "\n" +
                    "FROM\n" +
                    "\n" +
                    "(\n" +
                    "\n" +
                    "                SELECT CheckItem.JobUniqueID, COUNT(*) AS CNT\n" +
                    "\n" +
                    "                FROM ControlPoint, CheckItem\n" +
                    "\n" +
                    "                WHERE\n" +
                    "\n                      ControlPoint.JobUniqueID = CheckItem.JobUniqueID AND" +
                    "\n                      ControlPoint.ControlPointUniqueID = CheckItem.ControlPointUniqueID " +
                    "\n" +
                    "\n" +
                    "                      AND     %s                  \n" +
                    "                      GROUP BY CheckItem.JobUniqueID\n" +
                    "\n" +
                    "\n" +
                    ") AS A\n" +
                    "\n" +
                    "LEFT JOIN\n" +
                    "\n" +
                    "(\n" +
                    "\n" +
                    "        SELECT CheckResult.JobUniqueID, COUNT(*) AS CNT\n" +
                    "\n" +
                    "        FROM ControlPoint, CheckItem JOIN" +
                    "                ( " +
                    "                   SELECT \n" +
                    "\n                 JobUniqueID,ControlPointUniqueID,\n" +
                    "\n                 EquipmentUniqueID,PartUniqueID,CheckItemUniqueID,CheckDate,CheckTime," +
                    "\n                 Max(CheckDate + CheckTime),    FeelOptionUniqueID, Value, UniqueID,OtherMk" +
                    "\n                 FROM CheckResult" +
                    "\n                 GROUP BY JobUniqueID,ControlPointUniqueID,EquipmentUniqueID,PartUniqueID,CheckItemUniqueID   " +
                    "\n              ) CheckResult " +
                    "" +
                    "\n" +
                    "        WHERE\n" +
                    "\n" +
                    "                ControlPoint.JobUniqueID = CheckItem.JobUniqueID AND\n" +
                    "                ControlPoint.ControlPointUniqueID = CheckItem.ControlPointUniqueID AND\n" +
                    "                CheckItem.JobUniqueID = CheckResult.JobUniqueID AND                \n" +
                    "                CheckItem.ControlPointUniqueID = CheckResult.ControlPointUniqueID AND                \n" +
                    "                CheckItem.EquipmentUniqueID = CheckResult.EquipmentUniqueID AND                \n" +
                    "                CheckItem.PartUniqueID = CheckResult.PartUniqueID AND\n" +
                    "                CheckItem.CheckItemUniqueID = CheckResult.CheckItemUniqueID \n" +
                    "\n" +
                    "              AND    %s           \n" +
                    "              GROUP BY ControlPoint.JobUniqueID\n" +
                    "\n" +
                    "\n" +
                    ") AS B\n" +
                    "\n" +
                    "ON A.JobUniqueID = B.JobUniqueID\n";

            if (jobUniqueID == null) {
                query = String.format(queryTemplate, "1=1", "1=1");
                base_selectionArgs = new String[]{

                };
            } else {
                query = String.format(queryTemplate, " ControlPoint.JobUniqueID=? ", " CheckResult.JobUniqueID=? ");
                base_selectionArgs = new String[]{
                        jobUniqueID,
                        jobUniqueID
                };
            }

            cursor = rDb.rawQuery(query, base_selectionArgs);
            if (cursor.moveToFirst()) {

                do {

                    String jobUniqueID1 = cursor.getString(cursor.getColumnIndexOrThrow("JobUniqueID"));

                    int CheckItemCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckItemCount"));
                    int CheckedCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckedCount"));

                    result.put(jobUniqueID1, new DoneTotalItem(CheckItemCount, CheckedCount));

                } while (cursor.moveToNext());

            }


            //region 定保單
            final String[] mform_selectionArgs;

            String mform_query = "";
            String mform_queryTemplate = "" +
                    "         SELECT A.MFormUniqueID, A.CNT AS CheckItemCount, B.CNT AS CheckedCount          \n" +
                    "         FROM \n" +
                    "         (\n" +
                    "         SELECT MFormUniqueID ,COUNT(*) AS CNT\n" +
                    "                FROM Standard\n" +
                    "                WHERE\n" +
                    "                       %s \n" +
                    "                GROUP BY MFormUniqueID\n" +
                    "\n" +
                    "         ) AS A\n" +
                    "         LEFT JOIN\n" +
                    "         (\n" +
                    "\n" +
                    "         SELECT MFormUniqueID, COUNT(*) AS CNT         \n" +
                    "                FROM MFormStandardResult                 \n" +
                    "                WHERE \n" +
                    "                      %s \n" +
                    "                GROUP BY MFormUniqueID\n" +
                    "         ) AS B         \n" +
                    "         ON A.MFormUniqueID = B.MFormUniqueID";

            if (jobUniqueID == null) {
                mform_query = String.format(mform_queryTemplate, "1=1", "1=1");
                mform_selectionArgs = new String[]{

                };
            } else {
                mform_query = String.format(mform_queryTemplate, " MFormUniqueID = ? ", " MFormUniqueID = ? ");
                mform_selectionArgs = new String[]{
                        jobUniqueID,
                        jobUniqueID
                };
            }

            cursor = rDb.rawQuery(mform_query, mform_selectionArgs);
            if (cursor.moveToFirst()) {

                do {

                    String jobUniqueID1 = cursor.getString(cursor.getColumnIndexOrThrow("MFormUniqueID"));

                    int CheckItemCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckItemCount"));
                    int CheckedCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckedCount"));

                    result.put(jobUniqueID1, new DoneTotalItem(CheckItemCount, CheckedCount));

                } while (cursor.moveToNext());

            }
            //endregion


            //region 修復單
            final String[] rform_selectionArgs;

            String rform_query = "";
            String rform_queryTemplate = "" +
                    "         SELECT A.RFormUniqueID, A.CNT AS CheckItemCount, B.CNT AS CheckedCount          \n" +
                    "         FROM \n" +
                    "         (\n" +
                    "         SELECT RFormUniqueID ,COUNT(*) AS CNT\n" +
                    "                FROM RFormColumnValue\n" +
                    "                WHERE\n" +
                    "                       %s\n" +
                    "                GROUP BY RFormUniqueID        \n" +
                    "\n" +
                    "         ) AS A\n" +
                    "         LEFT JOIN\n" +
                    "         (\n" +
                    "\n" +
                    "         SELECT RFormUniqueID ,COUNT(*) AS CNT\n" +
                    "                FROM RFormColumnValue\n" +
                    "                WHERE                \n" +
                    "                       (ColumnOptionUniqueID is NOT NULL OR [Value] is NOT NULL)\n" +
                    "                       AND %s                       \n" +
                    "\n" +
                    "                GROUP BY RFormUniqueID     \n" +
                    "         ) AS B         \n" +
                    "         ON A.RFormUniqueID = B.RFormUniqueID         ";

            if (jobUniqueID == null) {
                rform_query = String.format(rform_queryTemplate, "1=1", "1=1");
                rform_selectionArgs = new String[]{

                };
            } else {
                rform_query = String.format(rform_queryTemplate, " RFormUniqueID = ? ", " RFormUniqueID = ? ");
                rform_selectionArgs = new String[]{
                        jobUniqueID,
                        jobUniqueID
                };
            }

            cursor = rDb.rawQuery(rform_query, rform_selectionArgs);
            if (cursor.moveToFirst()) {

                do {

                    String jobUniqueID1 = cursor.getString(cursor.getColumnIndexOrThrow("RFormUniqueID"));

                    int CheckItemCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckItemCount"));
                    int CheckedCount = cursor.getInt(cursor.getColumnIndexOrThrow("CheckedCount"));

                    result.put(jobUniqueID1, new DoneTotalItem(CheckItemCount, CheckedCount));

                } while (cursor.moveToNext());

            }
            //endregion
        } catch (Exception ex) {
//            LOGE(TAG, "getJobDoneMap=" + ex.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return result;
    }

    public HashMap<String, UnPatrolRecordItem> getUnPatrolRecordMap(String jobUniqueID) {
        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String, UnPatrolRecordItem> result = new HashMap<>();
        Cursor cursor = null;
        try {

            String sql_text = "SELECT a.*,r.[Description] UIUnPatrolReason\n" +
                    "FROM UnPatrolRecord a\n" +
                    "LEFT JOIN UnPatrolReason r on a.UnPatrolReasonUniqueID = r.[UniqueID]\n" +
                    "\n";


            String[] selectionArgs = null;

            if (!TextUtils.isEmpty(jobUniqueID)) {
                sql_text += "WHERE a.[JobUniqueID] = ?\n";
                selectionArgs = new String[]{
                        jobUniqueID
                };
            }

            cursor = rDb.rawQuery(sql_text, selectionArgs);


            if (cursor.moveToFirst()) {
                do {

                    UnPatrolRecordItem item = new UnPatrolRecordItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UnPatrolRecord.JobUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UnPatrolRecord.UnPatrolReasonUniqueID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UnPatrolRecord.UnPatrolReasonRemark)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UnPatrolRecord.UserID)),
                            cursor.getString(cursor.getColumnIndexOrThrow("UIUnPatrolReason"))
                    );

                    result.put(item.getJobUniqueID(), item);


                } while (cursor.moveToNext());

            }

        } catch (Exception ex) {
//            LOGE(TAG, "getArriveRecordPhotoList 發生錯誤:" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public HashMap<String, DoneTotalItem> getMFormJobDoneMap() {
        HashMap<String, DoneTotalItem> result = new HashMap<>();

        final SQLiteDatabase rDb = getReadableDatabase();

        Cursor cursor = null;
        try {


            final String[] fetch_columns = new String[]{
                    DBColumn.MFormResult.MFormUniqueID,
            };

            cursor = rDb.query(DBColumn.MFormResult.TABLE_NAME, fetch_columns, null, null, null, null, DBColumn.MFormResult.MFormUniqueID);

            if (cursor.moveToFirst()) {
                do {
                    result.put(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.MFormResult.MFormUniqueID)), new DoneTotalItem(0, 0));

                } while (cursor.moveToNext());

            }

        } catch (Exception ex) {
            Timber.e("getMFormJobDoneMap 發生錯誤: %s", ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;

    }

    public boolean deleteInsertUploadDefine(ArrayList<String> jobIDArrayList) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.beginTransaction();
            int delete_rows = wDb.delete(DBColumn.UploadDefine.TABLE_NAME, null, null);
//            LOGD(TAG, "移除 原本存在的註記 筆數:=" + delete_rows);

            for (String routeID : jobIDArrayList) {
                final ContentValues contentValues = new ContentValues(1);
                contentValues.put(DBColumn.UploadDefine.JobUniqueID, routeID);
                long insert_pic_row_id = wDb.insert(DBColumn.UploadDefine.TABLE_NAME, null, contentValues);
//                LOGD(TAG,"新增 註記 成功 insert_row_id:"  + String.valueOf(insert_pic_row_id)) ;
            }

            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
//            LOGE(TAG,"deleteInsertUploadDefine="+ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }

    public ArrayList<String> getArriveRecordPhotoList(ArrayList<String> jobIDArrayList) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM ArriveRecordPhoto \n" +
                    "\n WHERE ArriveRecordUniqueID IN (" +
                    "\n" +
                    "\n SELECT UniqueID FROM ArriveRecord " +
                    "\n WHERE  JobUniqueID IN  (" + makePlaceholders(jobIDArrayList.size()) + ")" +
                    "\n )";

            final ArrayList<String> selectionArgs = new ArrayList<>();

            for (String route_key : jobIDArrayList) {
                selectionArgs.add(route_key);
            }

            cursor = rDb.rawQuery(query, selectionArgs.toArray(new String[selectionArgs.size()]));

            if (cursor.moveToFirst()) {
                do {

                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ArriveRecordPhoto.FilePath)));

                } while (cursor.moveToNext());
            }

        } catch (Exception ex) {
//            LOGE(TAG, "getArriveRecordPhotoList 發生錯誤:" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public ArrayList<String> getCheckResultPhotoList(ArrayList<String> jobIDArrayList)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();
        Cursor cursor = null;
        try {


            String query = "SELECT * FROM " + DBColumn.CheckResultPhoto.TABLE_NAME +
                    "\n WHERE CheckResultUniqueID IN (\n" +
                    "\n" +
                    "\n SELECT UniqueID FROM CheckResult    \n" +
                    "\n WHERE  JobUniqueID IN  ("+ makePlaceholders(jobIDArrayList.size()) + ")"+
                    "\n )";

            final ArrayList<String> selectionArgs = new ArrayList<>();

            for(String route_key : jobIDArrayList)
            {
                selectionArgs.add(route_key);
            }

            cursor = rDb.rawQuery(query, selectionArgs.toArray(new String[selectionArgs.size()]));

            if(cursor.moveToFirst())
            {
                do{

                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckResultPhoto.FilePath)));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex) {
//            LOGE(TAG, "getCheckResultPhotoList 發生錯誤:" + ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public ArrayList<String> getMFormCheckResultPhotoList(ArrayList<String> jobIDArrayList)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();
        Cursor cursor = null;
        try {


            String query = "SELECT * FROM " + DBColumn.MFormStandardResultPhoto.TABLE_NAME +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")"+
                    "\n ";

            final ArrayList<String> selectionArgs = new ArrayList<>();

            for(String route_key : jobIDArrayList)
            {
                selectionArgs.add(route_key);
            }

            cursor = rDb.rawQuery(query, selectionArgs.toArray(new String[selectionArgs.size()]));

            if(cursor.moveToFirst()) {
                do{
                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckResultPhoto.FilePath)));
                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex) {
            Timber.e("getMFormCheckResultPhotoList 發生錯誤:%s",ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public boolean deleteUploadRelateRecord(ArrayList<String> jobIDArrayList) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();

            //未完成路線
//            String delete_RouteUnFinishedRecord = "DELETE FROM RouteUnFinishedRecord WHERE RouteUniqueID IN ("+ makePlaceholders(routeIDArrayList.size()) + ")";
//            wDb.execSQL(delete_RouteUnFinishedRecord, routeIDArrayList.toArray(new String[routeIDArrayList.size()]));

            // 遲到早退
            String delete_OverTimeRecord = "DELETE FROM "+ DBColumn.OverTimeRecord.TABLE_NAME +
                    " \n WHERE "+ DBColumn.OverTimeRecord.JobUniqueID +" IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_OverTimeRecord,jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_OverTimeRecord done");

            // 檢查結果照片
            String delete_CheckResultPhoto = "DELETE FROM " + DBColumn.CheckResultPhoto.TABLE_NAME +
                    "\n WHERE CheckResultUniqueID IN (\n" +
                    "\n" +
                    "\n SELECT UniqueID FROM CheckResult    \n" +
                    "\n WHERE  JobUniqueID IN  ("+ makePlaceholders(jobIDArrayList.size()) + ")"+
                    "\n )";
            wDb.execSQL(delete_CheckResultPhoto,jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_CheckResultPhoto done");


            String delete_CheckResult = "DELETE FROM CheckResult \n" +
                    "\n WHERE JobUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_CheckResult,jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_CheckResult done");


            String delete_ArriveRecordPhoto = "DELETE FROM ArriveRecordPhoto \n" +
                    "\n WHERE ArriveRecordUniqueID IN (" +
                    "\n" +
                    "\n SELECT UniqueID FROM ArriveRecord " +
                    "\n WHERE  JobUniqueID IN  ("+ makePlaceholders(jobIDArrayList.size()) + ")"+
                    "\n )";
            wDb.execSQL(delete_ArriveRecordPhoto, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_ArriveRecordPhoto done");

            String delete_ArriveRecord = "DELETE FROM ArriveRecord \n" +
                    "\n WHERE JobUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_ArriveRecord, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_ArriveRecord done");

            String delete_UnPatrolRecord = "DELETE FROM "+ DBColumn.UnPatrolRecord.TABLE_NAME +" \n" +
                    "\n WHERE JobUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_UnPatrolRecord, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_UnPatrolRecord done");

            // MForm

            // 基準也會跟著刪掉
            String delete_mform = "DELETE FROM "+ DBColumn.MForm.TABLE_NAME +" \n" +
                    "\n WHERE UniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_mform, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_mform done");

            // 抄表基準
            String delete_MFormMaterialResult = "DELETE FROM "+ DBColumn.MFormMaterialResult.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormMaterialResult, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_UnPatrolRecord done");

            String delete_MFormResult = "DELETE FROM "+ DBColumn.MFormResult.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormResult, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormResult done");

            String delete_MFormStandardResult = "DELETE FROM "+ DBColumn.MFormStandardResult.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormStandardResult, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormStandardResult done");

            String delete_MFormStandardResultAbnormalReason = "DELETE FROM "+ DBColumn.MFormStandardResultAbnormalReason.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormStandardResultAbnormalReason, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormStandardResultAbnormalReason done");

            String delete_MFormStandardResultHandlingMethod = "DELETE FROM "+ DBColumn.MFormStandardResultHandlingMethod.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormStandardResultHandlingMethod, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormStandardResultHandlingMethod done");

            String delete_MFormStandardResultPhoto = "DELETE FROM "+ DBColumn.MFormStandardResultPhoto.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormStandardResultPhoto, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormStandardResultPhoto done");

            String delete_MFormWorkingHour = "DELETE FROM "+ DBColumn.MFormWorkingHour.TABLE_NAME +" \n" +
                    "\n WHERE MFormUniqueID IN ("+ makePlaceholders(jobIDArrayList.size()) + ")";
            wDb.execSQL(delete_MFormWorkingHour, jobIDArrayList.toArray(new String[jobIDArrayList.size()]));
            Timber.i("remove delete_MFormWorkingHour done");

            wDb.setTransactionSuccessful();
            result = true;
        }catch (Exception ex)
        {
            Timber.e("deleteUploadRelateRecord 發生錯誤 : %s" , ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
        return result;
    }

    public void resetTables() {
        try {
            SQLiteDatabase wDb = getWritableDatabase();
            wDb.delete(DBColumn.ArriveRecord.TABLE_NAME, null, null);
            wDb.delete(DBColumn.ArriveRecordPhoto.TABLE_NAME, null, null);
            wDb.delete(DBColumn.CheckResult.TABLE_NAME, null, null);
            wDb.delete(DBColumn.CheckResultPhoto.TABLE_NAME, null, null);
            wDb.delete(DBColumn.OverTimeRecord.TABLE_NAME, null, null);
            wDb.delete(DBColumn.UnPatrolRecord.TABLE_NAME, null, null);
            // TODO RouteUnFinishedRecord not exist
//            wDb.delete(DBColumn.RouteUnFinishedRecord.TABLE_NAME, null, null);

            //MForm
            wDb.delete(DBColumn.MFormWorkingHour.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormResult.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormStandardResult.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormMaterialResult.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormStandardResultAbnormalReason.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormStandardResultHandlingMethod.TABLE_NAME, null, null);
            wDb.delete(DBColumn.MFormStandardResultPhoto.TABLE_NAME, null, null);

            wDb.close();
        } catch (Exception ex) {
            Timber.e("resetTables 發生錯誤 : %s", ex.getMessage());
        }
    }
}
