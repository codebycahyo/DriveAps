package com.example.kendaraanbp1.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.kendaraanbp1.data.local.dao.DocumentDao;
import com.example.kendaraanbp1.data.local.dao.DocumentDao_Impl;
import com.example.kendaraanbp1.data.local.dao.FuelLogDao;
import com.example.kendaraanbp1.data.local.dao.FuelLogDao_Impl;
import com.example.kendaraanbp1.data.local.dao.ServiceLogDao;
import com.example.kendaraanbp1.data.local.dao.ServiceLogDao_Impl;
import com.example.kendaraanbp1.data.local.dao.VehicleDao;
import com.example.kendaraanbp1.data.local.dao.VehicleDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile VehicleDao _vehicleDao;

  private volatile FuelLogDao _fuelLogDao;

  private volatile ServiceLogDao _serviceLogDao;

  private volatile DocumentDao _documentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleType` TEXT NOT NULL, `brand` TEXT NOT NULL, `model` TEXT NOT NULL, `plateNumber` TEXT NOT NULL, `year` INTEGER NOT NULL, `photoPath` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fuel_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `liters` REAL NOT NULL, `pricePerLiter` REAL NOT NULL, `totalCost` REAL NOT NULL, `odometer` INTEGER NOT NULL, `stationName` TEXT NOT NULL, `receiptPhotoPath` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fuel_logs_vehicleId` ON `fuel_logs` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `service_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `category` TEXT NOT NULL, `workshopName` TEXT NOT NULL, `odometer` INTEGER NOT NULL, `totalCost` REAL NOT NULL, `receiptPhotoPath` TEXT, `nextServiceDate` INTEGER, `nextServiceOdometer` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_logs_vehicleId` ON `service_logs` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicle_documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `documentType` TEXT NOT NULL, `documentNumber` TEXT, `issuedDate` INTEGER, `expiryDate` INTEGER, `photoPath` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vehicle_documents_vehicleId` ON `vehicle_documents` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e117e8c715d8345272977b8a1a0a9021')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `vehicles`");
        db.execSQL("DROP TABLE IF EXISTS `fuel_logs`");
        db.execSQL("DROP TABLE IF EXISTS `service_logs`");
        db.execSQL("DROP TABLE IF EXISTS `vehicle_documents`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsVehicles = new HashMap<String, TableInfo.Column>(9);
        _columnsVehicles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("vehicleType", new TableInfo.Column("vehicleType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("brand", new TableInfo.Column("brand", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("model", new TableInfo.Column("model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("plateNumber", new TableInfo.Column("plateNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVehicles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVehicles = new TableInfo("vehicles", _columnsVehicles, _foreignKeysVehicles, _indicesVehicles);
        final TableInfo _existingVehicles = TableInfo.read(db, "vehicles");
        if (!_infoVehicles.equals(_existingVehicles)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicles(com.example.kendaraanbp1.data.local.entity.VehicleEntity).\n"
                  + " Expected:\n" + _infoVehicles + "\n"
                  + " Found:\n" + _existingVehicles);
        }
        final HashMap<String, TableInfo.Column> _columnsFuelLogs = new HashMap<String, TableInfo.Column>(11);
        _columnsFuelLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("liters", new TableInfo.Column("liters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("pricePerLiter", new TableInfo.Column("pricePerLiter", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("totalCost", new TableInfo.Column("totalCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("odometer", new TableInfo.Column("odometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("stationName", new TableInfo.Column("stationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("receiptPhotoPath", new TableInfo.Column("receiptPhotoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFuelLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFuelLogs.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFuelLogs = new HashSet<TableInfo.Index>(1);
        _indicesFuelLogs.add(new TableInfo.Index("index_fuel_logs_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoFuelLogs = new TableInfo("fuel_logs", _columnsFuelLogs, _foreignKeysFuelLogs, _indicesFuelLogs);
        final TableInfo _existingFuelLogs = TableInfo.read(db, "fuel_logs");
        if (!_infoFuelLogs.equals(_existingFuelLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "fuel_logs(com.example.kendaraanbp1.data.local.entity.FuelLogEntity).\n"
                  + " Expected:\n" + _infoFuelLogs + "\n"
                  + " Found:\n" + _existingFuelLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsServiceLogs = new HashMap<String, TableInfo.Column>(12);
        _columnsServiceLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("workshopName", new TableInfo.Column("workshopName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("odometer", new TableInfo.Column("odometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("totalCost", new TableInfo.Column("totalCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("receiptPhotoPath", new TableInfo.Column("receiptPhotoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("nextServiceDate", new TableInfo.Column("nextServiceDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("nextServiceOdometer", new TableInfo.Column("nextServiceOdometer", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceLogs.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysServiceLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysServiceLogs.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesServiceLogs = new HashSet<TableInfo.Index>(1);
        _indicesServiceLogs.add(new TableInfo.Index("index_service_logs_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoServiceLogs = new TableInfo("service_logs", _columnsServiceLogs, _foreignKeysServiceLogs, _indicesServiceLogs);
        final TableInfo _existingServiceLogs = TableInfo.read(db, "service_logs");
        if (!_infoServiceLogs.equals(_existingServiceLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "service_logs(com.example.kendaraanbp1.data.local.entity.ServiceLogEntity).\n"
                  + " Expected:\n" + _infoServiceLogs + "\n"
                  + " Found:\n" + _existingServiceLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsVehicleDocuments = new HashMap<String, TableInfo.Column>(9);
        _columnsVehicleDocuments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("documentType", new TableInfo.Column("documentType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("documentNumber", new TableInfo.Column("documentNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("issuedDate", new TableInfo.Column("issuedDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("expiryDate", new TableInfo.Column("expiryDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleDocuments.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicleDocuments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVehicleDocuments.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVehicleDocuments = new HashSet<TableInfo.Index>(1);
        _indicesVehicleDocuments.add(new TableInfo.Index("index_vehicle_documents_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoVehicleDocuments = new TableInfo("vehicle_documents", _columnsVehicleDocuments, _foreignKeysVehicleDocuments, _indicesVehicleDocuments);
        final TableInfo _existingVehicleDocuments = TableInfo.read(db, "vehicle_documents");
        if (!_infoVehicleDocuments.equals(_existingVehicleDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicle_documents(com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity).\n"
                  + " Expected:\n" + _infoVehicleDocuments + "\n"
                  + " Found:\n" + _existingVehicleDocuments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e117e8c715d8345272977b8a1a0a9021", "363541f178292b82d6540ea973f3e429");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "vehicles","fuel_logs","service_logs","vehicle_documents");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `vehicles`");
      _db.execSQL("DELETE FROM `fuel_logs`");
      _db.execSQL("DELETE FROM `service_logs`");
      _db.execSQL("DELETE FROM `vehicle_documents`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(VehicleDao.class, VehicleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FuelLogDao.class, FuelLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ServiceLogDao.class, ServiceLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DocumentDao.class, DocumentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public VehicleDao vehicleDao() {
    if (_vehicleDao != null) {
      return _vehicleDao;
    } else {
      synchronized(this) {
        if(_vehicleDao == null) {
          _vehicleDao = new VehicleDao_Impl(this);
        }
        return _vehicleDao;
      }
    }
  }

  @Override
  public FuelLogDao fuelLogDao() {
    if (_fuelLogDao != null) {
      return _fuelLogDao;
    } else {
      synchronized(this) {
        if(_fuelLogDao == null) {
          _fuelLogDao = new FuelLogDao_Impl(this);
        }
        return _fuelLogDao;
      }
    }
  }

  @Override
  public ServiceLogDao serviceLogDao() {
    if (_serviceLogDao != null) {
      return _serviceLogDao;
    } else {
      synchronized(this) {
        if(_serviceLogDao == null) {
          _serviceLogDao = new ServiceLogDao_Impl(this);
        }
        return _serviceLogDao;
      }
    }
  }

  @Override
  public DocumentDao documentDao() {
    if (_documentDao != null) {
      return _documentDao;
    } else {
      synchronized(this) {
        if(_documentDao == null) {
          _documentDao = new DocumentDao_Impl(this);
        }
        return _documentDao;
      }
    }
  }
}
