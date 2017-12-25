package com.kendy.other;

/**
 * 对导入的Excel数据结果进行封装
 * 
 * @author 林泽涛
 * @time 2017年11月5日 下午1:21:52
 */
public class Wrap {

	public boolean resultSuccess = false;
	public Object obj ;
	
	public Wrap() {
		super();
	}

	public Wrap(boolean result, Object obj) {
		super();
		this.resultSuccess = result;
		this.obj = obj;
	}

	public boolean isResultSuccess() {
		return resultSuccess;
	}

	public void setResult(boolean result) {
		this.resultSuccess = result;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "Wrap [result=" + resultSuccess + ", obj=" + obj + "]";
	}
	
	
	
	
}
