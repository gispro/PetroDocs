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
@Table(name = "classification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Classification.findAll", query = "SELECT s FROM Classification s"),
    @NamedQuery(name = "Classification.findById", query = "SELECT s FROM Classification s WHERE s.id = :id"),
    @NamedQuery(name = "Classification.findByName", query = "SELECT s FROM Classification s WHERE s.name = :name"),
    @NamedQuery(name = "Classification.findByDescr", query = "SELECT s FROM Classification s WHERE s.descr = :descr")})
public class Classification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "descr")
    private String descr;
    @OneToMany(mappedBy = "classification")
    private Collection<Document> documentCollection;

    public Classification() {
    }

    public Classification(Integer id) {
        this.id = id;
    }

    public Classification(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @XmlTransient
    public Collection<Document> getDocumentCollection() {
        return documentCollection;
    }

    public void setDocumentCollection(Collection<Document> documentCollection) {
        this.documentCollection = documentCollection;
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
        if (!(object instanceof Classification)) {
            return false;
        }
        Classification other = (Classification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Classification[ id=" + id + " ]";
    }
    
}
