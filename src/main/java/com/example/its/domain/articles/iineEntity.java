package com.example.its.domain.articles;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;

@Data
@Entity
public class iineEntity {
	@Id
	private long id;
	private long weene;
}
