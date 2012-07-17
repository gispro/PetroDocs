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
@Table(name = "projection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projection.findAll", query = "SELECT g FROM Projection g"),
    @NamedQuery(name = "Projection.findByCode", query = "SELECT g FROM Projection g WHERE g.code = :code"),
    @NamedQuery(name = "Projection.findByName", query = "SELECT g FROM Projection g WHERE g.name = :name"),
    @NamedQuery(name = "Projection.findByDescr", query = "SELECT g FROM Projection g WHERE g.descr = :descr")})
public class Projection implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "descr")
    private String descr;

    public Projection() {
    }

    public Projection(String code) {
        this.code = code;
    }

    public Projection(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (code != null ? code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projection)) {
            return false;
        }
        Projection other = (Projection) object;
        if ((this.code == null && other.code != null) || (this.code != null && !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Projection[ code=" + code + " ]";
    }
    
}
