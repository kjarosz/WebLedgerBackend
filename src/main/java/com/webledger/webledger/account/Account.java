package com.webledger.webledger.account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Account {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Integer id;
}
