package database;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sk_meals database table.
 * 
 */
@Entity
@Table(name="sk_meals")
@NamedQuery(name="SkMealsInfo.findAll", query="SELECT s FROM SkMealsInfo s")
public class SkMealsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="materiel_ids")
	private String materielIds;

	private String name;

	private String remark;

	private int status;

	private int user;

	private float value;

	public SkMealsInfo() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getMaterielIds() {
		return this.materielIds;
	}

	public void setMaterielIds(String materielIds) {
		this.materielIds = materielIds;
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

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}