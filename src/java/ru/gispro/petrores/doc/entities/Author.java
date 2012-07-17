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
@Table(name = "author")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a"),
    @NamedQuery(name = "Author.findById", query = "SELECT a FROM Author a WHERE a.id = :id"),
    @NamedQuery(name = "Author.findByLogin", query = "SELECT a FROM Author a WHERE a.login = :login"),
    @NamedQuery(name = "Author.findByName", query = "SELECT a FROM Author a WHERE a.name = :name"),
    @NamedQuery(name = "Author.findByShortName", query = "SELECT a FROM Author a WHERE a.shortName = :shortName"),
    @NamedQuery(name = "Author.findByEmail", query = "SELECT a FROM Author a WHERE a.email = :email"),
    @NamedQuery(name = "Author.findByPhone", query = "SELECT a FROM Author a WHERE a.phone = :phone"),
    @NamedQuery(name = "Author.findByIsInternal", query = "SELECT a FROM Author a WHERE a.isInternal = :isInternal")})
public class Author implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "login")
    private String login;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "shortName")
    private String shortName;
    @Lob
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "contact")
    private String contact;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Basic(optional = false)
    @Column(name = "isInternal")
    private boolean isInternal;
    //@JoinTable(name = "documentAuthor", joinColumns = {
    //    @JoinColumn(name = "authorId", referencedColumnName = "id")}, inverseJoinColumns = {
    //    @JoinColumn(name = "documentId", referencedColumnName = "id")})
    //@ManyToMany
    @ManyToMany(mappedBy = "authors")
    private Collection<Document> documents;
    @OneToMany(mappedBy = "placer")
    private Collection<Document> documents1;
    
    @JoinColumn(name = "organizationId", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Organization organization;
    

    public Author() {
    }

    public Author(Long id) {
        this.id = id;
    }

    public Author(Long id, String login, String name, String shortName, boolean isInternal) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.shortName = shortName;
        this.isInternal = isInternal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    @XmlTransient
    public Collection<Document> getAuthoredDocuments() {
        return documents;
    }

    public void setAuthoredDocuments(Collection<Document> documentCollection) {
        this.documents = documentCollection;
    }

    @XmlTransient
    public Collection<Document> getPlacedDocument() {
        return documents1;
    }

    public void setPlacedDocument(Collection<Document> documentCollection1) {
        this.documents1 = documentCollection1;
    }
    
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Author[ id=" + id + " ]";
    }
    
}
