package com.newsscraper.app.data.local;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NewsDatabase_Impl extends NewsDatabase {
  private volatile NewsDao _newsDao;

  private volatile SeenIdDao _seenIdDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `news_items` (`id` TEXT NOT NULL, `headline` TEXT NOT NULL, `summary` TEXT NOT NULL, `source` TEXT NOT NULL, `source_url` TEXT NOT NULL, `published_at` TEXT NOT NULL, `scraped_at` TEXT NOT NULL, `category` TEXT NOT NULL, `region` TEXT NOT NULL, `image_url` TEXT, `is_breaking` INTEGER NOT NULL, `is_update` INTEGER NOT NULL, `read_time_seconds` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `seen_ids` (`id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a5f090ae9d13dfaa69814b68008eefe0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `news_items`");
        db.execSQL("DROP TABLE IF EXISTS `seen_ids`");
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
        final HashMap<String, TableInfo.Column> _columnsNewsItems = new HashMap<String, TableInfo.Column>(13);
        _columnsNewsItems.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("headline", new TableInfo.Column("headline", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("source_url", new TableInfo.Column("source_url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("published_at", new TableInfo.Column("published_at", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("scraped_at", new TableInfo.Column("scraped_at", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("region", new TableInfo.Column("region", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("image_url", new TableInfo.Column("image_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("is_breaking", new TableInfo.Column("is_breaking", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("is_update", new TableInfo.Column("is_update", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNewsItems.put("read_time_seconds", new TableInfo.Column("read_time_seconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNewsItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNewsItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNewsItems = new TableInfo("news_items", _columnsNewsItems, _foreignKeysNewsItems, _indicesNewsItems);
        final TableInfo _existingNewsItems = TableInfo.read(db, "news_items");
        if (!_infoNewsItems.equals(_existingNewsItems)) {
          return new RoomOpenHelper.ValidationResult(false, "news_items(com.newsscraper.app.data.local.NewsItem).\n"
                  + " Expected:\n" + _infoNewsItems + "\n"
                  + " Found:\n" + _existingNewsItems);
        }
        final HashMap<String, TableInfo.Column> _columnsSeenIds = new HashMap<String, TableInfo.Column>(2);
        _columnsSeenIds.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSeenIds.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSeenIds = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSeenIds = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSeenIds = new TableInfo("seen_ids", _columnsSeenIds, _foreignKeysSeenIds, _indicesSeenIds);
        final TableInfo _existingSeenIds = TableInfo.read(db, "seen_ids");
        if (!_infoSeenIds.equals(_existingSeenIds)) {
          return new RoomOpenHelper.ValidationResult(false, "seen_ids(com.newsscraper.app.data.local.SeenIdEntity).\n"
                  + " Expected:\n" + _infoSeenIds + "\n"
                  + " Found:\n" + _existingSeenIds);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a5f090ae9d13dfaa69814b68008eefe0", "a67f47f1bb2ea36df4bc4662f376df4d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "news_items","seen_ids");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `news_items`");
      _db.execSQL("DELETE FROM `seen_ids`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(NewsDao.class, NewsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SeenIdDao.class, SeenIdDao_Impl.getRequiredConverters());
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
  public NewsDao newsDao() {
    if (_newsDao != null) {
      return _newsDao;
    } else {
      synchronized(this) {
        if(_newsDao == null) {
          _newsDao = new NewsDao_Impl(this);
        }
        return _newsDao;
      }
    }
  }

  @Override
  public SeenIdDao seenIdDao() {
    if (_seenIdDao != null) {
      return _seenIdDao;
    } else {
      synchronized(this) {
        if(_seenIdDao == null) {
          _seenIdDao = new SeenIdDao_Impl(this);
        }
        return _seenIdDao;
      }
    }
  }
}
