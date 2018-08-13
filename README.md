# 数据层框架

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency

```
	dependencies {
	        implementation 'com.github.threekilogram:object-cache:1.1'
	}
```

### 使用内存缓存

* MemoryListLoader

```
/* MemoryListLoader 使用数字索引保存/读取数据 */
MemoryListLoader<String> loader = new MemoryListLoader<>();

String value = "HelloAndroidModel - 0";
String value1 = "HelloAndroidModel - 1";
String value2 = "HelloAndroidModel - 2";

// 保存
loader.save( 0, value );
loader.save( 1, value1 );
loader.save( 2, value2 );

// 统计数量
int size = loader.size();

// 读取
String valueLoad0 = loader.load( 0 );

// 测试是否包含该key对应的值
boolean containsOf = loader.containsOf( 2 );

// 移除
loader.remove( 2 );

//移除所有
loader.clear();
```

* MemoryMapLoader

```
/* MemoryListLoader 使用map保存/读取数据 */
MemoryMapLoader<String, String> loader = new MemoryMapLoader<>();

String value = "HelloAndroidModel - 0";
String value1 = "HelloAndroidModel - 1";
String value2 = "HelloAndroidModel - 2";
String key = "key";
String key1 = "key1";
String key2 = "key2";

// 保存
loader.save( key, value );
loader.save( key1, value1 );
loader.save( key2, value2 );

// 统计
int size = loader.size();

// 读取
String valueLoad0 = loader.load( key );

// 测试是否包含该key对应的值
boolean containsOf = loader.containsOf( key2 );

// 移除
loader.remove( key2 );

// 移除所有
loader.clear();
```

* MemoryLruCacheLoader

```
/* MemoryListLoader 使用lruCache保存/读取数据 */
MemoryLruCacheLoader<String, String> loader = new MemoryLruCacheLoader<>( 3 );	//最多3个值

String value = "HelloAndroidModel - 0";
String value1 = "HelloAndroidModel - 1";
String value2 = "HelloAndroidModel - 2";
String key = "key";
String key1 = "key1";
String key2 = "key2";

// 保存
loader.save( key, value );
loader.save( key1, value1 );
loader.save( key2, value2 );

// 统计数量
int size = loader.size();

// 读取
String valueLoad0 = loader.load( key );

// 测试是否包含该key对应的值
boolean containsOf = loader.containsOf( key2 );

// 移除
loader.remove( key2 );

// 移除所有
loader.clear();

// 添加4个值
loader.save( key, value );
loader.save( key1, value1 );
loader.save( key2, value2 );
loader.save( "extra", "extra" );

// 读取大小
size = loader.size();
System.out.println( "after add 4 item size: " + size ); // 3 因为最多3个
String load = loader.load( key ); // 读取最先添加的值
System.out.println( "value at key: " + load ); // null
```

### 使用文件缓存

