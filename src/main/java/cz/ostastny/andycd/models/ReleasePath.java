package cz.ostastny.andycd.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class ReleasePath {
	private Integer id;
	private String name;
	private Set<Environment> envs = new HashSet<Environment>(0); 
	
	public ReleasePath() {
		
	}
	
	public ReleasePath(String name) {
		this.name = name;
	}

	@Column(name = "name", unique = false, nullable = false, length = 64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pipeline", cascade = CascadeType.PERSIST)
	public Set<Environment> getEnvs() {
		return envs;
	}

	public void setEnvs(Set<Environment> envs) {
		this.envs = envs;
	}
}
