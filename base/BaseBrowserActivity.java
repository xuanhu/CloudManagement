package com.tg.cloudmanagement.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.view.MyWebView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 浏览器BaseActivity
 * @author Administrator
 *
 */
public abstract class BaseBrowserActivity extends Activity implements OnClickListener{
	public static final String KEY_HIDE_TITLE = "hide";
	public static final String KEY_TITLE = "title";
	public static final String KEY_HTML_TEXT = "text";
	public static final String KEY_URL = "url";
	protected MyWebView webView;
	private RelativeLayout rlHeadContent;
	private RelativeLayout rlRollback;
	protected String htmlText;
	private TextView tvTitle;
	private ImageView ivRefresh, ivClose;
	private String url;
	private final static int STOP = 0x10000;
	private final static int NEXT = 0x10001;
	private int count = 0;
	private ProgressBar bar;
	private String title;
	private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private ValueCallback<Uri> mUploadMessage;   
	public static final int REQUEST_CODE_LOLIPOP = 1;
	private final static int RESULT_CODE_ICE_CREAM = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		webView = (MyWebView) findViewById(R.id.webView);
		bar = (ProgressBar)findViewById(R.id.myProgressBar);
		rlHeadContent = (RelativeLayout) findViewById(R.id.head_content);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		rlRollback = (RelativeLayout) findViewById(R.id.rl_rollback);
		ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
		ivClose = (ImageView) findViewById(R.id.iv_close);
		rlRollback.setOnClickListener(this);
		ivRefresh.setOnClickListener(this);
		ivClose.setOnClickListener(this);
		Intent data = getIntent();
		if (data != null) {
			boolean hideTitle = data.getBooleanExtra(KEY_HIDE_TITLE, false);
			if (hideTitle) {
				rlHeadContent.setVisibility(View.GONE);
			} else {
				title = data.getStringExtra(KEY_TITLE);
				if (!TextUtils.isEmpty(title)) {
					tvTitle.setText(title);
				}
			}
			htmlText = data.getStringExtra(KEY_HTML_TEXT);
			url = data.getStringExtra(KEY_URL);
		}
		
