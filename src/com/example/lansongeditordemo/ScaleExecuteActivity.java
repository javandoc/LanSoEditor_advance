package com.example.lansongeditordemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lansosdk.box.BitmapSprite;
import com.lansosdk.box.ScaleExecute;
import com.lansosdk.box.onMediaPoolCompletedListener;
import com.lansosdk.box.onMediaPoolProgressListener;
import com.lansosdk.box.onScaleCompletedListener;
import com.lansosdk.box.onScaleProgressListener;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.SDKDir;
import com.lansosdk.videoeditor.SDKFileUtils;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.utils.FileUtils;
import com.lansosdk.videoeditor.utils.snoCrashHandler;

public class ScaleExecuteActivity extends Activity{

	String videoPath=null;
	ProgressDialog  mProgressDialog;
	int videoDuration;
	boolean isRuned=false;
	MediaInfo   mMediaInfo;
	TextView tvProgressHint;
	
	private String editTmpPath=null;
	private String dstPath=null;
	 TextView tvHint;
	 
	private static final String TAG="MediaPoolExecuteActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new snoCrashHandler());
		 
		 videoPath=getIntent().getStringExtra("videopath");
		 mMediaInfo=new MediaInfo(videoPath);
		 mMediaInfo.prepare();
		 
		 
		 
		 setContentView(R.layout.video_edit_demo_layout);
		 tvHint=(TextView)findViewById(R.id.id_video_editor_hint);
		 
		 tvHint.setText(R.string.scaleexecute_demo_hint);
   
		 tvProgressHint=(TextView)findViewById(R.id.id_video_edit_progress_hint);
		 
       findViewById(R.id.id_video_edit_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mMediaInfo.vDuration>60*1000){//大于60秒
					showHintDialog();
				}else{
					testScaleEdit();
				}
			}
		});
       
       findViewById(R.id.id_video_edit_btn2).setEnabled(false);
       findViewById(R.id.id_video_edit_btn2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(FileUtils.fileExist(dstPath)){
					Intent intent=new Intent(ScaleExecuteActivity.this,VideoPlayerActivity.class);
	    	    	intent.putExtra("videopath", dstPath);
	    	    	startActivity(intent);
				}else{
					 Toast.makeText(ScaleExecuteActivity.this, "目标文件不存在", Toast.LENGTH_SHORT).show();
				}
			}
		});
       
       editTmpPath=SDKFileUtils.newMp4PathInBox();
       dstPath=SDKFileUtils.newMp4PathInBox();
       
       
      
	}
	
	
	VideoEditor mVideoEditer;
	BitmapSprite bitmapSprite=null;
	private void showHintDialog()
	{
		new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("视频过大,可能会需要一段时间,您确定要处理吗?")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				testScaleEdit();
			}
		})
		.setNegativeButton("取消", null)
        .show();
	}
	private boolean isExecuting=false;
	private void testScaleEdit()
	{
		if(isExecuting)
			return ;
		
		isExecuting=true;
		ScaleExecute  vScale=new ScaleExecute(ScaleExecuteActivity.this,videoPath);
		vScale.setOutputPath(editTmpPath);
		vScale.setScaleSize(mMediaInfo.vCodecWidth/2,mMediaInfo.vCodecHeight/2);
		
		vScale.setScaleProgessListener(new onScaleProgressListener() {
			
			@Override
			public void onProgress(ScaleExecute v, long currentTimeUS) {
				// TODO Auto-generated method stub
				tvProgressHint.setText(String.valueOf(currentTimeUS));
			}
		});
		vScale.setScaleCompletedListener(new onScaleCompletedListener() {
			
			@Override
			public void onCompleted(ScaleExecute v) {
				// TODO Auto-generated method stub
				tvProgressHint.setText("Completed!!!");
				
				isExecuting=false;
				
				if(FileUtils.fileExist(editTmpPath)){
					VideoEditor.encoderAddAudio(videoPath, editTmpPath,SDKDir.TMP_DIR, dstPath);
				}
				findViewById(R.id.id_video_edit_btn2).setEnabled(true);
			}
		});
		vScale.start();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 if(FileUtils.fileExist(dstPath)){
	    	   FileUtils.deleteFile(dstPath);
	       }
	       if(FileUtils.fileExist(editTmpPath)){
	    	   FileUtils.deleteFile(editTmpPath);
	       } 
	}
}	