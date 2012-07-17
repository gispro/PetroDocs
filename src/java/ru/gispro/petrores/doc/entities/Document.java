/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author fkravchenko
 */
@Entity
@Table(name = "document")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d"),
    @NamedQuery(name = "Document.findById", query = "SELECT d FROM Document d WHERE d.id = :id"),
    @NamedQuery(name = "Document.findByYear", query = "SELECT d FROM Document d WHERE d.year = :year"),
    @NamedQuery(name = "Document.findByNumber", query = "SELECT d FROM Document d WHERE d.number = :number"),
    @NamedQuery(name = "Document.findByArchiveNumber", query = "SELECT d FROM Document d WHERE d.archiveNumber = :archiveNumber"),
    @NamedQuery(name = "Document.findByTitle", query = "SELECT d FROM Document d WHERE d.title = :title"),
    @NamedQuery(name = "Document.findByFullTitle", query = "SELECT d FROM Document d WHERE d.fullTitle = :fullTitle"),
    @NamedQuery(name = "Document.findByOriginationDate", query = "SELECT d FROM Document d WHERE d.originationDate = :originationDate"),
    @NamedQuery(name = "Document.findByApprovalDate", query = "SELECT d FROM Document d WHERE d.approvalDate = :approvalDate"),
    @NamedQuery(name = "Document.findByRegistrationDate", query = "SELECT d FROM Document d WHERE d.registrationDate = :registrationDate"),
    @NamedQuery(name = "Document.findByPlacementDate", query = "SELECT d FROM Document d WHERE d.placementDate = :placementDate"),
    @NamedQuery(name = "Document.findByUpdateDate", query = "SELECT d FROM Document d WHERE d.updateDate = :updateDate"),
    @NamedQuery(name = "Document.findByPageCount", query = "SELECT d FROM Document d WHERE d.pageCount = :pageCount"),
    @NamedQuery(name = "Document.findByScale", query = "SELECT d FROM Document d WHERE d.scale = :scale"),
    @NamedQuery(name = "Document.findByResolution", query = "SELECT d FROM Document d WHERE d.resolution = :resolution"),
    @NamedQuery(name = "Document.findByProjection", query = "SELECT d FROM Document d WHERE d.projection = :projection")})
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(name = "year")
    private int year;
    @Basic(optional = false)
    @Column(name = "number")
    private String number;
    @Column(name = "archiveNumber")
    private String archiveNumber;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Column(name = "fullTitle")
    private String fullTitle;
    @Lob
    @Column(name = "comment")
    private String comment;
    @Lob
    @Column(name = "originationDetails")
    private String originationDetails;
    @Lob
    @Column(name = "limitationDetails")
    private String limitationDetails;
    @Basic(optional = false)
    @Column(name = "originationDate")
    @Temporal(TemporalType.DATE)
    private Date originationDate;
    @Column(name = "approvalDate")
    @Temporal(TemporalType.DATE)
    private Date approvalDate;
    @Basic(optional = false)
    @Column(name = "registrationDate")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;
    @Basic(optional = false)
    @Column(name = "placementDate")
    @Temporal(TemporalType.DATE)
    private Date placementDate;
    @Column(name = "updateDate")
    @Temporal(TemporalType.DATE)
    private Date updateDate;
    @Column(name = "pageCount")
    private Integer pageCount;
    @Column(name = "scale")
    private Integer scale;
    @Column(name = "resolution")
    private Integer resolution;
    @JoinColumn(name = "projectionCode", referencedColumnName = "code")
    @ManyToOne
    private Projection projection;
    //@ManyToMany(mappedBy = "documentCollection")
    @JoinTable(name = "documentAuthor", joinColumns = {
        @JoinColumn(name = "documentId", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "authorId", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Author> authors;
    @JoinTable(name = "documentGeoObject", joinColumns = {
        @JoinColumn(name = "documentId", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "geoObjectId", referencedColumnName = "id")})
    @ManyToMany
    private Collection<GeoObject> geoObjects;
    @JoinTable(name = "documentWord", joinColumns = {
        @JoinColumn(name = "documentId", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "word", referencedColumnName = "word")})
    @ManyToMany(cascade= CascadeType.ALL)
    private Collection<Word> words;
    @JoinColumn(name = "typeId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Type type;
    @JoinColumn(name = "stageId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Stage stage;
    @JoinColumn(name = "siteId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Site site;
    @JoinColumn(name = "geometryTypeId", referencedColumnName = "id")
    @ManyToOne
    private GeoType geometryType;
    @JoinColumn(name = "domainId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Domain domain;
    @JoinColumn(name = "placerId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Author placer;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    private Collection<File> fileCollection;
    @JoinColumn(name = "periodicityId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Periodicity periodicity;
    @JoinColumn(name = "classificationId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Classification classification;
    @JoinColumn(name = "typeOfWorkId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TypeOfWork typeOfWork;
    @JoinColumn(name = "workProcessId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private WorkProcess workProcess;

    @Lob
    @Column(name = "condensed")
    private String condensed;
    
    
    public Document() {
    }

    public Document(Long id) {
        this.id = id;
    }

    public Document(Long id, int year, String number, String title, Date originationDate, Date registrationDate, Date placementDate) {
        this.id = id;
        this.year = year;
        this.number = number;
        this.title = title;
        this.originationDate = originationDate;
        this.registrationDate = registrationDate;
        this.placementDate = placementDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getArchiveNumber() {
        return archiveNumber;
    }

    public void setArchiveNumber(String archiveNumber) {
        this.archiveNumber = archiveNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOriginationDetails() {
        return originationDetails;
    }

    public void setOriginationDetails(String originationDetails) {
        this.originationDetails = originationDetails;
    }

    public String getLimitationDetails() {
        return limitationDetails;
    }

    public void setLimitationDetails(String limitationDetails) {
        this.limitationDetails = limitationDetails;
    }

    public Date getOriginationDate() {
        return originationDate;
    }

    public void setOriginationDate(Date originationDate) {
        this.originationDate = originationDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getPlacementDate() {
        return placementDate;
    }

    public void setPlacementDate(Date placementDate) {
        this.placementDate = placementDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    //@XmlTransient
    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> authorCollection) {
        this.authors = authorCollection;
    }

    //@XmlTransient
    public Collection<GeoObject> getGeoObjects() {
        return geoObjects;
    }

    public void setGeoObjects(Collection<GeoObject> geoObjects) {
        this.geoObjects = geoObjects;
    }

    //@XmlTransient
    public Collection<Word> getWords() {
        return words;
    }

    public void setWords(Collection<Word> wordCollection) {
        this.words = wordCollection;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public GeoType getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(GeoType geometryType) {
        this.geometryType = geometryType;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Author getPlacer() {
        return placer;
    }

    public void setPlacer(Author placer) {
        this.placer = placer;
    }

    public Periodicity getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Periodicity periodicity) {
        this.periodicity = periodicity;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
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
    
    
    //@XmlTransient
    public Collection<File> getFiles() {
        return fileCollection;
    }

    public void setFiles(Collection<File> fileCollection) {
        this.fileCollection = fileCollection;
    }

    @XmlTransient
    public String getCondensed() {
        return condensed;
    }

    public void setCondensed(String condensed) {
        this.condensed = condensed;
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
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.gispro.petrores.doc.entities.Document[ id=" + id + " ]";
    }
    
}
