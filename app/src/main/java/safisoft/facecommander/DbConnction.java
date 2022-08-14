package safisoft.facecommander;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SafiSoft on 3/19/2021.
 */

public class DbConnction extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "Face_Command_Control";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DbConnction(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        super.onOpen(myDataBase);
        myDataBase.disableWriteAheadLogging();

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {//fix database problem on api28+
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public Cursor Query_Data(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query(table,null,null,null,null,null,null);
    }

    public Cursor Row_Query(String Tapel_name,String column_name,String data){
        return myDataBase.rawQuery("SELECT * FROM " + Tapel_name + " WHERE " + column_name + "=?", new String[] { data });
    }

    public Cursor Search_Query(String Tapel_name,String column_name,String val){
        return myDataBase.rawQuery("SELECT * FROM "+Tapel_name+" WHERE "+column_name+" like '%"+val+"%'",null);
    }


    public void InsertNewProjectFaceControl(String project_name , String project_description , String right_eye , String right_eye_not , String smile , String smile_not , String left_eye , String left_eye_not , String look_right, String look_right_not, String look_left, String look_left_not, String rotate_right, String rotate_right_not , String rotate_left , String rotate_left_not
            , String look_down , String look_down_not , String look_up , String look_up_not  ) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("project_name",project_name);
        contentValues.put("project_description",project_description);
        contentValues.put("right_eye",right_eye);
        contentValues.put("right_eye_not",right_eye_not);
        contentValues.put("smile",smile);
        contentValues.put("smile_not",smile_not);
        contentValues.put("left_eye",left_eye);
        contentValues.put("left_eye_not",left_eye_not);
        contentValues.put("look_right",look_right);
        contentValues.put("look_right_not",look_right_not);
        contentValues.put("look_left",look_left);
        contentValues.put("look_left_not",look_left_not);
        contentValues.put("rotate_right",rotate_right);
        contentValues.put("rotate_right_not",rotate_right_not);
        contentValues.put("rotate_left",rotate_left);
        contentValues.put("rotate_left_not",rotate_left_not);
        contentValues.put("look_down",look_down);
        contentValues.put("look_down_not",look_down_not);
        contentValues.put("look_up",look_up);
        contentValues.put("look_up_not",look_up_not);
        database.insert("Project_Face_Control", null, contentValues);
        database.close();
    }


    public void InsertNewProjectDetails(String project_name , String project_description ) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("project_name",project_name);
        contentValues.put("project_description",project_description);
        database.insert("Projects_List", null, contentValues);
        database.close();
    }

    public void UpdateFaceControlRecord(String COLUMN_NAME_FOR_COMMAND_FIRST,String VALUE_FOR_COMMAND_FIRST,String PROJECT_NAME) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_FOR_COMMAND_FIRST,VALUE_FOR_COMMAND_FIRST);
        database.update("Project_Face_Control", contentValues, "project_name" + " = ?", new String[]{PROJECT_NAME});
        database.close();
    }

    public void UpdateRotationControlRecord(String COLUMN_NAME,String COLUMN_VAL,String PROJECT_NAME) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,COLUMN_VAL);
        database.update("Project_Rotation_Control", contentValues, "project_name" + " = ?", new String[]{PROJECT_NAME});
        database.close();
    }

    public void UpdateVoiceControlRecord(String COLUMN_NAME,String COLUMN_VAL,String PROJECT_NAME) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,COLUMN_VAL);
        database.update("Project_Voice_Control", contentValues, "project_name" + " = ?", new String[]{PROJECT_NAME});
        database.close();
    }

    public void updateRecordlogin(String COLUMN_NAME1,String COLUMN_VAL1,String COLUMN_NAME2,String COLUMN_VAL2,String COLUMN_NAME3,String COLUMN_VAL3,String PROJECT_NAME) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME1,COLUMN_VAL1);
        contentValues.put(COLUMN_NAME2,COLUMN_VAL2);
        contentValues.put(COLUMN_NAME3,COLUMN_VAL3);
        database.update("Project_Buttons_Control", contentValues, "project_name" + " = ?", new String[]{PROJECT_NAME});
        database.close();
    }

    public void deleteRecordAlternate(String TABLE_NAME,String COLUMN_ID,String ID_NUM) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = '" + ID_NUM + "'");
        database.close();
    }

    public void deleteAllRecord(String TABLE_NAME) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME );
        database.close();
    }

    public Cursor sortRecord_type( ) {
      return    myDataBase.rawQuery("SELECT * FROM qr_data Order By des1 DESC ",null);
    }

    public Cursor sortRecord_name( ) {
        return    myDataBase.rawQuery("SELECT * FROM qr_data Order By val1 ASC ",null);
    }

    public Cursor sortRecord_id( ) {
        return    myDataBase.rawQuery("SELECT * FROM Projects_List Order By _id DESC",null);
    }




}