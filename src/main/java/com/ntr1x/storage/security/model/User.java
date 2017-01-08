package com.ntr1x.storage.security.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import com.ntr1x.storage.core.converter.ConverterProvider.LocalDateTimeConverter;
import com.ntr1x.storage.core.model.Resource;
import com.ntr1x.storage.security.converter.EncryptedConverter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
	name = "users",
	indexes= {
		@Index(columnList = "Scope,Origin,Identity,Email", unique = true),
	}
)
@PrimaryKeyJoinColumn(name = "ResourceId", referencedColumnName = "Id")
@CascadeOnDelete
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends Resource {
	
	@Column(name = "Origin", nullable = false)
	private String origin;
	
	@Convert(converter = EncryptedConverter.class)
	@Column(name = "Identity", nullable = false)
	private String identity;
	
	@Convert(converter = EncryptedConverter.class)
	@Column(name = "Name", nullable = false)
	private String name;
	
	@Convert(converter = EncryptedConverter.class)
	@Column(name = "Email", nullable = false)
	private String email;
	
	@XmlTransient
	@Convert(converter = EncryptedConverter.class)
	@Column(name = "EmailNew", nullable = true)
	private String emailNew;
	
	@Column(name = "EmailConfirmed")
	private boolean emailConfirmed;
	
	@Column(name = "Registered")
	@XmlJavaTypeAdapter(LocalDateTimeConverter.class)
	@ApiModelProperty(example="2016-10-07T04:05")
	private LocalDateTime registered;
	
	@XmlTransient
	@Column(name = "Random")
	@ApiModelProperty(hidden = true)
	private Integer random;
	
	@XmlTransient
	@Column(name = "Pwdhash")
	@ApiModelProperty(hidden = true)
	private String pwdhash;
	
	@ResourceRelation
	@XmlElement
	@OneToMany(mappedBy = "user")
	@CascadeOnDelete
	@ApiModelProperty(hidden = true)
	private List<Session> sessions;
	
	@ResourceRelation
    @XmlElement
    @OneToMany(mappedBy = "user")
    @CascadeOnDelete
    @ApiModelProperty(hidden = true)
    private List<Grant> grants;
}
