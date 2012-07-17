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
@Table(name = "word")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Word.findAll", query = "SELECT w FROM Word w"),
    @NamedQuery(name = "Word.findByWord", query = "SELECT w FROM Word w WHERE w.word = :word")})
public class Word implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "word")
    private String word;
    @ManyToMany(mappedBy = "words")
    private Collection<Document> documents;

    public Word() {
    }

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @XmlTransient
    public Collection<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Collection<Document> documentCollection) {
        this.documents = documentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (word != null ? word.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Word)) {
            return false;
        }
        Word other = (Word) object;
        if ((this.word == null && other.word != null) || (this.word != null && !this.word.equals(other.word))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Word[ word=" + word + " ]";
    }
    
}
