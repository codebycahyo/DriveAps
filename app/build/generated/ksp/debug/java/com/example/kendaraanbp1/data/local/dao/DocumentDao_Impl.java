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
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity;
import java.lang.Class;
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
public final class DocumentDao_Impl implements DocumentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VehicleDocumentEntity> __insertionAdapterOfVehicleDocumentEntity;

  private final EntityDeletionOrUpdateAdapter<VehicleDocumentEntity> __deletionAdapterOfVehicleDocumentEntity;

  private final EntityDeletionOrUpdateAdapter<VehicleDocumentEntity> __updateAdapterOfVehicleDocumentEntity;

  public DocumentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVehicleDocumentEntity = new EntityInsertionAdapter<VehicleDocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vehicle_documents` (`id`,`vehicleId`,`documentType`,`documentNumber`,`issuedDate`,`expiryDate`,`photoPath`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleDocumentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getDocumentType());
        if (entity.getDocumentNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDocumentNumber());
        }
        if (entity.getIssuedDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getIssuedDate());
        }
        if (entity.getExpiryDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getExpiryDate());
        }
        if (entity.getPhotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoPath());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfVehicleDocumentEntity = new EntityDeletionOrUpdateAdapter<VehicleDocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `vehicle_documents` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleDocumentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVehicleDocumentEntity = new EntityDeletionOrUpdateAdapter<VehicleDocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `vehicle_documents` SET `id` = ?,`vehicleId` = ?,`documentType` = ?,`documentNumber` = ?,`issuedDate` = ?,`expiryDate` = ?,`photoPath` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleDocumentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getDocumentType());
        if (entity.getDocumentNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDocumentNumber());
        }
        if (entity.getIssuedDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getIssuedDate());
        }
        if (entity.getExpiryDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getExpiryDate());
        }
        if (entity.getPhotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoPath());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insertDocument(final VehicleDocumentEntity document,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVehicleDocumentEntity.insertAndReturnId(document);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDocument(final VehicleDocumentEntity document,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVehicleDocumentEntity.handle(document);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDocument(final VehicleDocumentEntity document,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVehicleDocumentEntity.handle(document);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VehicleDocumentEntity>> getDocumentsByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM vehicle_documents WHERE vehicleId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicle_documents"}, new Callable<List<VehicleDocumentEntity>>() {
      @Override
      @NonNull
      public List<VehicleDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfDocumentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "documentNumber");
          final int _cursorIndexOfIssuedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issuedDate");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehicleDocumentEntity> _result = new ArrayList<VehicleDocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleDocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpDocumentType;
            _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            final String _tmpDocumentNumber;
            if (_cursor.isNull(_cursorIndexOfDocumentNumber)) {
              _tmpDocumentNumber = null;
            } else {
              _tmpDocumentNumber = _cursor.getString(_cursorIndexOfDocumentNumber);
            }
            final Long _tmpIssuedDate;
            if (_cursor.isNull(_cursorIndexOfIssuedDate)) {
              _tmpIssuedDate = null;
            } else {
              _tmpIssuedDate = _cursor.getLong(_cursorIndexOfIssuedDate);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehicleDocumentEntity(_tmpId,_tmpVehicleId,_tmpDocumentType,_tmpDocumentNumber,_tmpIssuedDate,_tmpExpiryDate,_tmpPhotoPath,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<VehicleDocumentEntity>> getExpiringDocuments(final long vehicleId,
      final long currentTime, final long targetDate) {
    final String _sql = "SELECT * FROM vehicle_documents WHERE vehicleId = ? AND expiryDate IS NOT NULL AND expiryDate <= ? AND expiryDate >= ? ORDER BY expiryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, targetDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, currentTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicle_documents"}, new Callable<List<VehicleDocumentEntity>>() {
      @Override
      @NonNull
      public List<VehicleDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfDocumentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "documentNumber");
          final int _cursorIndexOfIssuedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issuedDate");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehicleDocumentEntity> _result = new ArrayList<VehicleDocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleDocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpDocumentType;
            _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            final String _tmpDocumentNumber;
            if (_cursor.isNull(_cursorIndexOfDocumentNumber)) {
              _tmpDocumentNumber = null;
            } else {
              _tmpDocumentNumber = _cursor.getString(_cursorIndexOfDocumentNumber);
            }
            final Long _tmpIssuedDate;
            if (_cursor.isNull(_cursorIndexOfIssuedDate)) {
              _tmpIssuedDate = null;
            } else {
              _tmpIssuedDate = _cursor.getLong(_cursorIndexOfIssuedDate);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehicleDocumentEntity(_tmpId,_tmpVehicleId,_tmpDocumentType,_tmpDocumentNumber,_tmpIssuedDate,_tmpExpiryDate,_tmpPhotoPath,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllDocumentsWithExpiry(
      final Continuation<? super List<VehicleDocumentEntity>> $completion) {
    final String _sql = "SELECT * FROM vehicle_documents WHERE expiryDate IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VehicleDocumentEntity>>() {
      @Override
      @NonNull
      public List<VehicleDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfDocumentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "documentNumber");
          final int _cursorIndexOfIssuedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issuedDate");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehicleDocumentEntity> _result = new ArrayList<VehicleDocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleDocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpDocumentType;
            _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            final String _tmpDocumentNumber;
            if (_cursor.isNull(_cursorIndexOfDocumentNumber)) {
              _tmpDocumentNumber = null;
            } else {
              _tmpDocumentNumber = _cursor.getString(_cursorIndexOfDocumentNumber);
            }
            final Long _tmpIssuedDate;
            if (_cursor.isNull(_cursorIndexOfIssuedDate)) {
              _tmpIssuedDate = null;
            } else {
              _tmpIssuedDate = _cursor.getLong(_cursorIndexOfIssuedDate);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehicleDocumentEntity(_tmpId,_tmpVehicleId,_tmpDocumentType,_tmpDocumentNumber,_tmpIssuedDate,_tmpExpiryDate,_tmpPhotoPath,_tmpCreatedAt,_tmpUpdatedAt);
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
