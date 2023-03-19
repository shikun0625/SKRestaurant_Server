package database;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the sk_materiel_action database table.
 * 
 */
@Entity
@Table(name = "sk_materiel_action")
@NamedQuery(name = "SkMaterielActionInfo.findAll", query = "SELECT s FROM SkMaterielActionInfo s")
public class SkMaterielActionInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name = "action_type")
	private int actionType;

	@Column(name = "create_time")
	private Timestamp createTime;

	private int delta;

	@Column(name = "materiel_id")
	private int materielId;

	private int reason;

	public SkMaterielActionInfo() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getActionType() {
		return this.actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getDelta() {
		return this.delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public int getMaterielId() {
		return this.materielId;
	}

	public void setMaterielId(int materielId) {
		this.materielId = materielId;
	}

	public int getReason() {
		return this.reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

}