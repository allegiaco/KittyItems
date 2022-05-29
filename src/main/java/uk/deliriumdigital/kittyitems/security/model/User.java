package uk.deliriumdigital.kittyitems.security.model;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.deliriumdigital.kittyitems.security.encryption.Encryptor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "app_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	@Convert(converter = Encryptor.class)
	private String userFlowAddress;
	private String userPrivateKey;
	private Boolean active = true;
	
	private String password;
	
	@ManyToMany
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User(String username, Boolean active, String password, String userFlowAddress, String userPrivateKey) {
		this.username = username;
		this.active = active;
		this.password = password;
		this.userFlowAddress = userFlowAddress;
		this.userPrivateKey = userPrivateKey;
	}	
	

	
}

