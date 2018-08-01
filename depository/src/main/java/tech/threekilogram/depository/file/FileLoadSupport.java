package tech.threekilogram.depository.file;

import tech.threekilogram.depository.Loader;

/**
 * 使用一个key从文件系统加载数据
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author liujin
 */
public interface FileLoadSupport<K, V> extends Loader<K, V> {

}
