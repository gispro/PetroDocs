/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "typeExt")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TypeExt.findAll", query = "SELECT t FROM TypeExt t"),
    @NamedQuery(name = "TypeExt.findById", query = "SELECT t FROM TypeExt t WHERE t.id = :id"),
    @NamedQuery(name = "TypeExt.findByIsMain", query = "SELECT t FROM TypeExt t WHERE t.isMain = :isMain"),
    @NamedQuery(name = "TypeExt.findByMaxCount", query = "SELECT t FROM TypeExt t WHERE t.maxCount = :maxCount"),
    @NamedQuery(name = "TypeExt.findByMinCount", query = "SELECT t FROM TypeExt t WHERE t.minCount = :minCount"),
    @NamedQuery(name = "TypeExt.findByMimeType", query = "SELECT t FROM TypeExt t WHERE t.mimeType = :mimeType")})
public class TypeExt implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "isMain")
    private boolean isMain;
    @Basic(optional = false)
    @Column(name = "maxCount")
    private short maxCount;
    @Basic(optional = false)
    @Column(name = "minCount")
    private short minCount;
    @Column(name = "mimeType")
    private String mimeType;
    //@JoinColumn(name = "typeId", referencedColumnName = "id", nullable=false)
    //@ManyToOne(optional = false)//, cascade= {CascadeType.MERGE, CascadeType.PERSIST})
    //private Type typeId;
    @JoinColumn(name = "ext", referencedColumnName = "ext")
    @ManyToOne(optional = false, cascade= CascadeType.MERGE)
    private Ext ext;

    public TypeExt() {
    }

    public TypeExt(Long id) {
        this.id = id;
    }

    public TypeExt(Long id, boolean isMain, short maxCount, short minCount) {
        this.id = id;
        this.isMain = isMain;
        this.maxCount = maxCount;
        this.minCount = minCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }

    public short getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(short maxCount) {
        this.maxCount = maxCount;
    }

    public short getMinCount() {
        return minCount;
    }

    public void setMinCount(short minCount) {
        this.minCount = minCount;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /*@XmlElement(name="typeId")
    public Long getTypeIdReal() {
        return typeId.getId();
    }*/
    
    //@XmlTransient
    //public Type getTypeId() {
    //    return typeId;
    //}

    //public void setTypeId(Type typeId) {
    //    this.typeId = typeId;
    //}

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
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
        if (!(object instanceof TypeExt)) {
            return false;
        }
        TypeExt other = (TypeExt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.TypeExt[ id=" + id + " ]";
    }
    
}
