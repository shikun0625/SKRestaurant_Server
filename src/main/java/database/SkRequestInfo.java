package database;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sk_request database table.
 * 
 */
@Entity
@Table(name="sk_request")
@NamedQuery(name = "SkRequestInfo.findAll", query = "SELECT s FROM SkRequestInfo s")
@NamedQuery(name = "SkRequestInfo.findByRequestId", query = "SELECT s FROM SkRequestInfo s WHERE s.requestId = :requestId")
public class SkRequestInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="request_id")
	private String requestId;

	private String action;

	private String body;

	@Column(name="create_time")
	private Timestamp createTime;

	private String device;

	@Column(name="device_id")
	private String deviceId;

	private String host;

	private String os;

	@Column(name="os_version")
	private String osVersion;

	private String parameter;

	private String reason;

	private int status;

	private int user;

	public SkRequestInfo() {
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return this.osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

}