package tech.threekilogram.depository.net.retrofit.service;

import okhttp3.ResponseBody;
import tech.threekilogram.depository.net.NetConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-13
 * @time: 14:36
 */
public interface RetrofitConverter<K, V> extends NetConverter<K, V, ResponseBody> {
}
