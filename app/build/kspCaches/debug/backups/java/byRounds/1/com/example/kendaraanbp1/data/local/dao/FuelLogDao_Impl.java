package com.example.kendaraanbp1.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FuelLogDao_Impl implements FuelLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FuelLogEntity> __insertionAdapterOfFuelLogEntity;

  private final EntityDeletionOrUpdateAdapter<FuelLogEntity> __deletionAdapterOfFuelLogEntity;

  private final EntityDeletionOrUpdateAdapter<FuelLogEntity> __updateAdapterOfFuelLogEntity;

  public FuelLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFuelLogEntity = new EntityInsertionAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `fuel_logs` (`id`,`vehicleId`,`date`,`liters`,`pricePerLiter`,`totalCost`,`odometer`,`stationName`,`receiptPhotoPath`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getDate());
        statement.bindDouble(4, entity.getLiters());
        statement.bindDouble(5, entity.getPricePerLiter());
        statement.bindDouble(6, entity.getTotalCost());
        statement.bindLong(7, entity.getOdometer());
        statement.bindString(8, entity.getStationName());
        if (entity.getReceiptPhotoPath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getReceiptPhotoPath());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfFuelLogEntity = new EntityDeletionOrUpdateAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `fuel_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFuelLogEntity = new EntityDeletionOrUpdateAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `fuel_logs` SET `id` = ?,`vehicleId` = ?,`date` = ?,`liters` = ?,`pricePerLiter` = ?,`totalCost` = ?,`odometer` = ?,`stationName` = ?,`receiptPhotoPath` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getDate());
        statement.bindDouble(4, entity.getLiters());
        statement.bindDouble(5, entity.getPricePerLiter());
        statement.bindDouble(6, entity.getTotalCost());
        statement.bindLong(7, entity.getOdometer());
        statement.bindString(8, entity.getStationName());
        if (entity.getReceiptPhotoPath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getReceiptPhotoPath());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
  }

  @Override
  public Object insertLog(final FuelLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFuelLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLog(final FuelLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFuelLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLog(final FuelLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFuelLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FuelLogEntity>> getLogsByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM fuel_logs WHERE vehicleId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuel_logs"}, new Callable<List<FuelLogEntity>>() {
      @Override
      @NonNull
      public List<FuelLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfReceiptPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptPhotoPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FuelLogEntity> _result = new ArrayList<FuelLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuelLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final String _tmpStationName;
            _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            final String _tmpReceiptPhotoPath;
            if (_cursor.isNull(_cursorIndexOfReceiptPhotoPath)) {
              _tmpReceiptPhotoPath = null;
            } else {
              _tmpReceiptPhotoPath = _cursor.getString(_cursorIndexOfReceiptPhotoPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpDate,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpOdometer,_tmpStationName,_tmpReceiptPhotoPath,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Double> getTotalExpenseByPeriod(final long vehicleId, final long startDate,
      final long endDate) {
    final String _sql = "SELECT SUM(totalCost) FROM fuel_logs WHERE vehicleId = ? AND date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuel_logs"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
