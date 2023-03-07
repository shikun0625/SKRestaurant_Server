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
@NamedQuery(name="SkRequestInfo.findAll", query="SELECT s FROM SkRequestInfo s")
public class SkRequestInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="request_id")
	private String requestId;

	private String action;

	private String body;

	@Column(name="create_time")
	private Timestamp createTime;

	private String host;

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

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
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