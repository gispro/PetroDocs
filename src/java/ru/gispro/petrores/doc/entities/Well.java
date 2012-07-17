/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "well")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Well.findAll", query = "SELECT w FROM Well w"),
    @NamedQuery(name = "Well.findById", query = "SELECT w FROM Well w WHERE w.id = :id"),
    @NamedQuery(name = "Well.findByName", query = "SELECT w FROM Well w WHERE w.name = :name")})
public class Well implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "siteId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Site site;
    @OneToMany(mappedBy = "well")
    private Collection<Domain> domainCollection;

    public Well() {
    }

    public Well(Long id) {
        this.id = id;
    }

    public Well(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @XmlTransient
    public Collection<Domain> getDomainCollection() {
        return domainCollection;
    }

    public void setDomainCollection(Collection<Domain> domainCollection) {
        this.domainCollection = domainCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Well)) {
            return false;
        }
        Well other = (Well) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Well[ id=" + id + " ]";
    }
    
}
