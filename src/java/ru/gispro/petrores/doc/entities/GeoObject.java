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
@Table(name = "geoObject")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeoObject.findAll", query = "SELECT g FROM GeoObject g"),
    @NamedQuery(name = "GeoObject.findById", query = "SELECT g FROM GeoObject g WHERE g.id = :id"),
    @NamedQuery(name = "GeoObject.findByIdInTable", query = "SELECT g FROM GeoObject g WHERE g.idInTable = :idInTable"),
    @NamedQuery(name = "GeoObject.findByTableName", query = "SELECT g FROM GeoObject g WHERE g.tableName = :tableName")})
public class GeoObject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "idInTable")
    private long idInTable;
    @Basic(optional = false)
    @Column(name = "tableName")
    private String tableName;
    @ManyToMany(mappedBy = "geoObjects")
    private Collection<Document> documents;

    public GeoObject() {
    }

    public GeoObject(Long id) {
        this.id = id;
    }

    public GeoObject(Long id, long idInTable, String tableName) {
        this.id = id;
        this.idInTable = idInTable;
        this.tableName = tableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getIdInTable() {
        return idInTable;
    }

    public void setIdInTable(long idInTable) {
        this.idInTable = idInTable;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @XmlTransient
    public Collection<Document> getDocuments() {
        return documents;
    }

    public void setDocumentCollection(Collection<Document> documents) {
        this.documents = documents;
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
        if (!(object instanceof GeoObject)) {
            return false;
        }
        GeoObject other = (GeoObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.GeoObject[ id=" + id + " ]";
    }
    
}
