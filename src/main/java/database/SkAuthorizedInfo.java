package database;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sk_authorized database table.
 * 
 */
@Entity
@Table(name="sk_authorized")
@NamedQuery(name="SkAuthorizedInfo.findAll", query="SELECT s FROM SkAuthorizedInfo s")
@NamedQuery(name="SkAuthorizedInfo.findAuthorizedNotExpired", query="SELECT s FROM SkAuthorizedInfo s WHERE s.token = :token AND s.deviceId = :deviceId AND s.expiredTime > :expiredTime")
@NamedQuery(name="SkAuthorizedInfo.findWithUserIdAndDeviceId", query="SELECT s FROM SkAuthorizedInfo s WHERE s.deviceId = :deviceId AND s.userId = :userId")
public class SkAuthorizedInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String token;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="device_id")
	private String deviceId;

	@Column(name="expired_time")
	private Timestamp expiredTime;

	@Column(name="user_id")
	private int userId;

	public SkAuthorizedInfo() {
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Timestamp getExpiredTime() {
		return this.expiredTime;
	}

	public void setExpiredTime(Timestamp expiredTime) {
		this.expiredTime = expiredTime;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}