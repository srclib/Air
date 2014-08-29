package srclib.huyanwei.menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Air extends ViewGroup {

	private final String TAG = "srclib.huyanwei.menu.Air";	
	private boolean DBG = true;
	
	private int first_air_view_index = -1;    // ��һ��downʱѡ�еġ�
	private int current_air_view_index = -1;  // ��ǰ�Ƿ���ѡ��ֻview ��-1 Ϊû��ѡ�е�
	private int last_air_view_index = -1;     // ��ǰ�Ƿ���ѡ��ֻview ��-1 Ϊû��ѡ�е�
	
	// ȷ�� �ؼ��Ŀռ��С
	private int width_mode 	= 0 ;
	private int height_mode = 0 ;			
	private int width_size  = 0 ;  // �ؼ��Ŀ��
	private int height_size = 0 ;  // �ؼ��ĸ߶�
	
	private int shaft_width_size = 0;  // �м䰴ť���
	private int shaft_height_size = 0; // �м䰴ť�߶�
	
	GestureDetector mGestureDetector;
	
	// Event listeners
	private OnItemClickListener 	mOnItemClickListener 	= null;
	private OnItemSelectedListener  mOnItemSelectedListener = null;
	private OnCenterClickListener   mOnCenterClickListener  = null;
	
	private boolean isTouchingButton = false;
	
	// Background image
	private Bitmap img_bg_origin, img_bg_scaled;
	private Bitmap img_shaft_origin, img_shaft_scaled;
	private Bitmap img_shadow_origin, img_shadow_scaled;
	private Bitmap img_focus_origin, img_focus_scaled;
	private Matrix matrix;
	
	private float img_bg_x_scale_factor = 0.0f;
	private float img_bg_y_scale_factor = 0.0f;
	
	// Child sizes
	private int mMaxChildWidth = 0;
	private int mMaxChildHeight = 0;
	
	private int childWidth = 0;
	private int childHeight = 0;
	
	private int radius = 0 ; 
	
	private final double AIR_SRC_ANGLE = 44d; // ����ͼƬ������.
	private final double AIR_ARC_START = 360d-38d;
	private final double airview_layout_start_angle = AIR_ARC_START+(AIR_SRC_ANGLE/2) ;	//air view layout start angle :341.
	private double airview_layout_end_angle   = 180.0d + 2.0d ;	
	private float  rotate_angle = -5.0f/2.0f;	
	
	private double first_pointer_Angle = 0.0d ;		
	private double current_pointer_Angle = 0.0d ;
	private double last_pointer_Angle = 0.0d ;
	private boolean bSelectedItemChanged = false;
	
	
	private TextView mTextView ;
	
	public Air(Context context) {
		this(context, null);	
		// TODO Auto-generated constructor stub
	}	

	public Air(Context context ,AttributeSet attrs) {
		this(context, attrs, 0);	
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public Air(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);			
		init(attrs);		
	}

	/**
	 * Initializes the ViewGroup and modifies it's default behavior by the passed attributes
	 * @param attrs	the attributes used to modify default settings
	 */
	protected void init(AttributeSet attrs) {
		if (attrs != null) {
			
			TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.Air);
			
			int picId = a.getResourceId(R.styleable.Air_bg, -1);				
			// If a background image was set as an attribute,
			// retrieve the image
			if (picId != -1)
			{
				img_bg_origin = BitmapFactory.decodeResource(getResources(), picId);
			}
			
			picId = a.getResourceId(R.styleable.Air_shaft, -1);				
			// If a background image was set as an attribute,
			// retrieve the image
			if (picId != -1)
			{
				img_shaft_origin = BitmapFactory.decodeResource(getResources(), picId);
			}
			
			picId = a.getResourceId(R.styleable.Air_shaft_shadow, -1);				
			// If a background image was set as an attribute,
			// retrieve the image
			if (picId != -1)
			{
				img_shadow_origin = BitmapFactory.decodeResource(getResources(), picId);
			}
			
			picId = a.getResourceId(R.styleable.Air_focus, -1);				
			// If a background image was set as an attribute,
			// retrieve the image
			if (picId != -1)
			{
				img_focus_origin = BitmapFactory.decodeResource(getResources(), picId);
			}			
			a.recycle();

			// initialize the matrix only once
			if (matrix == null) {
				matrix = new Matrix();
			} else {
				// not needed, you can also post the matrix immediately to
				// restore the old state
				matrix.reset();
			}
			// Needed for the ViewGroup to be drawn
			setWillNotDraw(false);
		}
	}
	
	/** 
     * ��onDraw��ִ�� 
     *  
     * һ��MeasureSpec��װ�˸����ִ��ݸ��Ӳ��ֵĲ���Ҫ��ÿ��MeasureSpec������һ���Ⱥ͸߶ȵ�Ҫ�� 
     * һ��MeasureSpec�ɴ�С��ģʽ��� 
     * ��������ģʽ��UNSPECIFIED(δָ��),��Ԫ�ز�����Ԫ��ʩ���κ���������Ԫ�ؿ��Եõ�������Ҫ�Ĵ�С; 
     *              EXACTLY(��ȫ)����Ԫ�ؾ�����Ԫ�ص�ȷ�д�С����Ԫ�ؽ����޶��ڸ����ı߽���������������С�� 
     *              AT_MOST(����)����Ԫ������ﵽָ����С��ֵ�� 
     *  
              * ���������õ����������� ���� 
     * 1.static int getMode(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡģʽ(��������ģʽ֮һ) 
     * 2.static int getSize(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡ��Сֵ(�����СҲ��������ͨ����˵�Ĵ�С)  
     * 3.static int makeMeasureSpec(int size,int mode):�����ṩ�Ĵ�Сֵ��ģʽ����һ������ֵ(��ʽ) 
     */  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		/*
		 * ������ �����ڵĲ��� ϣ����ǰ�Ӵ��ڵĲ��� ��Ϣ
		 * һ��MeasureSpec��װ�˸����ִ��ݸ��Ӳ��ֵĲ���Ҫ��ÿ��MeasureSpec������һ���Ⱥ͸߶ȵ�Ҫ��
		 * 
		 * �����õ�����������
		 * 1.static int getMode(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡģʽ(��������ģʽ֮һ)
		 * 2.static int getSize(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡ��Сֵ(�����СҲ��������ͨ����˵�Ĵ�С)
		 * 3.static int makeMeasureSpec(int size,int mode):�����ṩ�Ĵ�Сֵ��ģʽ����һ������ֵ(��ʽ)�����ֽ�+���ֽڣ�
		 * 
		 * MeasureSpec.EXACTLY�Ǿ�ȷ�ߴ磺���ؼ���layout_width��layout_heightָ��Ϊ������ֵʱ��andorid:layout_width="50dip��FILL_PARENT ������ȷ�ġ�
		 * MeasureSpec.AT_MOST �����ߴ磺���ؼ���layout_width��layout_heightָ��ΪWRAP_CONTENTʱ��Ҫ���ӿؼ��Ŀռ��С��ֻҪ���������ؼ���������ߴ缴��
		 * MeasureSpec.UNSPECIFIED��δָ���ߴ硣
		 */
		
		int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
		int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
		int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		if(DBG)
		{
			Log.d(TAG,"onMeasure() : width="+specWidthSize+",height="+specHeightSize);
		}
		
		// width 
		if( specWidthMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����.   // fill
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.AT_MOST");
			}
			width_size = specWidthSize; 
		} 
		else if(specWidthMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. // wrap
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.EXACTLY");
			}
			width_size = specWidthSize; 
		}
		else if(specWidthMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.UNSPECIFIED");
			}
			width_size = 392;  // �ؼ���С�ǿؼ������2������
		}
		
		// height
		if( specHeightMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����.
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.AT_MOST");
			}
			height_size = specHeightSize; 
		} 
		else if(specHeightMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. 
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.EXACTLY");			
			}
			height_size = specHeightSize; 
		}
		else if(specHeightMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.UNSPECIFIED");
			}
			height_size   = width_size ;     // �ͱ���һ����С��
		}		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public int getAirViewCount()
	{
		final int childCount = getChildCount();
		int m_airview_count = 0 ; 
		
		for (int i = 0; i < childCount; i++)
		{
			final View view = getChildAt(i);	
			
			if (view.getVisibility() == GONE) 
			{
				continue;
			}
			
			if(view instanceof AirView)
			{
				m_airview_count ++;
			}
			
		}		
		return m_airview_count;
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int layoutWidth = r - l;
		int layoutHeight = b - t;
		
		float angleDelay = (float) AIR_SRC_ANGLE;
		
		// Laying out the child views
		final int  childCount = getChildCount();
		final  int airviewCount = getAirViewCount();
		
		int left, top;
		
		int index_visual_air_view = 0 ;

		radius = (layoutWidth <= layoutHeight)?(int)(layoutWidth/3.5) :(int)(layoutHeight/3.5);
		
		childWidth  = (int) (radius / 2.5);
		childHeight = (int) (radius / 2.5);
		
		if( airviewCount != 0)
		{
			angleDelay = (float) ((AIR_SRC_ANGLE * 5 ) / airviewCount);
		}
		
		Log.d(TAG,"onLayout() airview_layout_start_angle="+airview_layout_start_angle);

		double airview_angle = airview_layout_start_angle;
		
		for (int i = 0; i < childCount; i++) 
		{
			final View view = getChildAt(i);			

			if (view.getVisibility() == GONE) 
			{
				continue;
			}

			if( view instanceof TextView)
			{
				final TextView child = (TextView) view;	
				
				Log.d(TAG,"child.getWidth()="+child.getWidth());
				Log.d(TAG,"child.getHeight()="+child.getHeight());
				
				left = layoutWidth/2 - 120/2 ;
				top  = layoutHeight/2 -40/2;				
				child.layout(left, top, left + 120, top + 40);
				child.setGravity(Gravity.CENTER);
				
				mTextView = child ;
			}
			else if((view instanceof AirView))
			{				
				final AirView child = (AirView) view;

				while (airview_angle > 360d) 
				{
					airview_angle -= 360d;
				}
				
				while (airview_angle < 0d)
				{
					airview_angle += 360d;
				}

				left = Math.round((float) (((layoutWidth / 2) - childWidth / 2) + radius
								* Math.cos(Math.toRadians(airview_angle))));
				
				top = Math.round((float) (((layoutHeight / 2) - childHeight / 2) - radius
								* Math.sin(Math.toRadians(airview_angle))));

				child.layout(left, top, left + childWidth, top + childHeight);
				
				child.setData(index_visual_air_view);
				child.setOrder(index_visual_air_view);		
				index_visual_air_view++;
				
				airview_angle += angleDelay;		
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		// the sizes of the ViewGroup
		int Privot_x = this.getLeft();
		int Privot_y = this.getTop();
		int Width  = getWidth();
		int Height = getHeight();
		
		Log.d(TAG,"onDraw rotate_angle="+rotate_angle);
		
		while(rotate_angle > 360)
		{
			rotate_angle -= 360;
		}
		
		while(rotate_angle < 0)
		{
			rotate_angle += 360;
		}
		
		if (img_bg_origin != null)
		{
			// Scaling the size of the background image
			if (img_bg_scaled == null) 
			{
				matrix = new Matrix();
				float sx = img_bg_x_scale_factor= 1.0f; //((width_size) / (float) img_bg_origin.getWidth());
				float sy = img_bg_y_scale_factor= 1.0f; //(( height_size) / (float) img_bg_origin.getHeight());
				matrix.postScale(sx, sy);
				img_bg_scaled = Bitmap.createBitmap(img_bg_origin, 0, 0,
						img_bg_origin.getWidth(), img_bg_origin.getHeight(),
						matrix, false);
			}

			if (img_bg_scaled != null) {
				// Move the background to the center
				int cx = (Width - img_bg_scaled.getWidth()) / 2;
				int cy = (Height - img_bg_scaled.getHeight()) / 2;

				Canvas g = canvas;
				canvas.save();
				//canvas.rotate(0, Width / 2, Height / 2);				
				g.drawBitmap(img_bg_scaled, cx, cy, null);
				canvas.restore();
			}			
		}
		
		if(current_air_view_index!= -1) // ��ѡ�е�״̬��
		{	
			if (img_focus_origin != null)
			{	
				if (img_focus_scaled == null) 	
				{
					matrix = new Matrix();
					float sx = img_bg_x_scale_factor;
					float sy = img_bg_y_scale_factor;
					matrix.postScale(sx, sy);
					img_focus_scaled = Bitmap.createBitmap(img_focus_origin, 0, 0,
							img_focus_origin.getWidth(), img_focus_origin.getHeight(),
							matrix, false);
				}
	
				if (img_focus_scaled != null) 
				{
					// Move the background to the center
					int cx = (Width ) / 2 ;
					int cy = (Height) / 2 ;
		
					Canvas g = canvas;
	
					Matrix mMatrix = new Matrix();
					
			        //3 �Ƶ����ĵ�
					mMatrix.preTranslate(cx,cy);
					
			        //2 ��ʼת
					mMatrix.preRotate(rotate_angle);
	
					//1 ����ת��λ��
					mMatrix.preTranslate(-img_focus_scaled.getWidth(),-img_focus_scaled.getHeight()+6); // ��ת����������6���ؿհס�
					
			        //��λ���͵�view������ . ����ͬ3
					//mMatrix.postTranslate(cx,cy);
					
					canvas.save();				
					canvas.drawBitmap(img_focus_scaled, mMatrix, null);								
					canvas.restore();
				}
			}
		}
		
		// shaft img
		if (img_shaft_origin != null)
		{	
			if (img_shaft_scaled == null) 	
			{
				matrix = new Matrix();
				float sx = (((radius - childWidth / 2 -30) * 2) / (float) img_shaft_origin
						.getWidth());
				float sy = (((radius - childWidth / 2 -30) * 2) / (float) img_shaft_origin
						.getHeight());
				matrix.postScale(sx, sy);
				img_shaft_scaled = Bitmap.createBitmap(img_shaft_origin, 0, 0,
						img_shaft_origin.getWidth(), img_shaft_origin.getHeight(),
						matrix, false);
			}

			if (img_shaft_scaled != null) {
				// Move the background to the center
				
				shaft_width_size = img_shaft_scaled.getWidth();
				shaft_height_size = img_shaft_scaled.getHeight();
				
				int cx = (Width  - shaft_width_size) / 2;
				int cy = (Height - shaft_height_size) / 2;
	
				Canvas g = canvas;
				canvas.save();
				canvas.rotate(0, Width / 2, Height / 2);
				g.drawBitmap(img_shaft_scaled, cx, cy, null);
				canvas.restore();				
			}
		}
		
		if (img_shadow_origin != null)
		{	
			if (img_shadow_scaled == null) 	
			{
				matrix = new Matrix();
				float sx = (((radius - childWidth / 2 -30) * 2) / (float) img_shadow_origin
						.getWidth());
				float sy = (((radius - childWidth / 2 -30) * 2) / (float) img_shadow_origin
						.getHeight());
				matrix.postScale(sx, sy);
				img_shadow_scaled = Bitmap.createBitmap(img_shadow_origin, 0, 0,
						img_shadow_origin.getWidth(), img_shadow_origin.getHeight(),
						matrix, false);
			}

			if (img_shadow_scaled != null) {
				// Move the background to the center
				int cx = (Width - img_shadow_scaled.getWidth()) / 2;
				int cy = (Height - img_shadow_scaled.getHeight()) / 2;
	
				Canvas g = canvas;
				canvas.save();
				canvas.rotate(0, Width / 2, Height / 2);
				g.drawBitmap(img_shadow_scaled, cx, cy, null);
				canvas.restore();				
			}
		}
		//super.onDraw(canvas);
	}

	
	/**
	 * @return The selected quadrant.
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}
	
	/**
	 * @return The angle of the unit circle with the image view's center
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (width_size / 2d);
		double y = (width_size/2d) - yTouch;

		switch (getQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 2:
			return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);			
		case 3:
			return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
		case 4:
			return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		default:
			// ignore, does not happen
			return 0;
		}
	}
	
	
	int getAirViewIndexByAngle(double angle)
	{
		int index = 0 ;
		int count = this.getAirViewCount();
		
		while(angle > 360d)
		{
			angle -= 360d;
		}
		
		while(angle < 0 )
		{
			angle += 360d;
		}
		
		for(index = 0; index < count; index++)
		{
			double start_angle = (AIR_ARC_START+AIR_SRC_ANGLE*index) % 360;
			double end_angle   = (start_angle + AIR_SRC_ANGLE)%360;
			
			Log.d(TAG,"getAirViewIndexByAngle(" +angle+")"+",start_angle="+start_angle);
			
			if(start_angle > 270d)
			{
				if(((start_angle <= angle)&&(angle < 360)) || ((0<=angle) && (angle <= end_angle)))
				{
					return index ;
				}
			}
			else
			{
				if((start_angle<angle)&&(angle < end_angle))
				{
					return index ;
				}
			}
		}
		return -1; // ���ڽ���
	}
	
	
	boolean isPointerInSpecialArea(double x,double y)
	{
		if((Math.abs(x-(width_size/2))<shaft_width_size/2) && (Math.abs(y-(height_size/2))<shaft_height_size/2))
				return true;
		else
			return false;
	}
	
	public View getAirViewByCode(int data)
	{		
		final int  childCount = getChildCount();
		final  int airviewCount = getAirViewCount();		
		
		for (int i = 0; i < childCount; i++) 
		{
			final View view = getChildAt(i);			

			if (view.getVisibility() == GONE)
				continue;
			
			if(view instanceof AirView)
			{
				if(((AirView) view).getData() == data)
				{
					return view;
				}
			}
		}
		return null;
	}
	
	public View getAirViewByOrder(int index)
	{		
		final int  childCount = getChildCount();
		final  int airviewCount = getAirViewCount();		
		
		for (int i = 0; i < childCount; i++) 
		{
			View view = getChildAt(i);			

			if (view.getVisibility() == GONE)
				continue;
			
			if(view instanceof AirView)
			{
				AirView tmp = (AirView) view;
				int order = tmp.getOrder() ; 
				if( order == index)
				{
					return view;
				}
			}
		}
		return null;
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub	
		int select_item_index = 0 ;
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:				
				bSelectedItemChanged = false;				
				isTouchingButton = isPointerInSpecialArea((double)event.getX(),(double)event.getY());
				if(isTouchingButton)
				{
					first_air_view_index = last_air_view_index = current_air_view_index = -1;
				}
				else
				{	
					first_pointer_Angle = current_pointer_Angle = getAngle(event.getX(), event.getY());
					Log.d(TAG,"Down Angle=" +first_pointer_Angle);
					if(current_air_view_index != -1)
					{
						last_air_view_index = current_air_view_index;
					}
					first_air_view_index = current_air_view_index = select_item_index = getAirViewIndexByAngle(first_pointer_Angle);					
					Log.d(TAG,"Down current_air_view_index=" + current_air_view_index);
					rotate_angle = (float) -(((current_air_view_index*AIR_SRC_ANGLE)%360)+ 189.0f);		// 189 = 180+ (50-44)/2 + (44-38)

					// first changed.
					AirView item = (AirView) getAirViewByOrder(current_air_view_index);
					if (mOnItemSelectedListener != null)						
					{					
						mOnItemSelectedListener.onItemSelected(item);					
					}						
					//inner process string
					if(mTextView != null)
					{
						String tip_string = this.getResources().getString(R.string.air_cmd);
						mTextView.setText((item==null)?tip_string:item.getName());
					}
				}
				postInvalidate();
				break;
			case MotionEvent.ACTION_MOVE:				
				isTouchingButton = isPointerInSpecialArea((double)event.getX(),(double)event.getY()); // update state
				
				current_pointer_Angle = getAngle(event.getX(), event.getY());
				Log.d(TAG,"Move Angle=" +first_pointer_Angle);
				select_item_index = getAirViewIndexByAngle(current_pointer_Angle);
				if(select_item_index == current_air_view_index)
				{
					return true;
				}
				else
				{
					last_air_view_index = current_air_view_index;					
					current_air_view_index = select_item_index;					
					bSelectedItemChanged = true;
				}	
				//Log.d(TAG,"Move current_air_view_index=" + current_air_view_index);
				rotate_angle = (float) -(((current_air_view_index*AIR_SRC_ANGLE)%360)+189.0f);

				AirView item = (AirView) getAirViewByOrder(current_air_view_index);
				
				if (mOnItemSelectedListener != null)
				{					
					mOnItemSelectedListener.onItemSelected(item);					
				}
				
				//inner process string
				if(mTextView != null)
				{	
					String tip_string = this.getResources().getString(R.string.air_cmd);
					mTextView.setText((item==null)?tip_string:item.getName());
				}				
				postInvalidate();
				break;
			case MotionEvent.ACTION_UP:
				if( isTouchingButton && isPointerInSpecialArea((double)event.getX(),(double)event.getY()))
				{
					if (mOnCenterClickListener != null) 
					{
						mOnCenterClickListener.onCenterClick();
						return true;
					}
				}
				else
				{
					current_pointer_Angle = getAngle(event.getX(), event.getY());
					Log.d(TAG,"up Angle=" +first_pointer_Angle);
					select_item_index = getAirViewIndexByAngle(current_pointer_Angle);
					if((first_air_view_index !=-1) 
					&& (select_item_index != -1) 
					&& (first_air_view_index == select_item_index)
					&& (!bSelectedItemChanged))
					{
						// Item Click
						if(mOnItemClickListener != null)
						{
							mOnItemClickListener.onItemClick(getAirViewByOrder(current_air_view_index));
						}
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:			
				break;
		}
		
		return true;
		
		//return super.onTouchEvent(event);
	}

	//Item Click
	public interface OnItemClickListener 
	{
		void onItemClick(View view);
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener)
	{
		this.mOnItemClickListener = onItemClickListener;
	}
	
	// Item Selected
	public interface OnItemSelectedListener 
	{
		void onItemSelected(View view);
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener)
	{
		this.mOnItemSelectedListener = onItemSelectedListener;
	}

	// Center Click
	public interface OnCenterClickListener 
	{
		void onCenterClick();
	}

	public void setOnCenterClickListener(OnCenterClickListener onCenterClickListener) 
	{
		this.mOnCenterClickListener = onCenterClickListener;
	}
}
