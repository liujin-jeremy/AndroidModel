package tech.threekilogram.androidmodellib;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.file.converter.FileJsonConverter;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.function.instance.GsonClient;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-23
 * @time: 8:40
 */
public class TestFileLoaderFragment extends Fragment implements OnClickListener {

      private FileLoader<String>           mStringFileLoader;
      private FileLoader<GankCategoryBean> mJsonFileLoader;
      private FileLoader<Bitmap>           mBitmapFileLoader;
      private Button                       mString;
      private TextView                     mMsg;
      private Button                       mLoadString;
      private Button                       mRemoveString;
      private Button                       mSaveJson;
      private Button                       mLoadJson;
      private Button                       mRemoveJson;
      private Button                       mSaveBitmap;
      private Button                       mLoadBitmap;
      private Button                       mRemoveBitmap;
      private ImageView                    mImageView;

      private final String JSON = "{\n"
          + "    \"error\": false,\n"
          + "    \"results\": [\n"
          + "        {\n"
          + "            \"_id\": \"5b7b836c9d212201e982de6e\",\n"
          + "            \"createdAt\": \"2018-08-21T11:13:48.989Z\",\n"
          + "            \"desc\": \"2018-08-21\",\n"
          + "            \"publishedAt\": \"2018-08-21T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ws1.sinaimg.cn/large/0065oQSqly1fuh5fsvlqcj30sg10onjk.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";

      public static TestFileLoaderFragment newInstance ( ) {

            return new TestFileLoaderFragment();
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_file_loader, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            File file = getContext().getExternalFilesDir( "file" );

            mStringFileLoader = new FileLoader<>(
                file,
                new FileStringConverter()
            );

            mJsonFileLoader = new FileLoader<>(
                file,
                new FileJsonConverter<>( GankCategoryBean.class )
            );

            ScreenSize.init( getContext() );
            FileBitmapConverter converter = new FileBitmapConverter();
            mBitmapFileLoader = new FileLoader<>(
                file,
                converter
            );
            converter.configBitmap(
                mBitmapFileLoader,
                ScreenSize.getWidth(),
                ScreenSize.getHeight(),
                BitmapConverter.MATCH_WIDTH,
                Config.RGB_565
            );
      }

      private void initView ( @NonNull final View itemView ) {

            mString = (Button) itemView.findViewById( R.id.saveString );
            mString.setOnClickListener( this );
            mMsg = (TextView) itemView.findViewById( R.id.msg );
            mLoadString = (Button) itemView.findViewById( R.id.loadString );
            mLoadString.setOnClickListener( this );
            mRemoveString = (Button) itemView.findViewById( R.id.removeString );
            mRemoveString.setOnClickListener( this );
            mSaveJson = (Button) itemView.findViewById( R.id.saveJson );
            mSaveJson.setOnClickListener( this );
            mLoadJson = (Button) itemView.findViewById( R.id.loadJson );
            mLoadJson.setOnClickListener( this );
            mRemoveJson = (Button) itemView.findViewById( R.id.removeJson );
            mRemoveJson.setOnClickListener( this );
            mSaveBitmap = (Button) itemView.findViewById( R.id.saveBitmap );
            mSaveBitmap.setOnClickListener( this );
            mLoadBitmap = (Button) itemView.findViewById( R.id.loadBitmap );
            mLoadBitmap.setOnClickListener( this );
            mRemoveBitmap = (Button) itemView.findViewById( R.id.removeBitmap );
            mRemoveBitmap.setOnClickListener( this );
            mImageView = (ImageView) itemView.findViewById( R.id.imageView );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.saveString:
                        saveString();
                        break;
                  case R.id.loadString:
                        loadString();
                        break;
                  case R.id.removeString:
                        deleteString();
                        break;
                  case R.id.saveJson:
                        saveJson();
                        break;
                  case R.id.loadJson:
                        loadJson();
                        break;
                  case R.id.removeJson:
                        deleteJson();
                        break;
                  case R.id.saveBitmap:
                        saveBitmap();
                        break;
                  case R.id.loadBitmap:
                        loadBitmap();
                        break;
                  case R.id.removeBitmap:
                        deleteBitmap();
                        break;
                  default:
                        break;
            }
      }

      private void saveString ( ) {

            final String key = "poem";
            final String value = "曾经沧海难为水,除却巫山不是云";

            mStringFileLoader.save( key, value );

            setMsg( "save string: \n" + key + "\n" + value );
      }

      private void loadString ( ) {

            final String key = "poem";
            String load = mStringFileLoader.load( key );
            setMsg( "loadFromNet string: \n" + key + "\n" + load );
      }

      private void deleteString ( ) {

            final String key = "poem";
            String load = mStringFileLoader.remove( key );
            File file = mStringFileLoader.getFile( key );
            setMsg( "delete string: \n" + key + "\n" + load + "\n" + file.exists() );
      }

      private void saveJson ( ) {

            final String key = "json";

            GankCategoryBean bean = GsonClient.fromJson( JSON, GankCategoryBean.class );
            mJsonFileLoader.save( key, bean );

            setMsg( "save json: \n" + key + "\n" + bean.getResults().toString() );
      }

      private void loadJson ( ) {

            final String key = "json";
            GankCategoryBean bean = mJsonFileLoader.load( key );
            setMsg( "loadFromNet json: \n" + key + "\n" + bean.getResults().toString() );
      }

      private void deleteJson ( ) {

            final String key = "json";
            GankCategoryBean bean = mJsonFileLoader.remove( key );
            File file = mStringFileLoader.getFile( key );
            setMsg( "delete json: \n" + key + "\n" + bean + "\n" + file.exists() );
      }

      private void setMsg ( String msg ) {

            mMsg.setText( msg );
      }

      private void saveBitmap ( ) {

            final String key = "bitmap";
            Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.drawable.a117 );
            mBitmapFileLoader.save( key, bitmap );

            setMsg( "save bitmap: " + key + +bitmap.getWidth() );
      }

      private void loadBitmap ( ) {

            final String key = "bitmap";

            Bitmap bitmap = mBitmapFileLoader.load( key );

            mImageView.setImageBitmap( bitmap );

            setMsg( "loadFromNet bitmap: " + key + " " + bitmap.getWidth() );
      }

      private void deleteBitmap ( ) {

            final String key = "bitmap";
            Bitmap remove = mBitmapFileLoader.remove( key );
            File file = mBitmapFileLoader.getFile( key );
            setMsg( "delete bitmap: " + key + " " + file.exists() );
      }
}
