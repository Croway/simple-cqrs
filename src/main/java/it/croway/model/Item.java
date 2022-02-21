package it.croway.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "findWithId", query = "SELECT i FROM Item i WHERE i.id = :itemId")
@NamedQuery(name = "findAll", query = "SELECT i FROM Item i")
public class Item {

	@Id
	@GeneratedValue
	public Long id;

	public String description;

	public Item() {
		super();
	}

	public Item(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	public Item(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