		if(title == null){
			/**
			 * 设置标题
			 */
			webView.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					if (url.startsWith("mailto:") || 
							url.startsWith("geo:") ||url.startsWith("tel:")) { 
		                Intent intent = new Intent(Intent.ACTION_VIEW,
		                        Uri.parse(url)); 
		               startActivity(intent); 
		            }else{
		            	view.loadUrl(url);
		            }
					return true;
				}
				@Override
				public void onPageFinished(WebView view, String url) {
					tvTitle.setText(view.getTitle());
				}
			});
		}
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		if (!TextUtils.isEmpty(htmlText)) {
			webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8",
					null);
		} else if (!TextUtils.isEmpty(url)) {
			// 设置setWebChromeClient对象
			webView.setWebChromeClient(new XHSWebChromeClient());
			webView.loadUrl(url);
		}
	}
	 public class XHSWebChromeClient extends WebChromeClient {
			@Override
			public void onProgressChanged(WebView view, final int newProgress) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(newProgress == 0){
							if (newProgress < 100) {
								for (int i = 0; i < 100; i++) {
									try {
										count = i;
										Thread.sleep(20);
										Message msg = new Message();
										msg.what = NEXT;
										handler.sendMessage(msg);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}else if(newProgress == 100){
							count = newProgress;
							Message msg = new Message();
							msg.what = STOP;
							handler.sendMessage(msg);
						}
					}
				});
				t.start();
				super.onProgressChanged(view, newProgress);
			}
		 //The undocumented magic method override
         //Eclipse will swear at you if you try to put @Override here
         // For Android 3.0+
         public void openFileChooser(ValueCallback<Uri> uploadMsg) {
             mUploadMessage = uploadMsg;
             Intent i = new Intent(Intent.ACTION_GET_CONTENT);
             i.addCategory(Intent.CATEGORY_OPENABLE);
             i.setType("image/*");
             startActivityForResult(Intent.createChooser(i, "File Chooser"),
                     RESULT_CODE_ICE_CREAM);

         }

         // For Android 3.0+
         public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
             mUploadMessage = uploadMsg;
             Intent i = new Intent(Intent.ACTION_GET_CONTENT);
             i.addCategory(Intent.CATEGORY_OPENABLE);
             i.setType("*/*");
             startActivityForResult(Intent.createChooser(i, "File Browser"),
                     RESULT_CODE_ICE_CREAM);
         }

         //For Android 4.1
         public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
             mUploadMessage = uploadMsg;
             Intent i = new Intent(Intent.ACTION_GET_CONTENT);
             i.addCategory(Intent.CATEGORY_OPENABLE);
             i.setType("image/*");
             startActivityForResult(Intent.createChooser(i, "File Chooser"),
                     RESULT_CODE_ICE_CREAM);

         }
	@Override
	public boolean onShowFileChooser(WebView webView,ValueCallback<Uri[]> filePathCallback,FileChooserParams fileChooserParams) {
	     //这句话建议屏蔽掉，当多次打开上传时会导致崩溃  但这是 google 官方做法
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(BaseBrowserActivity.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, REQUEST_CODE_LOLIPOP);

        return true;
	}
}
	 /**
	  * 5.0+选择图片
	  * @return
	  * @throws IOException
	  */
	 private File createImageFile() throws IOException {
	        // Create an image file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String imageFileName = "JPEG_" + timeStamp + "_";
	        File storageDir = Environment.getExternalStoragePublicDirectory(
	                Environment.DIRECTORY_PICTURES);
	        File imageFile = File.createTempFile(
	                imageFileName,  /* prefix */
	                ".jpg",         /* suffix */
	                storageDir      /* directory */
	        );
	        return imageFile;
	    }
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		        super.onActivityResult(requestCode, resultCode, data);
		        if(resultCode != RESULT_OK){
		        	if(mUploadMessage != null){
		        		mUploadMessage.onReceiveValue(null);
		            	mUploadMessage = null;
		        	}else if(mFilePathCallback != null){
		        		mFilePathCallback.onReceiveValue(null);
		            	mFilePathCallback = null;
		        	}
					return;
				}
		        switch (requestCode) {
		            case RESULT_CODE_ICE_CREAM:
		                Uri uri = null;
		                if (data != null) {
		                    uri = data.getData();
		                }
		                mUploadMessage.onReceiveValue(uri);
		                mUploadMessage = null;
		                if (resultCode == 0) {
		                	// 取消
		                	mUploadMessage.onReceiveValue(null);
		                	mUploadMessage = null;
		                	}
		                break;
		            case REQUEST_CODE_LOLIPOP:
		                Uri[] results = null;
		                    if (data == null) {
		                        // If there is not data, then we may have taken a photo
		                        if (mCameraPhotoPath != null) {
		                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
		                        }
		                    } else {
		                        String dataString = data.getDataString();
		                        if (dataString != null) {
		                            results = new Uri[]{Uri.parse(dataString)};
		                        }
		                    }
		                mFilePathCallback.onReceiveValue(results);
		                mFilePathCallback = null;
		                if (resultCode == 0) {
		                	// 取消
		                	mFilePathCallback.onReceiveValue(null);
		                	mFilePathCallback = null;
		                	}
		                break;
		        }
		    }
	private Handler handler = new Handler() {
		@SuppressWarnings("static-access")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEXT:
				if (!Thread.currentThread().interrupted()) {
					if(count < 100){
						bar.setProgress(count);
					}
				}
				break;
			case STOP:
				bar.setVisibility(View.GONE);
				break;
			}
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_rollback:// 返回
			if (webView.canGoBack()) {
				if (webView.getUrl().equals(url)) {
					super.onBackPressed();
				} else {
					webView.goBack();
				}
			}else {
				finish();
			}
			break;
		case R.id.iv_refresh:// 刷新
			webView.reload();
			break;
		case R.id.iv_close:// 关闭
			finish();
			break;
		}
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	private void backPress() {
		if (webView.canGoBack()) {
			if (webView.getUrl().equals(url)) {
				super.onBackPressed();
			} else {
				webView.goBack();
			}
		}else {
			finish();
		}
	}
	/**
	 * 下载帮助类
	 */
	private class MyWebViewDownLoadListener implements DownloadListener {

		   @Override
		 public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,long contentLength) {
		      Uri uri = Uri.parse(url);
		      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		      startActivity(intent);
		   }

		}
}
