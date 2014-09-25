package org.cache2k;

/*
 * #%L
 * cache2k API only package
 * %%
 * Copyright (C) 2000 - 2014 headissue GmbH, Munich
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.cache2k.spi.Cache2kCoreProvider;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author Jens Wilke; created: 2013-06-25
 */
public abstract class CacheBuilder<K,T>
  extends RootAnyBuilder<K, T> implements Cloneable {

  private static CacheBuilder PROTOTYPE;

  static {
    try {
      Cache2kCoreProvider _provider = Cache2kCoreProvider.get();
      PROTOTYPE =
        (CacheBuilder) _provider.getBuilderImplementation().newInstance();
    } catch (Exception ex) {
      throw new LinkageError("cache2k-core module missing, no builder prototype", ex);
    }
  }

  @SuppressWarnings("unchecked")
  public static <K,T> CacheBuilder<K,T> newCache(Class<K> _keyType, Class<T> _valueType) {
    CacheBuilder<K,T> cb = null;
    try {
      cb = (CacheBuilder<K,T>) PROTOTYPE.clone();
    } catch (CloneNotSupportedException ignored) {  }
    cb.ctor(_keyType, _valueType, null);
    return cb;
  }

  @SuppressWarnings("unchecked")
  public static <K, C extends Collection<T>, T> CacheBuilder<K, C> newCache(
    Class<K> _keyType, Class<C> _collectionType, Class<T> _entryType) {
    CacheBuilder<K,C> cb = null;
    try {
      cb = (CacheBuilder<K,C>) PROTOTYPE.clone();
    } catch (CloneNotSupportedException ignored) { }
    cb.ctor(_keyType, _collectionType, _entryType);
    return cb;
  }

  protected CacheSource cacheSource;
  protected CacheSourceWithMetaInfo cacheSourceWithMetaInfo;
  protected RefreshController refreshController;
  protected ExperimentalBulkCacheSource experimentalBulkCacheSource;
  protected BulkCacheSource bulkCacheSource;

  /** Builder is constructed from prototype */
  protected void ctor(Class<K> _keyType, Class<T> _valueType, @Nullable Class<?> _entryType) {
    root = this;
    config = new CacheConfig();
    config.setValueType(_valueType);
    config.setKeyType(_keyType);
    config.setEntryType(_entryType);
  }

  /**
   * Constructs a cache name out of the simple class name and fieldname.
   *
   * a cache name should be unique within an application / cache manager!
   */
  public CacheBuilder<K, T> name(Class<?> _class, String _fieldName) {
    config.setName(_class.getSimpleName() + "." + _fieldName);
    return this;
  }

  public CacheBuilder<K, T> name(Class<?> _class) {
    config.setName(_class.getSimpleName());
    return this;
  }

  /**
   * Constructs a cache name out of the simple class name and fieldname.
   *
   * a cache name should be unique within an application / cache manager!
   */
  public CacheBuilder<K, T> name(Object _containingObject, String _fieldName) {
    return name(_containingObject.getClass(), _fieldName);
  }

  public CacheBuilder<K, T> name(String v) {
    config.setName(v);
    return this;
  }

  public CacheBuilder<K, T> maxSize(int v) {
    config.setMaxSize(v);
    return this;
  }

  public CacheBuilder<K, T> maxSizeBound(int v) {
    config.setMaxSizeHighBound(v);
    return this;
  }

  public CacheBuilder<K, T> eternal(boolean v) {
    config.setEternal(v);
    return this;
  }

  public CacheBuilder<K, T> heapEntryCapacity(int v) {
    config.setHeapEntryCapacity(v);
    return this;
  }

  public CacheBuilder<K, T> expirySecs(int v) {
    config.setExpirySeconds(v);
    return this;
  }

  public CacheBuilder<K, T> expiryMillis(long v) {
    config.setExpiryMillis(v);
    return this;
  }

  public CacheBuilder<K, T> source(CacheSource<K, T> g) {
    cacheSource = g;
    return this;
  }

  public CacheBuilder<K, T> source(CacheSourceWithMetaInfo<K, T> g) {
    cacheSourceWithMetaInfo = g;
    return this;
  }

  public CacheBuilder<K, T> source(ExperimentalBulkCacheSource<K, T> g) {
    experimentalBulkCacheSource = g;
    return this;
  }

  public CacheBuilder<K, T> source(BulkCacheSource<K, T> g) {
    bulkCacheSource = g;
    return this;
  }

  public CacheBuilder<K, T> refreshController(RefreshController c) {
    refreshController = c;
    return this;
  }

  public CacheBuilder<K, T> implementation(Class<?> c) {
    config.setImplementation(c);
    return this;
  }

  @Deprecated
  public CacheConfig getConfig() {
    return createConfiguration();
  }


  /**
   * Builds a cache with the specified configuration parameters.
   * The builder reused to build caches with similar or identical
   * configuration. The builder is not thread safe.
   */
  public abstract Cache<K, T> build();

}
