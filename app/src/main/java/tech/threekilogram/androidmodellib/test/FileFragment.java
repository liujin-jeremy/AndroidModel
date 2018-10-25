package tech.threekilogram.androidmodellib.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import tech.threekilogram.androidmodellib.R;
import tech.threekilogram.androidmodellib.bean.GankCategoryBean;
import tech.threekilogram.androidmodellib.util.FileManager;
import tech.threekilogram.model.converter.GsonConverter;
import tech.threekilogram.model.converter.StringConverter;
import tech.threekilogram.model.file.BaseFileLoader;
import tech.threekilogram.model.file.loader.FileLoader;
import tech.threekilogram.model.util.instance.GsonClient;

/**
 * @author Liujin 2018-10-25:16:25
 */
public class FileFragment extends Fragment implements OnClickListener {

      private BaseFileLoader<String>           mStringFileLoader;
      private BaseFileLoader<GankCategoryBean> mJsonFileLoader;

      private String                          mData = "{\n"
          + "    \"error\": false,\n"
          + "    \"results\": [\n"
          + "        {\n"
          + "            \"_id\": \"5bcd71979d21220315c663fc\",\n"
          + "            \"createdAt\": \"2018-10-22T06:43:35.440Z\",\n"
          + "            \"desc\": \"2018-10-22\",\n"
          + "            \"publishedAt\": \"2018-10-22T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ws1.sinaimg.cn/large/0065oQSqgy1fwgzx8n1syj30sg15h7ew.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5bc434ac9d212279160c4c9e\",\n"
          + "            \"createdAt\": \"2018-10-15T06:33:16.497Z\",\n"
          + "            \"desc\": \"2018-10-15\",\n"
          + "            \"publishedAt\": \"2018-10-15T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ws1.sinaimg.cn/large/0065oQSqly1fw8wzdua6rj30sg0yc7gp.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";
      private Button                          mSaveString;
      private Button                          mGetString;
      private Button                          mGetStringFile;
      private Button                          mSaveJson;
      private Button                          mGetJson;
      private Button                          mGetJsonFile;
      private TextView                        mTextView2;
      private StringConverter                 mStringConverter;
      private GsonConverter<GankCategoryBean> mGsonConverter;

      public static FileFragment newInstance ( ) {

            Bundle args = new Bundle();

            FileFragment fragment = new FileFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_file, container, false );
      }

      private void initView ( @NonNull final View itemView ) {

            mSaveString = itemView.findViewById( R.id.saveString );
            mSaveString.setOnClickListener( this );
            mGetString = itemView.findViewById( R.id.getString );
            mGetString.setOnClickListener( this );
            mGetStringFile = itemView.findViewById( R.id.getStringFile );
            mGetStringFile.setOnClickListener( this );
            mSaveJson = itemView.findViewById( R.id.saveJson );
            mSaveJson.setOnClickListener( this );
            mGetJson = itemView.findViewById( R.id.getJson );
            mGetJson.setOnClickListener( this );
            mGetJsonFile = itemView.findViewById( R.id.getJsonFile );
            mGetJsonFile.setOnClickListener( this );
            mTextView2 = itemView.findViewById( R.id.textView2 );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            File dir = FileManager.getDir( "fileLoader" );

            mStringConverter = new StringConverter();
            mStringFileLoader = new FileLoader<>(
                dir, mStringConverter
            );

            mGsonConverter = new GsonConverter<>( GankCategoryBean.class );
            mJsonFileLoader = new FileLoader<>(
                dir, mGsonConverter
            );
      }

      @Override
      public void onClick ( View v ) {

            final String stringKey = "test";
            final String jsonKey = "json";

            switch( v.getId() ) {
                  case R.id.saveString:
                        mStringFileLoader.save( stringKey, mData );
                        setText( "保存成功 : " + mStringFileLoader.getFile( stringKey ) );
                        break;
                  case R.id.getString:
                        String load = mStringFileLoader.load( stringKey );
                        setText( "获取: " + load );
                        break;
                  case R.id.getStringFile:
                        File file = mStringFileLoader.getFile( stringKey );
                        setText( "文件: " + file + " " + file.exists() );
                        break;
                  case R.id.saveJson:
                        GankCategoryBean categoryBean = GsonClient
                            .fromJson( mData, GankCategoryBean.class );
                        mJsonFileLoader.save( jsonKey, categoryBean );
                        setText( "保存成功 : " + mJsonFileLoader.getFile( stringKey ) );
                        break;
                  case R.id.getJson:
                        GankCategoryBean bean = mJsonFileLoader.load( jsonKey );
                        setText( "获取: " + bean );
                        break;
                  case R.id.getJsonFile:
                        File jsonFile = mJsonFileLoader.getFile( jsonKey );
                        setText( "文件: " + jsonFile + " " + jsonFile.exists() );
                        break;
                  default:
                        break;
            }
      }

      private void setText ( String text ) {

            mTextView2.setText( text );
      }
}
