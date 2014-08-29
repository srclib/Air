package srclib.huyanwei.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AirView extends ImageView {
	private String 	name;	
	private int 	data;
	private int 	order_by_parent;
	
	public AirView(Context context) 
	{
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public AirView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	
	public AirView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		if (attrs != null) 
		{
			int	name_id;
			TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.AirView);			
			name_id = a.getResourceId(R.styleable.AirView_name,-1);	
			name = context.getResources().getString(name_id);
			a.recycle();			
		}
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * get Name  
	 * */

	public String getName(){
		return name;
	}

	/*
	 * Set Name
	 * */
	public void setName(String name){
		this.name = name;
	}
	
	/*
	 * get Data  
	 * */
	public int getData(){
		return data;
	}

	/*
	 * set Data  
	 * */
	public void setData(int data)
	{
		this.data = data;
	}
	
	/*
	 * Get order No in Parent pos
	 * */
	public int getOrder(){
		return order_by_parent;
	}
	
	/*
	 * set order No in Parent pos
	 * */
	public void setOrder(int order)
	{
		this.order_by_parent = order;
	}
}
