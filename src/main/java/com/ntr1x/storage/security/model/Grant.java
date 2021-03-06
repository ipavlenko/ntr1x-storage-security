package com.ntr1x.storage.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import com.ntr1x.storage.core.model.Resource;

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
    name = "grants",
    indexes= {
        @Index(columnList = "UserId, Scope, Pattern, Action", unique = true),
    }
)
@PrimaryKeyJoinColumn(name = "ResourceId", referencedColumnName = "Id")
@CascadeOnDelete
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grant extends Resource {
    
    @XmlElement
    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false, updatable = false)
    private User user;
    
    @Column(name = "Pattern")
    private String pattern;
    
    @Column(name = "Action")
    private String action;
}
