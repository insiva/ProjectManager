package com.xhj.pm.ui.view.popuptree;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public String mName;
	public String mValue;
	public List<Node> mChildren;
	public boolean mSelected;
	
	public Node(){
		this.mSelected=false;
	}
	
	public void setSelected(boolean selected){
		this.mSelected=selected;
	}
	
	public void addChild(Node child){
		if(this.mChildren==null){
			this.mChildren=new ArrayList<Node>();
		}
		this.mChildren.add(child);
	}
	
	public boolean hasChildren(){
		return this.getChildrenCount()>0;
	}
	
	public int getChildrenCount(){
		return this.mChildren==null?0:this.mChildren.size();
	}
}
