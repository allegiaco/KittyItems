package uk.deliriumdigital.kittyitems.security.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@Table(name = "auth_roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_roles_generator")
	@SequenceGenerator(name="auth_roles_generator", sequenceName = "auth_roles_seq")
	private Long id;

	@Enumerated(EnumType.STRING)
	private RoleType roleType;

	
	public Role(RoleType roleType) {
		this.roleType = roleType;
	}
	
}
