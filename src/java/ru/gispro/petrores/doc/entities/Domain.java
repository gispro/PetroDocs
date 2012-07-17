/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "domain")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Domain.findAll", query = "SELECT d FROM Domain d"),
    @NamedQuery(name = "Domain.findById", query = "SELECT d FROM Domain d WHERE d.id = :id"),
    @NamedQuery(name = "Domain.findByName", query = "SELECT d FROM Domain d WHERE d.name = :name"),
    @NamedQuery(name = "Domain.findByLevel", query = "SELECT d FROM Domain d WHERE d.level = :level"),
    @NamedQuery(name = "Domain.findByFullName", query = "SELECT d FROM Domain d WHERE d.fullName = :fullName"),
    @NamedQuery(name = "Domain.findByPathPart", query = "SELECT d FROM Domain d WHERE d.pathPart = :pathPart")})
public class Domain implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "\"level\"")
    private Short level;
    @Column(name = "fullName")
    private String fullName;
    @Column(name = "pathPart")
    private String pathPart;
    @OneToMany(mappedBy = "domain")
    private Collection<Document> documentCollection;
    @OneToMany(mappedBy = "parent")
    private Collection<Domain> domainCollection;
    @JoinColumn(name = "parentId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Domain parent;
    
    @JoinColumn(name="siteId", referencedColumnName="id")
    @ManyToOne(optional=true)
    private Site site;
    @JoinColumn(name="wellId", referencedColumnName="id")
    @ManyToOne(optional=true)
    private Well well;

    @JoinColumn(name="typeOfWorkId", referencedColumnName="id")
    @ManyToOne(optional=true)
    private TypeOfWork typeOfWork;
    @JoinColumn(name="workProcessId", referencedColumnName="id")
    @ManyToOne(optional=true)
    private WorkProcess workProcess;
    
    public Domain() {
    }

    public Domain(Long id) {
        this.id = id;
    }

    public Domain(Long id, String name) {
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

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPathPart() {
        return pathPart;
    }

    public void setPathPart(String pathPart) {
        this.pathPart = pathPart;
    }

    @XmlTransient
    public Collection<Document> getDocumentCollection() {
        return documentCollection;
    }

    public void setDocumentCollection(Collection<Document> documentCollection) {
        this.documentCollection = documentCollection;
    }

    @XmlTransient
    public Collection<Domain> getDomainCollection() {
        return domainCollection;
    }

    public void setDomainCollection(Collection<Domain> domainCollection) {
        this.domainCollection = domainCollection;
    }
    
    @XmlTransient
    public Collection<Domain> getAllSubDomains() {
        Collection<Domain>my = getDomainCollection();
        HashSet<Domain>ret = new HashSet<Domain>(my);
        for(Domain sub: my){
            ret.addAll(sub.getAllSubDomains());
        }
        return ret;
    }
    

    

    public Domain getParent() {
        return parent;
    }

    public void setParent(Domain parent) {
        this.parent = parent;
    }

    public Well getWell() {
        return well;
    }

    public void setWell(Well well) {
        this.well = well;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public TypeOfWork getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(TypeOfWork typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public WorkProcess getWorkProcess() {
        return workProcess;
    }

    public void setWorkProcess(WorkProcess workProcess) {
        this.workProcess = workProcess;
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
        if (!(object instanceof Domain)) {
            return false;
        }
        Domain other = (Domain) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Domain[ id=" + id + " ]";
    }
    
}
