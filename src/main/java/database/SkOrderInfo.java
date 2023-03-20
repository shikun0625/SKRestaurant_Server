package database;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sk_order database table.
 * 
 */
@Entity
@Table(name="sk_order")
@NamedQuery(name = "SkOrderInfo.findAll", query = "SELECT s FROM SkOrderInfo s")
@NamedQuery(name = "SkOrderInfo.countTodayWithType", query = "SELECT COUNT(s) FROM SkOrderInfo s WHERE s.type = :type AND s.createTime > :minTime AND s.createTime < :maxTime")
@NamedQuery(name = "SkOrderInfo.findByIdAndUser", query = "SELECT s FROM SkOrderInfo s WHERE s.id = :id AND s.user = :user")
public class SkOrderInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name="create_time")
	private Timestamp createTime;

	private String menus;

	private String number;

	@Column(name="pay_type")
	private int payType;

	private String remark;

	private int status;

	@Column(name="takeout_order")
	private String takeoutOrder;

	@Column(name="takeout_platform")
	private int takeoutPlatform;

	@Column(name="takeout_status")
	private int takeoutStatus;

	@Column(name="total_amount")
	private float totalAmount;

	private int type;

	private int user;

	public SkOrderInfo() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getMenus() {
		return this.menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getPayType() {
		return this.payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
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

	public String getTakeoutOrder() {
		return this.takeoutOrder;
	}

	public void setTakeoutOrder(String takeoutOrder) {
		this.takeoutOrder = takeoutOrder;
	}

	public int getTakeoutPlatform() {
		return this.takeoutPlatform;
	}

	public void setTakeoutPlatform(int takeoutPlatform) {
		this.takeoutPlatform = takeoutPlatform;
	}

	public int getTakeoutStatus() {
		return this.takeoutStatus;
	}

	public void setTakeoutStatus(int takeoutStatus) {
		this.takeoutStatus = takeoutStatus;
	}

	public float getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

}