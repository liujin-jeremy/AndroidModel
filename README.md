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
	         implementation 'com.github.threekilogram:AndroidCache:1.5'
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

* FileLoader<T> : 可以保存到本地文件
* DiskLruLoader<T> : 底层使用DiskLruCache保存文件

> 以上两个都需要 FileConverter<T> 辅助将一个数据流转换为对应的对象

#### 示例

* string

```
// FileStringConverter 会将文件流转为string对象
FileLoader<String> loader = new FileLoader<>( TEMP, new FileStringConverter() );

String key = "key";
String value = "曾经沧海难为水";
// 保存
loader.save( key, value );
// 读取
String load = loader.load( key );
// 获取文件
File file = loader.getFile( key );
// 删除文件
String remove = loader.remove( key );
// 测试是否有该文件
boolean containsOf = loader.containsOf( key );
```

```
// 使用 DiskLruLoader 读取保存string对象
DiskLruLoader<String> loader = new DiskLruLoader<>(
    TEMP,
    MAX_SIZE,
    new FileStringConverter() 
);
```

* Json对象

```
// FileGsonConverter 会将文件流转为指定bean对象,底层使用Gson
FileLoader<Bean> loader = new FileLoader<>(
    TEMP,
    new FileGsonConverter<Bean>( Bean.class )
);

String key = "json";
// 创建一个value用于保存
Gson gson = GsonClient.INSTANCE;
Bean bean = gson.fromJson( JSON, Bean.class );
// 保存
loader.save( key, bean );
// 读取
Bean load = loader.load( key );
// 获取文件
File file = loader.getFile( key );
// 删除
loader.remove( key );
// 测试是否包含改文件
boolean containsOf = loader.containsOf( key );
```

```
// 使用 DiskLruLoader 读取保存json对象
DiskLruLoader<Bean> loader = new DiskLruLoader<>(
    TEMP,
    MAX_SIZE,
    new FileGsonConverter<Bean>( Bean.class )
);
```

> 目前框架只实现了 FileStringConverter 和 FileGsonConverter, 其他类型数据需要自己实现FileConverter<T>接口,完成转换工作

### 使用网络

* 下载

```
// 配置文件夹
RetrofitDowner loader = new RetrofitDowner( TEMP );
// url
final String url00 = "https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg";
// 从网络下载
File file = loader.load( url00 );
```

* string

```
// 配置
RetrofitLoader<String> loader = new RetrofitLoader<>( new RetrofitStringConverter() );
final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";
String load = loader.load( url );
```

* json

```
// json
RetrofitLoader<Bean> loader = new RetrofitLoader<>( new RetrofitGsonConverter<Bean>(Bean.class) );
// url
final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";
Bean bean = loader.load( url );
```
