package GameEngine;
import java.util.ArrayList;

import GameEngine.InPlayObj;
import VMQ.DLinkedList;


public class Row {

	private ArrayList<DLinkedList<InPlayObj>> rowData;
	private int length;
	
	public Row(int length) {
		rowData = new ArrayList<DLinkedList<InPlayObj>>(length);
		this.length = length;
		for (int i=0;i<length;i++) {
			rowData.add(new DLinkedList<InPlayObj>());
		}
	}
	
	public DLinkedList<InPlayObj> get(int index) {
		if (index<0) {
			throw new IndexOutOfBoundsException("Index cannot be negative");
		} else if (index>=length) {
			throw new IndexOutOfBoundsException("Index to large");
		} else {
			return rowData.get(index);
		}
	}
	
	public int length() {
		return length;
	}
	
	public int numberOfObjects() {
		int size = 0;
		for (DLinkedList<InPlayObj> currentList: rowData) {
			size += currentList.length();
		}
		return size;
	}
	
}



