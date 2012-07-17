/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fkravchenko
 */
@XmlRootElement
public class DocumentFindCriteria implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer yearFrom;
    private Integer yearTo;
    private String number;
    private String archiveNumber;
    private String title;
    private String fullTitle;
    private String comment;
    private String originationDetails;
    private String limitationDetails;
    private Date originationDateFrom;
    private Date originationDateTo;
    private Date approvalDateFrom;
    private Date approvalDateTo;
    private Date registrationDateFrom;
    private Date registrationDateTo;
    private Date placementDateFrom;
    private Date placementDateTo;
    private Date updateDateFrom;
    private Date updateDateTo;
    private Integer[] pageCount;
    private Integer[] scale;
    private Integer[] resolution;
    private String projectionCode;
    private Author[] author;
    private GeoObject[] geoObjects;
    private String words;
    private SuperType[] superType;
    private String fileNames;

    private Type[] type;
    private Stage[] stage;
    private Site[] site;
    private GeoType[] geometryType;
    private Domain domain;
    private Author[] placer;
    private Periodicity[] periodicity;
    private Classification[] classification;
    private TypeOfWork[] typeOfWork;
    private WorkProcess[] workProcess;
    
    private String simpleSearch;
    private String contentSearch;

    
    
    public DocumentFindCriteria() {
    }

    public String getSimpleSearch(){
        return simpleSearch;
    }
    
    public void setSimpleSearch(String simpleSearch){
        this.simpleSearch = simpleSearch;
    }
    
    public String getContentSearch(){
        return contentSearch;
    }
    
    public void setContentSearch(String contentSearch){
        this.contentSearch = contentSearch;
    }
    
    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public SuperType[] getSuperType() {
        return superType;
    }

    public void setSuperType(SuperType[] superType) {
        this.superType = superType;
    }

    public Date getApprovalDateFrom() {
        return approvalDateFrom;
    }

    public void setApprovalDateFrom(Date approvalDateFrom) {
        this.approvalDateFrom = approvalDateFrom;
    }

    public Date getApprovalDateTo() {
        return approvalDateTo;
    }

    public void setApprovalDateTo(Date approvalDateTo) {
        this.approvalDateTo = approvalDateTo;
    }

    public String getArchiveNumber() {
        return archiveNumber;
    }

    public void setArchiveNumber(String archiveNumber) {
        this.archiveNumber = archiveNumber;
    }

    public Author[] getAuthor() {
        return author;
    }

    public void setAuthor(Author[] author) {
        this.author = author;
    }

    public Classification[] getClassification() {
        return classification;
    }

    public void setClassification(Classification[] classification) {
        this.classification = classification;
    }

    public TypeOfWork[] getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(TypeOfWork[] typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public WorkProcess[] getWorkProcess() {
        return workProcess;
    }

    public void setWorkProcess(WorkProcess[] workProcess) {
        this.workProcess = workProcess;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public GeoObject[] getGeoObjects() {
        return geoObjects;
    }

    public void setGeoObjects(GeoObject[] geoObjects) {
        this.geoObjects = geoObjects;
    }

    public GeoType[] getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(GeoType[] geometryType) {
        this.geometryType = geometryType;
    }

    public String getLimitationDetails() {
        return limitationDetails;
    }

    public void setLimitationDetails(String limitationDetails) {
        this.limitationDetails = limitationDetails;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getOriginationDateFrom() {
        return originationDateFrom;
    }

    public void setOriginationDateFrom(Date originationDateFrom) {
        this.originationDateFrom = originationDateFrom;
    }

    public Date getOriginationDateTo() {
        return originationDateTo;
    }

    public void setOriginationDateTo(Date originationDateTo) {
        this.originationDateTo = originationDateTo;
    }

    public String getOriginationDetails() {
        return originationDetails;
    }

    public void setOriginationDetails(String originationDetails) {
        this.originationDetails = originationDetails;
    }

    public Integer[] getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer[] pageCount) {
        this.pageCount = pageCount;
    }

    public Periodicity[] getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Periodicity[] periodicity) {
        this.periodicity = periodicity;
    }

    public Date getPlacementDateFrom() {
        return placementDateFrom;
    }

    public void setPlacementDateFrom(Date placementDateFrom) {
        this.placementDateFrom = placementDateFrom;
    }

    public Date getPlacementDateTo() {
        return placementDateTo;
    }

    public void setPlacementDateTo(Date placementDateTo) {
        this.placementDateTo = placementDateTo;
    }

    public Author[] getPlacer() {
        return placer;
    }

    public void setPlacer(Author[] placer) {
        this.placer = placer;
    }

    public String getProjectionCode() {
        return projectionCode;
    }

    public void setProjectionCode(String projectionCode) {
        this.projectionCode = projectionCode;
    }

    public Date getRegistrationDateFrom() {
        return registrationDateFrom;
    }

    public void setRegistrationDateFrom(Date registrationDateFrom) {
        this.registrationDateFrom = registrationDateFrom;
    }

    public Date getRegistrationDateTo() {
        return registrationDateTo;
    }

    public void setRegistrationDateTo(Date registrationDateTo) {
        this.registrationDateTo = registrationDateTo;
    }

    public Integer[] getResolution() {
        return resolution;
    }

    public void setResolution(Integer[] resolution) {
        this.resolution = resolution;
    }

    public Integer[] getScale() {
        return scale;
    }

    public void setScale(Integer[] scale) {
        this.scale = scale;
    }

    public Site[] getSite() {
        return site;
    }

    public void setSite(Site[] site) {
        this.site = site;
    }

    public Stage[] getStage() {
        return stage;
    }

    public void setStage(Stage[] stage) {
        this.stage = stage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type[] getType() {
        return type;
    }

    public void setType(Type[] type) {
        this.type = type;
    }

    public Date getUpdateDateFrom() {
        return updateDateFrom;
    }

    public void setUpdateDateFrom(Date updateDateFrom) {
        this.updateDateFrom = updateDateFrom;
    }

    public Date getUpdateDateTo() {
        return updateDateTo;
    }

    public void setUpdateDateTo(Date updateDateTo) {
        this.updateDateTo = updateDateTo;
    }

    public String getWords() {
        return words;
    } 

    public void setWords(String words) {
        this.words = words;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }
}
