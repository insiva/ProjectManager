package com.xhj.pm.ui.view.popuptree;

import java.util.List;

import com.xhj.pm.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupTree{

	private Context mContext;
	private PopupTreeAdapter mAdapter;
	private PopupWindow mPopupWindow;
	private PopupTree mChildTree;
	private int mWidth;
	private View mContentView;
	private ListView lvNodes;
	private NodesAdapter mNodesAdapter;
	private ChildTreeAdapter mChildTreeAdapter;
	private View mParent;
	private PopupTree mParentTree;
	
	public PopupTree(Context context,View parent){
		this.mParent=parent;
		this.init(context);
	}
	
	public void setAdapter(PopupTreeAdapter adapter){
		this.mAdapter=adapter;
		this.initViews();
	}
	
	private void init(Context context){
		this.mContext=context;
	}
	
	private void initViews(){
		this.mWidth=this.mAdapter.getWindowWidth();
		this.initContentView();
		this.mPopupWindow=new PopupWindow(this.mContentView,this.mWidth,LayoutParams.WRAP_CONTENT);
		this.mPopupWindow.setOutsideTouchable(true);
		this.mPopupWindow.setFocusable(false);
	}
	
	private void initContentView(){
		this.mContentView=View.inflate(this.mContext, R.layout.layout_popup_tree, null);
		this.lvNodes=(ListView)this.mContentView.findViewById(R.id.lvNodes);
		this.mNodesAdapter=new NodesAdapter();
		this.lvNodes.setAdapter(this.mNodesAdapter);
	}
	
	public void show(View view){
		this.cancelSelectedNode();
		this.mPopupWindow.showAsDropDown(view);
	}
	
	public void dismiss(){
		this.mPopupWindow.dismiss();
		this.cancelSelectedNode();
		if(this.mChildTree!=null&&this.mChildTree.isShowing()){
			this.mChildTree.dismiss();
		}
		if(this.mParentTree!=null&&this.mParentTree.isShowing()){
			this.mParentTree.dismiss();
		}
	}
	
	public boolean isShowing(){
		return this.mPopupWindow.isShowing();
	}
	
	private void cancelSelectedNode(){
		List<Node> nodes=this.mAdapter.getNodeList();
		for (Node n : nodes) {
			n.setSelected(false);
		}
		int c=this.lvNodes.getChildCount();
		for(int i=0;i<c;i++){
			View view=this.lvNodes.getChildAt(i);
			if(view instanceof NodeView){
				((NodeView)view).onSelected();
			}
		}
	}
	
	private void openChildTree(Node node){
		PopupTree.this.cancelSelectedNode();
		node.setSelected(true);
		if(this.mChildTree==null){
			this.mChildTree=new PopupTree(this.mContext,this.mParent);
			this.mChildTree.mParentTree=this;
			this.mChildTreeAdapter=new ChildTreeAdapter();
			this.mChildTree.setAdapter(this.mChildTreeAdapter);
		}
		this.mChildTreeAdapter.setNodes(node.mChildren);
		this.mChildTree.lvNodes.setAdapter(this.mChildTree.mNodesAdapter);
		int[] location = new int[2];
		this.lvNodes.getLocationOnScreen(location);
		int x=location[0]+this.mPopupWindow.getWidth();
		int y=location[1];//(int)this.lvNodes.getY();
		this.mChildTree.mPopupWindow.showAtLocation(this.mParent, Gravity.NO_GRAVITY,x,y);
	}
	
	class NodesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return PopupTree.this.mAdapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return PopupTree.this.mAdapter.getNode(position);
		}

		@Override
		public long getItemId(int position) {
			return  position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NodeView nv=null;
			if(convertView==null){
				nv=new NodeView(mContext);
				convertView=nv;
			}else{
				nv=(NodeView)convertView;
			}
			nv.setNode((Node)this.getItem(position),position);
			return convertView;
		}
	}
	
	class NodeView extends LinearLayout implements OnClickListener{

		private Context mContext;
		private Node mNode;
		private TextView tvName;
		//private View vSelected;
		private TextView tvNext;
		private int mPosition;
		
		public NodeView(Context context) {
			super(context);
			this.mContext=context;
			View.inflate(context, R.layout.layout_popup_tree_node, this);
			this.tvName=(TextView)this.findViewById(R.id.tvName);
			this.tvNext=(TextView)this.findViewById(R.id.tvNext);
			//this.vSelected=(View)this.findViewById(R.id.vSelected);
			this.setClickable(true);
			this.setBackgroundResource(R.drawable.black_selector);
			this.setOrientation(LinearLayout.HORIZONTAL);
			this.setPadding(10, 25, 10, 25);
			this.setOnClickListener(this);
		}
		
		public void setNode(Node node,int position){
			this.mNode=node;
			this.mPosition=position;
			this.tvName.setText(node.mName);
			this.tvNext.setVisibility(node.hasChildren()?View.VISIBLE:View.GONE);
			this.onSelected();
		}

		@Override
		public void onClick(View v) {
			if(PopupTree.this.mAdapter.onSelected(this.mNode)){
				PopupTree.this.dismiss();
			}else if(this.mNode.hasChildren()){
				PopupTree.this.openChildTree(this.mNode);
				this.onSelected();
			}
		}
		
		public void onSelected(){
			if(this.mNode.mSelected){
				//this.vSelected.setBackgroundColor(Color.LTGRAY);
				//this.setBackgroundColor(Color.GRAY);
				this.setBackgroundResource(R.drawable.gray_bg);
				this.setPadding(10, 25, 10, 25);
			}else{
				//this.vSelected.setBackgroundColor(Color.TRANSPARENT);
				//this.setBackgroundColor(Color.TRANSPARENT);
				this.setBackgroundResource(R.drawable.black_selector);
				this.setPadding(10, 25, 10, 25);
			}
		}
	}
	
	class ChildTreeAdapter extends PopupTreeAdapter{
		private List<Node> mNodes;
		public void setNodes(List<Node> nodes){
			this.mNodes=nodes;
		}

		@Override
		public boolean onSelected(Node node, int... indexs) {
			return PopupTree.this.mAdapter.onSelected(node, indexs);
		}

		@Override
		public int getWindowWidth() {
			return PopupTree.this.mAdapter.getWindowWidth();
		}

		@Override
		public int getHeight() {
			return 0;
		}

		@Override
		public int getLevelNum() {
			return 0;
		}

		@Override
		public List<Node> getNodeList() {
			// TODO Auto-generated method stub
			return this.mNodes;
		}
		
	}
}
