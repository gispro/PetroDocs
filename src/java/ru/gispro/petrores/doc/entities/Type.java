/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Type.findAll", query = "SELECT t FROM Type t"),
    @NamedQuery(name = "Type.findById", query = "SELECT t FROM Type t WHERE t.id = :id"),
    @NamedQuery(name = "Type.findByName", query = "SELECT t FROM Type t WHERE t.name = :name"),
    @NamedQuery(name = "Type.findByIsMultiFile", query = "SELECT t FROM Type t WHERE t.isMultiFile = :isMultiFile"),
    @NamedQuery(name = "Type.findByIsGeo", query = "SELECT t FROM Type t WHERE t.isGeo = :isGeo"),
    @NamedQuery(name = "Type.findByDescr", query = "SELECT t FROM Type t WHERE t.descr = :descr")})
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "isMultiFile")
    private boolean isMultiFile;
    @Basic(optional = false)
    @Column(name = "isGeo")
    private boolean isGeo;
    @Column(name = "descr")
    private String descr;
    @OneToMany(/*mappedBy = "typeId", */cascade= {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true)
    @JoinColumn( name="typeId", referencedColumnName="id", nullable=false)
    //@ElementCollection
    //@XmlElement(name="typeExt")
    private Collection<TypeExt> typeExt;
    @JoinColumn(name = "superTypeId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SuperType superType;
    @OneToMany(mappedBy = "type")
    private Collection<Document> documentCollection;

    public Type() {
    }

    public Type(Long id) {
        this.id = id;
    }

    public Type(Long id, String name, boolean isMultiFile, boolean isGeo) {
        this.id = id;
        this.name = name;
        this.isMultiFile = isMultiFile;
        this.isGeo = isGeo;
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

    public boolean getIsMultiFile() {
        return isMultiFile;
    }

    public void setIsMultiFile(boolean isMultiFile) {
        this.isMultiFile = isMultiFile;
    }

    public boolean getIsGeo() {
        return isGeo;
    }

    public void setIsGeo(boolean isGeo) {
        this.isGeo = isGeo;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    //@XmlTransient
    //@XmlInverseReference(mappedBy="typeId")
    //@XmlElementWrapper(name="typeExts")
    //@XmlTransient
    //@XmlElement(name="typeExt")
    public Collection<TypeExt> getTypeExt() {
        return typeExt;
    }

    ///////////////////////////@XmlElement(name="typeExt")
    public void setTypeExt(Collection<TypeExt> typeExtCollection) {
        this.typeExt = typeExtCollection;
    }

    public SuperType getSuperType() {
        return superType;
    }

    public void setSuperType(SuperType superType) {
        this.superType = superType;
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
        if (!(object instanceof Type)) {
            return false;
        }
        Type other = (Type) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Type[ id=" + id + " ]";
    }
    
}
