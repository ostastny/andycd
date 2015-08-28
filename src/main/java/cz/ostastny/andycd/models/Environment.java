package cz.ostastny.andycd.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Environment {
	private Integer id;
	private String name;
	private ReleasePath pipeline;
	
	public Environment() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", unique = false, nullable = false, length = 64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pipeline_id")
	@JsonBackReference
	public ReleasePath getPipeline() {
		return pipeline;
	}

	public void setPipeline(ReleasePath pipeline) {
		this.pipeline = pipeline;
	}
}
