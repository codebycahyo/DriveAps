package com.example.kendaraanbp1.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ServiceLogDao_Impl implements ServiceLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ServiceLogEntity> __insertionAdapterOfServiceLogEntity;

  private final EntityDeletionOrUpdateAdapter<ServiceLogEntity> __deletionAdapterOfServiceLogEntity;

  private final EntityDeletionOrUpdateAdapter<ServiceLogEntity> __updateAdapterOfServiceLogEntity;

  public ServiceLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfServiceLogEntity = new EntityInsertionAdapter<ServiceLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `service_logs` (`id`,`vehicleId`,`date`,`category`,`workshopName`,`odometer`,`totalCost`,`receiptPhotoPath`,`nextServiceDate`,`nextServiceOdometer`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getDate());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getWorkshopName());
        statement.bindLong(6, entity.getOdometer());
        statement.bindDouble(7, entity.getTotalCost());
        if (entity.getReceiptPhotoPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getReceiptPhotoPath());
        }
        if (entity.getNextServiceDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextServiceDate());
        }
        if (entity.getNextServiceOdometer() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNextServiceOdometer());
        }
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfServiceLogEntity = new EntityDeletionOrUpdateAdapter<ServiceLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `service_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfServiceLogEntity = new EntityDeletionOrUpdateAdapter<ServiceLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `service_logs` SET `id` = ?,`vehicleId` = ?,`date` = ?,`category` = ?,`workshopName` = ?,`odometer` = ?,`totalCost` = ?,`receiptPhotoPath` = ?,`nextServiceDate` = ?,`nextServiceOdometer` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getDate());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getWorkshopName());
        statement.bindLong(6, entity.getOdometer());
        statement.bindDouble(7, entity.getTotalCost());
        if (entity.getReceiptPhotoPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getReceiptPhotoPath());
        }
        if (entity.getNextServiceDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextServiceDate());
        }
        if (entity.getNextServiceOdometer() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNextServiceOdometer());
        }
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
        statement.bindLong(13, entity.getId());
      }
    };
  }

  @Override
  public Object insertLog(final ServiceLogEntity log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfServiceLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLog(final ServiceLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfServiceLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLog(final ServiceLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfServiceLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ServiceLogEntity>> getLogsByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM service_logs WHERE vehicleId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"service_logs"}, new Callable<List<ServiceLogEntity>>() {
      @Override
      @NonNull
      public List<ServiceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfWorkshopName = CursorUtil.getColumnIndexOrThrow(_cursor, "workshopName");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfReceiptPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptPhotoPath");
          final int _cursorIndexOfNextServiceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceDate");
          final int _cursorIndexOfNextServiceOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceOdometer");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceLogEntity> _result = new ArrayList<ServiceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpWorkshopName;
            _tmpWorkshopName = _cursor.getString(_cursorIndexOfWorkshopName);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpReceiptPhotoPath;
            if (_cursor.isNull(_cursorIndexOfReceiptPhotoPath)) {
              _tmpReceiptPhotoPath = null;
            } else {
              _tmpReceiptPhotoPath = _cursor.getString(_cursorIndexOfReceiptPhotoPath);
            }
            final Long _tmpNextServiceDate;
            if (_cursor.isNull(_cursorIndexOfNextServiceDate)) {
              _tmpNextServiceDate = null;
            } else {
              _tmpNextServiceDate = _cursor.getLong(_cursorIndexOfNextServiceDate);
            }
            final Integer _tmpNextServiceOdometer;
            if (_cursor.isNull(_cursorIndexOfNextServiceOdometer)) {
              _tmpNextServiceOdometer = null;
            } else {
              _tmpNextServiceOdometer = _cursor.getInt(_cursorIndexOfNextServiceOdometer);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ServiceLogEntity(_tmpId,_tmpVehicleId,_tmpDate,_tmpCategory,_tmpWorkshopName,_tmpOdometer,_tmpTotalCost,_tmpReceiptPhotoPath,_tmpNextServiceDate,_tmpNextServiceOdometer,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<ServiceLogEntity>> getUpcomingServices(final long vehicleId,
      final long currentTime) {
    final String _sql = "SELECT * FROM service_logs WHERE vehicleId = ? AND nextServiceDate IS NOT NULL AND nextServiceDate >= ? ORDER BY nextServiceDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, currentTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"service_logs"}, new Callable<List<ServiceLogEntity>>() {
      @Override
      @NonNull
      public List<ServiceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfWorkshopName = CursorUtil.getColumnIndexOrThrow(_cursor, "workshopName");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfReceiptPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptPhotoPath");
          final int _cursorIndexOfNextServiceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceDate");
          final int _cursorIndexOfNextServiceOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceOdometer");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceLogEntity> _result = new ArrayList<ServiceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpWorkshopName;
            _tmpWorkshopName = _cursor.getString(_cursorIndexOfWorkshopName);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpReceiptPhotoPath;
            if (_cursor.isNull(_cursorIndexOfReceiptPhotoPath)) {
              _tmpReceiptPhotoPath = null;
            } else {
              _tmpReceiptPhotoPath = _cursor.getString(_cursorIndexOfReceiptPhotoPath);
            }
            final Long _tmpNextServiceDate;
            if (_cursor.isNull(_cursorIndexOfNextServiceDate)) {
              _tmpNextServiceDate = null;
            } else {
              _tmpNextServiceDate = _cursor.getLong(_cursorIndexOfNextServiceDate);
            }
            final Integer _tmpNextServiceOdometer;
            if (_cursor.isNull(_cursorIndexOfNextServiceOdometer)) {
              _tmpNextServiceOdometer = null;
            } else {
              _tmpNextServiceOdometer = _cursor.getInt(_cursorIndexOfNextServiceOdometer);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ServiceLogEntity(_tmpId,_tmpVehicleId,_tmpDate,_tmpCategory,_tmpWorkshopName,_tmpOdometer,_tmpTotalCost,_tmpReceiptPhotoPath,_tmpNextServiceDate,_tmpNextServiceOdometer,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllLogsWithUpcomingService(
      final Continuation<? super List<ServiceLogEntity>> $completion) {
    final String _sql = "SELECT * FROM service_logs WHERE nextServiceDate IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceLogEntity>>() {
      @Override
      @NonNull
      public List<ServiceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfWorkshopName = CursorUtil.getColumnIndexOrThrow(_cursor, "workshopName");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfReceiptPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptPhotoPath");
          final int _cursorIndexOfNextServiceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceDate");
          final int _cursorIndexOfNextServiceOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextServiceOdometer");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceLogEntity> _result = new ArrayList<ServiceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpWorkshopName;
            _tmpWorkshopName = _cursor.getString(_cursorIndexOfWorkshopName);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpReceiptPhotoPath;
            if (_cursor.isNull(_cursorIndexOfReceiptPhotoPath)) {
              _tmpReceiptPhotoPath = null;
            } else {
              _tmpReceiptPhotoPath = _cursor.getString(_cursorIndexOfReceiptPhotoPath);
            }
            final Long _tmpNextServiceDate;
            if (_cursor.isNull(_cursorIndexOfNextServiceDate)) {
              _tmpNextServiceDate = null;
            } else {
              _tmpNextServiceDate = _cursor.getLong(_cursorIndexOfNextServiceDate);
            }
            final Integer _tmpNextServiceOdometer;
            if (_cursor.isNull(_cursorIndexOfNextServiceOdometer)) {
              _tmpNextServiceOdometer = null;
            } else {
              _tmpNextServiceOdometer = _cursor.getInt(_cursorIndexOfNextServiceOdometer);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ServiceLogEntity(_tmpId,_tmpVehicleId,_tmpDate,_tmpCategory,_tmpWorkshopName,_tmpOdometer,_tmpTotalCost,_tmpReceiptPhotoPath,_tmpNextServiceDate,_tmpNextServiceOdometer,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
