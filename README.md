# 数据层框架

```
implementation 'tech.threekilogram:model:2.0.3'
```

### 内存缓存

> 内存缓存包含3个种类,
>
> 一个是MemoryList使用数字作为索引保存数据,
>
> 一个是MemoryMap,使用键值对保存数据,
>
> 还有一个是MemoryLruCache,只能保存一定数量数据,到达上限之后删除最近最少使用的数据
>

* MemoryList使用

```
/* MemoryListLoader 使用数字索引保存/读取数据 */
MemoryList<String> loader = new MemoryList<>();

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

* MemoryMap使用

```
/* MemoryMap 使用map保存/读取数据 */
MemoryMap<String, String> loader = new MemoryMap<>();

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

* MemoryLruCache使用

```
/* MemoryLruCache 使用lruCache保存/读取数据 */
MemoryLruCache<String, String> loader = new MemoryLruCache<>( 3 );	//最多3个值

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

### 文件缓存

* FileLoader<T> : 可以保存到本地文件
* DiskLruLoader<T> : 底层使用DiskLruCache保存文件,会自动管理文件夹大小

> 以上两个都需要 FileConverter<T> 辅助将一个数据流转换为对应的对象

#### 示例

* 从本地文件读取string

```
// FileStringConverter 会将文件流转为string对象
FileStringConverter converter = new FileStringConverter()
// 文件创建
FileLoader<String> loader = new FileLoader<>( TEMP, converter );
// disk文件创建
DiskLruLoader<String> loader = new DiskLruLoader<>(
          TEMP,
          10 * 1024 * 1024,
          converter
);

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

* Json对象

```
// FileJsonConverter 会将文件流转为指定bean对象,底层使用Gson
FileLoader<Bean> loader = new FileLoader<>(
    TEMP,
    new FileJsonConverter<Bean>( Bean.class )
);

// disk文件创建
DiskLruLoader<String> loader = new DiskLruLoader<>(
          TEMP,
          10 * 1024 * 1024,
          new FileJsonConverter<Bean>( Bean.class )
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

> 目前框架只实现了 FileStringConverter 和 FileGsonConverter, 其他类型数据需要自己实现FileConverter<T>接口,完成转换工作

### 网络缓存

* 从网络获取string

```
// 配置
RetrofitLoader<String> loader = new RetrofitLoader<>( new BodyStringConverter() );
OkhttpLoader<String> loader = new OkhttpLoader<>( new BodyStringConverter() );

// url
final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";

// 读取
String load = loader.load( url );
```

* 从网络获取json

```
// json
RetrofitLoader<Bean> loader = new RetrofitLoader<>( new BodyJsonConverter<>(Bean.class) );
OkhttpLoader<Bean> loader = new OkhttpLoader<>( new BodyJsonConverter<>( Bean.class ) );

// url
final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";

// 读取
Bean bean = loader.load( url );
```

### BitmapLoader : bitmap三级缓存

> 该类是一个综合类型缓存器,用于提供bitmap三级缓存

```
private BitmapLoader mLoader;

// 创建
mLoader = new BitmapLoader(
    (int) Runtime.getRuntime().maxMemory() >> 3, --> 最大使用内存
    getContext().getExternalFilesDir( "bitmap" ) --> 缓存文件夹
);

// 配置图片缩放
mLoader.configBitmap( ScreenSize.getWidth(), ScreenSize.getHeight() );

// 内存加载图片
Bitmap bitmap = mLoader.loadFromMemory( url );
// 文件加载图片
Bitmap bitmap = mLoader.loadFromFile( url );
// 网络加载图片
Bitmap bitmap = mLoader.loadFromNet( url );
```

### JsonLoader : json对象三级缓存

> 该类提供了json三级缓存

* 创建

```
private JsonLoader<GankDayBean>     mDayLoader;

// 指定缓存文件夹,如果不需要文件缓存,那么可以不指定
File jsonFile = getContext().getExternalFilesDir( "jsonFile" );
mDayLoader = new JsonLoader<>(
	-1,		//--> 指定内存中保留的数据量,负数表示无限多
    jsonFile,
    GankDayBean.class	//--> json bean 对象
);
```

```
// 内存
GankDayBean dayBean = mDayLoader.loadFromMemory( url );
boolean b = mDayLoader.containsOfMemory( url );

// 文件
GankDayBean dayBean = mDayLoader.loadFromFile( url );
boolean b = mDayLoader.containsOfFile( url );

// 网络
boolean containsOf = mDayLoader.containsOf( url );
GankDayBean dayBean = mDayLoader.loadFromNet( url );
```

## 下载文件

* Downer下载 : 该类提供简单下载方式

```
File down = Downer.downloadTo( dir, url );
```

* RetrofitDowner : 该类用于下载大量文件到一个文件夹

```
// 配置文件夹
RetrofitDowner loader = new RetrofitDowner( TEMP );

// url
final String url00 = "https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg";

// 从网络下载
File file = loader.load( url00 );

// 设置下载进度监听
OnProgressUpdateListener onProgressUpdateListener = new OnProgressUpdateListener() {
      @Override
      public void onProgressUpdate ( String key, long total, long current ) {
            Log.e( TAG, "onProgressUpdate : " + total + " " + current );
      }
};
mRetrofitDowner.setOnProgressUpdateListener( onProgressUpdateListener );
```

## ObjectLoader : 从数据流中读取json对象

* 网络

```
GankDayBean dayBean = ObjectLoader.loadFromNet(
    "https://gank.io/api/day/2015/08/07",
    GankDayBean.class
);
```

* 保存到文件

```
ObjectLoader.toFile( file, categoryBean, GankCategoryBean.class );
```

* 从文件读取

```
GankCategoryBean bean = ObjectLoader.loadFromFile( file, GankCategoryBean.class );
```

## StreamLoader : 从数据流中读取对象

> 该类提供一些工具方法,简化从数据流中加载对象

* String

```
// 从网络读取
mString = StreamLoader
    .loadStringFromNet( "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1" );
```

```
// 从文件读取/保存
StreamLoader.saveStringToFile( mString, mFile );
String s = StreamLoader.loadStringFromFile( mFile );
```

* Json

```
// 从网络
mBean = StreamLoader
    .loadJsonFromNet(
        "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1",
        GankCategoryBean.class
    );
```

```
// 从文件读取/保存
StreamLoader.saveJsonToFile( mJsonFile, GankCategoryBean.class, mBean );
GankCategoryBean bean = StreamLoader
    .loadJsonFromFile( mJsonFile, GankCategoryBean.class );
```

* bitmap

```
Bitmap bitmap = StreamLoader.loadBitmapFromNet(
    "https://ws1.sinaimg.cn/large/0065oQSqly1fvexaq313uj30qo0wldr4.jpg" );
```

* 下载

```
StreamLoader
    .downLoad(
        "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1",
        file,
        new OnDownloadUpdateListener() {
              @Override
              public void onProgressUpdate (
                  File file, String url, long total, long current ) {
                    Log.e( TAG, "onProgressUpdate : " + current );
              }
              @Override
              public void onFinished ( File file, String url ) {
                    Log.e( TAG, "onFinished : " + file );
              }
        }
    );
```