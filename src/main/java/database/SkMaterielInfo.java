package database;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the `sk_ materiel` database table.
 * 
 */
@Entity
@Table(name="`sk_ materiel`")
@NamedQuery(name="SkMaterielInfo.findAll", query="SELECT s FROM SkMaterielInfo s")
public class SkMaterielInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private int count;

	private String name;

	private String remark;

	private int type;

	private int unit;

	private int user;

	public SkMaterielInfo() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUnit() {
		return this.unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

}