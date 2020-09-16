package de.salt.sce.model.edifact;

import java.io.Serializable;

public class Gid implements Serializable {

	private String goodsItemNumber;

	public String getGoodsItemNumber() {
		return goodsItemNumber;
	}

	public void setGoodsItemNumber(String goodsItemNumber) {
		this.goodsItemNumber = goodsItemNumber;
	}

}
