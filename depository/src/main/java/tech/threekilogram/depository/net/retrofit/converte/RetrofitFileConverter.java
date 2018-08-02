package tech.threekilogram.depository.net.retrofit.converte;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import tech.threekilogram.depository.file.FileNameConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 8:44
 */
public class RetrofitFileConverter<K> implements RetrofitConverter<K, Object> {

      private File                 mDir;
      private FileNameConverter<K> mFileNameConverter;

      public RetrofitFileConverter (
          File dir, FileNameConverter<K> fileNameConverter) {

            mDir = dir;
            mFileNameConverter = fileNameConverter;
      }

      @Override
      public Object onResponse (K key, long contentLength, InputStream stream) throws Exception {

            File toSave = new File(mDir, mFileNameConverter.fileName(key));
            FileOutputStream outputStream = new FileOutputStream(toSave);

            byte[] bytes = new byte[512];
            int len = 0;

            while((len = stream.read(bytes)) != -1) {
                  outputStream.write(bytes, 0, len);
            }

            /* just save stream to file not return a value */
            return null;
      }
}
