package com.xhj.pm.ui.view.popuptree;

import java.util.List;

public abstract class PopupTreeAdapter implements OnSelectedListener {
	//public List<Node> mNodes;
	//protected OnSelectedListener mOnSelectedListener;
	
	public PopupTreeAdapter(){
		//this.mNodes=new ArrayList<Node>();
	}
	
	public abstract List<Node> getNodeList();
	public abstract int getWindowWidth();
	public abstract int getHeight();
	public abstract int getLevelNum();

	public Node getNode(int... indexs){
		Node node=this.getNodeList().get(indexs[0]);
		int l=indexs.length;
		for(int i=1;i<l;i++){
			node=node.mChildren.get(indexs[i]);
		}
		return node;
	}
	
	public int getCount(int... indexs){
		if(indexs==null||indexs.length<=0){
			return this.getNodeList()==null?0:this.getNodeList().size();
		}
		return this.getNode(indexs).getChildrenCount();
	}
	
	
}
