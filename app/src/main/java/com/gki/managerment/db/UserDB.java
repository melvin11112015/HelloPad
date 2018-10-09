package com.gki.managerment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gki.managerment.LoginUser;

import java.util.ArrayList;

public class UserDB {
	SqlHelper dbHelper;
	
	 public UserDB(Context context){
		 dbHelper = new SqlHelper(context, null, 1);
	}

	//获取所有登录用户
	public ArrayList<LoginUser> findAllLoginUser(){
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select userId,userPswd,loginTime from LoginUser order by loginTime desc ", null);
		ArrayList<LoginUser> lstUser = new ArrayList<LoginUser>();
		while (cursor.moveToNext()) {
			LoginUser entity = new LoginUser();
			entity.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
			entity.setPswd(cursor.getString(cursor.getColumnIndex("userPswd")));
			entity.setLoginTime(cursor.getString(cursor.getColumnIndex("loginTime")));
			lstUser.add(entity);
		}
		return lstUser;
	}
	//插入登录用户信息
	public Long insertLoginUser(LoginUser entity){
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userPswd", entity.getPswd());
		values.put("loginTime", entity.getLoginTime());
		long iCount = db.update("LoginUser", values, " userId = ? ", new String[]{entity.getUserId()});
		//更新不成功就插入数据
		if (iCount == 0)
		{
			values.put("userId", entity.getUserId());
			iCount = db.insert("LoginUser", null, values);
		}
		db.close();
		return iCount;
	}
	//删除登录用户
	public int deleteLoginUser(String strUserId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int iCount = db.delete("LoginUser","userId = ?", new String[]{strUserId});
		db.close();
		return iCount;
	}
}
