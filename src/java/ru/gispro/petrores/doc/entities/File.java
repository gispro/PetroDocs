/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "\"file\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "File.findAll", query = "SELECT f FROM File f"),
    @NamedQuery(name = "File.findById", query = "SELECT f FROM File f WHERE f.id = :id"),
    @NamedQuery(name = "File.findByPath", query = "SELECT f FROM File f WHERE f.path = :path"),
    @NamedQuery(name = "File.findByMimeType", query = "SELECT f FROM File f WHERE f.mimeType = :mimeType"),
    @NamedQuery(name = "File.findByIsDeleted", query = "SELECT f FROM File f WHERE f.isDeleted = :isDeleted")})
public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "\"path\"")
    private String path;
    @Basic(optional = false)
    @Column(name = "mimeType")
    private String mimeType;
    @Basic(optional = false)
    @Column(name = "isDeleted")
    private long isDeleted;
    @JoinColumn(name = "documentId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Document document;
    
    @Transient
    private CommonsMultipartFile content;
    
    @Basic(optional = false)
    @Column(name = "fileName")
    private String fileName;
    

    public File() {
    }

    public File(Long id) {
        this.id = id;
    }

    public File(Long id, String path, String mimeType, long isDeleted, String fileName) {
        this.id = id;
        this.path = path;
        this.mimeType = mimeType;
        this.isDeleted = isDeleted;
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(long isDeleted) {
        this.isDeleted = isDeleted;
    }

    @XmlTransient
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    @XmlTransient
    @JsonIgnore
    public CommonsMultipartFile getContent(){
        return content;
    }
    
    public void setContent(CommonsMultipartFile content){
        this.content = content;
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
        if (!(object instanceof File)) {
            return false;
        }
        File other = (File) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.File[ id=" + id + " ]";
    }
    
}
