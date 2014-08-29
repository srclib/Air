package srclib.huyanwei.menu;

import srclib.huyanwei.menu.Air.OnCenterClickListener;
import srclib.huyanwei.menu.Air.OnItemClickListener;
import srclib.huyanwei.menu.Air.OnItemSelectedListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity implements OnItemClickListener,OnItemSelectedListener,OnCenterClickListener
{

	private final String TAG = "srclib.huyanwei.menu.MainActivity";
	
	private Context mContext = null;
	private RelativeLayout mRelativeLayout ;
	private Air mAir;
	private AirView mAirView;
	
	AnimationListener mAnimationListener = new AnimationListener() 
	{
	      @Override
	      public void onAnimationStart(Animation animation)
	      {
	                   // TODO Auto-generatedmethod stub
	      }
	      
	      @Override
	      public void onAnimationRepeat(Animation animation)
	      {
	                   // TODO Auto-generatedmethod stub
	      }
	      
	      @Override
	      public void onAnimationEnd(Animation animation)
	      {
	                   
	                   
	      }	      
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this ;
		
		mRelativeLayout = (RelativeLayout) this.findViewById(R.id.container);
		if(mRelativeLayout != null)
		{
			mAir = (Air)mRelativeLayout.findViewById(R.id.framework);
		}
		if(mAir!=null)
		{
			mAir.setOnItemClickListener(this);
			mAir.setOnItemSelectedListener(this);
			mAir.setOnCenterClickListener(this);			
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		RotateAnimation mRotateAnimation =new RotateAnimation(0, 2*360, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f); 
		mRotateAnimation.setDuration(1000);
		mRotateAnimation.setFillBefore(false); 
		mRotateAnimation.setFillAfter(true);
		mRotateAnimation.setInterpolator(new DecelerateInterpolator());//设置加速度:慢慢减速		
		mRotateAnimation.setAnimationListener(mAnimationListener);		
		mAir.startAnimation(mRotateAnimation);
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onItemSelected(View view) {
		// TODO Auto-generated method stub		
		if((view instanceof AirView))
		{
			String name = ((AirView)(view)).getName();
			
			Log.d(TAG,"onItemSelected() name="+name);
			
			Toast mToast = Toast.makeText(mContext, name,Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	@Override
	public void onItemClick(View view) {
		// TODO Auto-generated method stub	
		if(view != null)
		{
			switch(view.getId())
			{
				case  R.id.airbutton_global_icon_quickmemo:
					handleTouchPanelEventForMusic();
					break;
				case  R.id.airbutton_global_icon_pinmode:
					handleTouchPanelEventForCamera();
					break;
				case  R.id.airbutton_global_icon_multiwindow:
					handleTouchPanelEventForDialor();
					break;
				case  R.id.airbutton_global_icon_galaxyfinder:
					handleTouchPanelEventForSettings();
					break;
				case  R.id.flashannotation:
					handleTouchPanelEventForCalendar();
					break;
				default:
					break;
			}
		}		
	}

	@Override
	public void onCenterClick() {
		// TODO Auto-generated method stub			
		
		Log.d(TAG,"onItemSelected() onCenterClick()");
		
		Toast mToast = Toast.makeText(mContext, "你点击了最中间的按钮",Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	
    private void handleTouchPanelEventForMusic()
    {
        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
    
    private void handleTouchPanelEventForCamera()
    {
        Intent intent = new Intent();
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
    
    private void handleTouchPanelEventForDialor()
    {
    	
        //Intent i = new Intent(Intent.ACTION_MAIN);
        //i.setComponent(new ComponentName("com.android.contacts","com.android.contacts.activities.CallEntryActivity"));    	
    	Intent i = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:112"));    	
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            mContext.startActivity(i);
        }
        catch (android.content.ActivityNotFoundException e)
        {
            Log.d(TAG,"ActivityNotFound");
        }        
    }

    private void handleTouchPanelEventForSettings()
    {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            mContext.startActivity(i);
        }
        catch (android.content.ActivityNotFoundException e)
        {
            Log.d(TAG,"ActivityNotFound");
        }
    }

    private void handleTouchPanelEventForCalendar()
    {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName("com.android.calendar","com.android.calendar.AllInOneActivity"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            mContext.startActivity(i);
        }
        catch (android.content.ActivityNotFoundException e)
        {
            Log.d(TAG,"ActivityNotFound");
        }
    }

    
}
