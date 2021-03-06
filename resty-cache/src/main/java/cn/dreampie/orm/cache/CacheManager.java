package cn.dreampie.orm.cache;

import cn.dreampie.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract method to be sub-classed by various caching technologies.
 */
public abstract class CacheManager {
  private final static Logger logger = Logger.getLogger(CacheManager.class);

  List<CacheEventListener> listeners = new ArrayList<CacheEventListener>();

  /**
   * Returns a cached item. Can return null if not found.
   *
   * @param group group of caches - this is a name of a table for which query results are cached
   * @param key   key of the item.
   * @return a cached item. Can return null if not found.
   */
  public abstract <T> T getCache(String group, String key);

  /**
   * Adds item to cache.
   *
   * @param group group name of cache.
   * @param key   key of the item.
   * @param cache cache item to add to cache.
   */
  public abstract void addCache(String group, String key, Object cache);

  /**
   * remove item from cache.
   *
   * @param group group name of cache.
   * @param key   key of the item.
   */
  public abstract void removeCache(String group, String key);

  public abstract void doFlush(CacheEvent event);


  /**
   * Flash cache.
   *
   * @param event type of caches to flush.
   */
  public final void flush(CacheEvent event) {
    doFlush(event);
    for (CacheEventListener listener : listeners) {
      try {
        listener.onFlush(event);
      } catch (Throwable e) {
        logger.warn("failed to propagate cache event: %s to listener: %s", event, listener, e);
      }
    }
    if (logger.isInfoEnabled()) {
      String message = "Cache purged: " + (event.getType() == CacheEvent.CacheEventType.ALL
          ? "all caches" : "table: " + event.getGroup());
      logger.info(message);
    }
  }

  public final void addCacheEventListener(CacheEventListener listener) {
    listeners.add(listener);
  }

  public final void removeCacheEventListener(CacheEventListener listener) {
    listeners.remove(listener);
  }

  public final void removeAllCacheEventListeners() {
    listeners = new ArrayList<CacheEventListener>();
  }
}
