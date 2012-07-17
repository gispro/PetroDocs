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
@Table(name = "ext")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ext.findAll", query = "SELECT e FROM Ext e"),
    @NamedQuery(name = "Ext.findByExt", query = "SELECT e FROM Ext e WHERE e.ext = :ext"),
    @NamedQuery(name = "Ext.findByMimeTypeDef", query = "SELECT e FROM Ext e WHERE e.mimeTypeDef = :mimeTypeDef")})
public class Ext implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ext")
    private String ext;
    @Column(name = "mimeTypeDef")
    private String mimeTypeDef;
    //@OneToMany(mappedBy = "ext")
    //private Collection<TypeExt> typeExtCollection;

    public Ext() {
    }

    public Ext(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMimeTypeDef() {
        return mimeTypeDef;
    }

    public void setMimeTypeDef(String mimeTypeDef) {
        this.mimeTypeDef = mimeTypeDef;
    }

    /*@XmlTransient
    public Collection<TypeExt> getTypeExtCollection() {
        return typeExtCollection;
    }

    public void setTypeExtCollection(Collection<TypeExt> typeExtCollection) {
        this.typeExtCollection = typeExtCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ext != null ? ext.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ext)) {
            return false;
        }
        Ext other = (Ext) object;
        if ((this.ext == null && other.ext != null) || (this.ext != null && !this.ext.equals(other.ext))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Ext[ ext=" + ext + " ]";
    }
    
}
