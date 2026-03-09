package com.newsscraper.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class NewsDao_Impl implements NewsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NewsItem> __insertionAdapterOfNewsItem;

  private final EntityDeletionOrUpdateAdapter<NewsItem> __deletionAdapterOfNewsItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public NewsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNewsItem = new EntityInsertionAdapter<NewsItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `news_items` (`id`,`headline`,`summary`,`source`,`source_url`,`published_at`,`scraped_at`,`category`,`region`,`image_url`,`is_breaking`,`is_update`,`read_time_seconds`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NewsItem entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getHeadline());
        statement.bindString(3, entity.getSummary());
        statement.bindString(4, entity.getSource());
        statement.bindString(5, entity.getSource_url());
        statement.bindString(6, entity.getPublished_at());
        statement.bindString(7, entity.getScraped_at());
        statement.bindString(8, entity.getCategory());
        statement.bindString(9, entity.getRegion());
        if (entity.getImage_url() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getImage_url());
        }
        final int _tmp = entity.is_breaking() ? 1 : 0;
        statement.bindLong(11, _tmp);
        final int _tmp_1 = entity.is_update() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getRead_time_seconds());
      }
    };
    this.__deletionAdapterOfNewsItem = new EntityDeletionOrUpdateAdapter<NewsItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `news_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NewsItem entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM news_items";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<NewsItem> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNewsItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final NewsItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNewsItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final NewsItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNewsItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NewsItem>> getAllNews() {
    final String _sql = "SELECT * FROM news_items ORDER BY published_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"news_items"}, new Callable<List<NewsItem>>() {
      @Override
      @NonNull
      public List<NewsItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "source_url");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "published_at");
          final int _cursorIndexOfScrapedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scraped_at");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final int _cursorIndexOfIsBreaking = CursorUtil.getColumnIndexOrThrow(_cursor, "is_breaking");
          final int _cursorIndexOfIsUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "is_update");
          final int _cursorIndexOfReadTimeSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "read_time_seconds");
          final List<NewsItem> _result = new ArrayList<NewsItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NewsItem _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadline;
            _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSource_url;
            _tmpSource_url = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpPublished_at;
            _tmpPublished_at = _cursor.getString(_cursorIndexOfPublishedAt);
            final String _tmpScraped_at;
            _tmpScraped_at = _cursor.getString(_cursorIndexOfScrapedAt);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpImage_url;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImage_url = null;
            } else {
              _tmpImage_url = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final boolean _tmpIs_breaking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBreaking);
            _tmpIs_breaking = _tmp != 0;
            final boolean _tmpIs_update;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUpdate);
            _tmpIs_update = _tmp_1 != 0;
            final int _tmpRead_time_seconds;
            _tmpRead_time_seconds = _cursor.getInt(_cursorIndexOfReadTimeSeconds);
            _item = new NewsItem(_tmpId,_tmpHeadline,_tmpSummary,_tmpSource,_tmpSource_url,_tmpPublished_at,_tmpScraped_at,_tmpCategory,_tmpRegion,_tmpImage_url,_tmpIs_breaking,_tmpIs_update,_tmpRead_time_seconds);
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
  public Object getLatestNews(final int limit,
      final Continuation<? super List<NewsItem>> $completion) {
    final String _sql = "SELECT * FROM news_items ORDER BY published_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NewsItem>>() {
      @Override
      @NonNull
      public List<NewsItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "source_url");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "published_at");
          final int _cursorIndexOfScrapedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scraped_at");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final int _cursorIndexOfIsBreaking = CursorUtil.getColumnIndexOrThrow(_cursor, "is_breaking");
          final int _cursorIndexOfIsUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "is_update");
          final int _cursorIndexOfReadTimeSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "read_time_seconds");
          final List<NewsItem> _result = new ArrayList<NewsItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NewsItem _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadline;
            _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSource_url;
            _tmpSource_url = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpPublished_at;
            _tmpPublished_at = _cursor.getString(_cursorIndexOfPublishedAt);
            final String _tmpScraped_at;
            _tmpScraped_at = _cursor.getString(_cursorIndexOfScrapedAt);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpImage_url;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImage_url = null;
            } else {
              _tmpImage_url = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final boolean _tmpIs_breaking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBreaking);
            _tmpIs_breaking = _tmp != 0;
            final boolean _tmpIs_update;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUpdate);
            _tmpIs_update = _tmp_1 != 0;
            final int _tmpRead_time_seconds;
            _tmpRead_time_seconds = _cursor.getInt(_cursorIndexOfReadTimeSeconds);
            _item = new NewsItem(_tmpId,_tmpHeadline,_tmpSummary,_tmpSource,_tmpSource_url,_tmpPublished_at,_tmpScraped_at,_tmpCategory,_tmpRegion,_tmpImage_url,_tmpIs_breaking,_tmpIs_update,_tmpRead_time_seconds);
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

  @Override
  public Flow<List<NewsItem>> getNewsByCategory(final String category) {
    final String _sql = "SELECT * FROM news_items WHERE category = ? ORDER BY published_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"news_items"}, new Callable<List<NewsItem>>() {
      @Override
      @NonNull
      public List<NewsItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "source_url");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "published_at");
          final int _cursorIndexOfScrapedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scraped_at");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final int _cursorIndexOfIsBreaking = CursorUtil.getColumnIndexOrThrow(_cursor, "is_breaking");
          final int _cursorIndexOfIsUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "is_update");
          final int _cursorIndexOfReadTimeSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "read_time_seconds");
          final List<NewsItem> _result = new ArrayList<NewsItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NewsItem _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadline;
            _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSource_url;
            _tmpSource_url = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpPublished_at;
            _tmpPublished_at = _cursor.getString(_cursorIndexOfPublishedAt);
            final String _tmpScraped_at;
            _tmpScraped_at = _cursor.getString(_cursorIndexOfScrapedAt);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpImage_url;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImage_url = null;
            } else {
              _tmpImage_url = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final boolean _tmpIs_breaking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBreaking);
            _tmpIs_breaking = _tmp != 0;
            final boolean _tmpIs_update;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUpdate);
            _tmpIs_update = _tmp_1 != 0;
            final int _tmpRead_time_seconds;
            _tmpRead_time_seconds = _cursor.getInt(_cursorIndexOfReadTimeSeconds);
            _item = new NewsItem(_tmpId,_tmpHeadline,_tmpSummary,_tmpSource,_tmpSource_url,_tmpPublished_at,_tmpScraped_at,_tmpCategory,_tmpRegion,_tmpImage_url,_tmpIs_breaking,_tmpIs_update,_tmpRead_time_seconds);
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
  public Flow<List<NewsItem>> getBreakingNews() {
    final String _sql = "SELECT * FROM news_items WHERE is_breaking = 1 ORDER BY published_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"news_items"}, new Callable<List<NewsItem>>() {
      @Override
      @NonNull
      public List<NewsItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "source_url");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "published_at");
          final int _cursorIndexOfScrapedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scraped_at");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final int _cursorIndexOfIsBreaking = CursorUtil.getColumnIndexOrThrow(_cursor, "is_breaking");
          final int _cursorIndexOfIsUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "is_update");
          final int _cursorIndexOfReadTimeSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "read_time_seconds");
          final List<NewsItem> _result = new ArrayList<NewsItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NewsItem _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadline;
            _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSource_url;
            _tmpSource_url = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpPublished_at;
            _tmpPublished_at = _cursor.getString(_cursorIndexOfPublishedAt);
            final String _tmpScraped_at;
            _tmpScraped_at = _cursor.getString(_cursorIndexOfScrapedAt);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpImage_url;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImage_url = null;
            } else {
              _tmpImage_url = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final boolean _tmpIs_breaking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBreaking);
            _tmpIs_breaking = _tmp != 0;
            final boolean _tmpIs_update;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUpdate);
            _tmpIs_update = _tmp_1 != 0;
            final int _tmpRead_time_seconds;
            _tmpRead_time_seconds = _cursor.getInt(_cursorIndexOfReadTimeSeconds);
            _item = new NewsItem(_tmpId,_tmpHeadline,_tmpSummary,_tmpSource,_tmpSource_url,_tmpPublished_at,_tmpScraped_at,_tmpCategory,_tmpRegion,_tmpImage_url,_tmpIs_breaking,_tmpIs_update,_tmpRead_time_seconds);
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
  public Object getNewsById(final String id, final Continuation<? super NewsItem> $completion) {
    final String _sql = "SELECT * FROM news_items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NewsItem>() {
      @Override
      @Nullable
      public NewsItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "source_url");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "published_at");
          final int _cursorIndexOfScrapedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scraped_at");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final int _cursorIndexOfIsBreaking = CursorUtil.getColumnIndexOrThrow(_cursor, "is_breaking");
          final int _cursorIndexOfIsUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "is_update");
          final int _cursorIndexOfReadTimeSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "read_time_seconds");
          final NewsItem _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadline;
            _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSource_url;
            _tmpSource_url = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpPublished_at;
            _tmpPublished_at = _cursor.getString(_cursorIndexOfPublishedAt);
            final String _tmpScraped_at;
            _tmpScraped_at = _cursor.getString(_cursorIndexOfScrapedAt);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpImage_url;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImage_url = null;
            } else {
              _tmpImage_url = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final boolean _tmpIs_breaking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBreaking);
            _tmpIs_breaking = _tmp != 0;
            final boolean _tmpIs_update;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsUpdate);
            _tmpIs_update = _tmp_1 != 0;
            final int _tmpRead_time_seconds;
            _tmpRead_time_seconds = _cursor.getInt(_cursorIndexOfReadTimeSeconds);
            _result = new NewsItem(_tmpId,_tmpHeadline,_tmpSummary,_tmpSource,_tmpSource_url,_tmpPublished_at,_tmpScraped_at,_tmpCategory,_tmpRegion,_tmpImage_url,_tmpIs_breaking,_tmpIs_update,_tmpRead_time_seconds);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM news_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
