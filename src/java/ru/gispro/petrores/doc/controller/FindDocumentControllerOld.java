/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.management.RuntimeErrorException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import ru.gispro.petrores.doc.entities.*;
import ru.gispro.petrores.doc.entities.File;
import ru.gispro.petrores.doc.entities.Files;
import ru.gispro.petrores.doc.view.JsonView;

/**
 *
 * @author fkravchenko
 */
@Controller
@RequestMapping(value = "/findDocumentOld")
public class FindDocumentControllerOld{// implements ServletContextAware{
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;
    
    //private ServletContext servletContext;
    private String rootPath = null;
    private static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    //private static JsonView view = new JsonView();
        
        

    public FindDocumentControllerOld() {
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Documents find(@RequestBody DocumentFindCriteria dfc, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //DocumentFindCriteria fff = dfc;
        
        /*CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Document> cq = cb.createQuery(Document.class);
        
        Root<Document> docRoot = cq.from(Document.class);
        
        docRoot.joi*/
        
        
        //ftse
        
        
        
        //SQLFunc
        
        StringBuilder sql = new StringBuilder();
        
        //dfc.setYear( (Integer[])addCriteriaFromArray("year", sql, dfc.getYear()) );
        addEq("number", sql, dfc.getNumber());
        addEq("archiveNumber", sql, dfc.getArchiveNumber());
        addFullText("title", sql, dfc.getTitle());
        addFullText("fullTitle", sql, dfc.getFullTitle());
        addFullText("comment", sql, dfc.getComment());
        addFullText("originationDetails", sql, dfc.getOriginationDetails());
        addFullText("limitationDetails", sql, dfc.getLimitationDetails());
        addCriteriaFromTo("originationDate", sql, dfc.getOriginationDateFrom(), dfc.getOriginationDateTo());
        addCriteriaFromTo("approvalDate", sql, dfc.getApprovalDateFrom(), dfc.getApprovalDateTo());
        addCriteriaFromTo("registrationDate", sql, dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo());
        addCriteriaFromTo("placementDate", sql, dfc.getPlacementDateFrom(), dfc.getPlacementDateTo());
        addCriteriaFromTo("updateDate", sql, dfc.getUpdateDateFrom(), dfc.getUpdateDateTo());
        dfc.setPageCount((Integer[])addCriteriaFromArray("pageCount", sql, dfc.getPageCount()));
        dfc.setScale( (Integer[]) addCriteriaFromArray("scale", sql, dfc.getScale()) );
        dfc.setResolution( (Integer[]) addCriteriaFromArray("resolution", sql, dfc.getResolution()) );
        dfc.setPageCount( (Integer[]) addCriteriaFromArray("pageCount", sql, dfc.getPageCount()) );
        addEq("projectionCode", sql, dfc.getProjectionCode());
        dfc.setAuthor((Author[]) addCriteriaFromArray("authorCollection", sql, dfc.getAuthor()) );
        dfc.setGeoObjects( (GeoObject[]) addCriteriaFromArray("geoObjects", sql, dfc.getGeoObjects()) );
        addEq("wordCollection", sql, dfc.getWords());
        dfc.setSuperType( (SuperType[]) addCriteriaFromArray("type.superType", sql, dfc.getSuperType()) );
        //addEq("filenames", sql, dfc.getFileNames());
        dfc.setType( (Type[]) addCriteriaFromArray("type", sql, dfc.getType()) );
        dfc.setStage( (Stage[]) addCriteriaFromArray("stage", sql, dfc.getStage()) );
        dfc.setSite( (Site[]) addCriteriaFromArray("site", sql, dfc.getSite()) );
        dfc.setGeometryType( (GeoType[])  addCriteriaFromArray("geometryType", sql, dfc.getGeometryType()) );
        addEq("domain", sql, dfc.getDomain());
        dfc.setPlacer( (Author[]) addCriteriaFromArray("placer", sql, dfc.getPlacer()) );
        dfc.setPeriodicity( (Periodicity[]) addCriteriaFromArray("periodicity", sql, dfc.getPeriodicity()) );
        dfc.setClassification( (Classification[]) addCriteriaFromArray("classification", sql, dfc.getClassification()) );
        
        sql.replace(0, 4, "\n");
        sql.insert(0, "select d from Document d where");
        
        Query q = entityManager.createQuery(sql.toString());
        
        
        
        //addParameterFromArray(q, "year", dfc.getYear());
        addParameter(q, "number", dfc.getNumber());
        addParameter(q, "archiveNumber", dfc.getArchiveNumber());
        addParameter(q, "title", dfc.getTitle());
        addParameter(q, "fullTitle", dfc.getFullTitle());
        addParameter(q, "comment", dfc.getComment());
        addParameter(q, "originationDetails", dfc.getOriginationDetails());
        addParameter(q, "limitationDetails", dfc.getLimitationDetails());
        addParameterFromTo(q, "originationDate", dfc.getOriginationDateFrom(), dfc.getOriginationDateTo());
        addParameterFromTo(q, "approvalDate", dfc.getApprovalDateFrom(), dfc.getApprovalDateTo());
        addParameterFromTo(q, "registrationDate", dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo());
        addParameterFromTo(q, "placementDate", dfc.getPlacementDateFrom(), dfc.getPlacementDateTo());
        addParameterFromTo(q, "updateDate", dfc.getUpdateDateFrom(), dfc.getUpdateDateTo());
        addParameterFromArray(q, "pageCount", dfc.getPageCount());
        addParameterFromArray(q, "scale", dfc.getScale());
        addParameterFromArray(q, "resolution", dfc.getResolution());
        addParameter(q, "projectionCode", dfc.getProjectionCode());
        addParameterFromArray(q, "authorCollection", dfc.getAuthor());
        addParameterFromArray(q, "geoObjects", dfc.getGeoObjects());
        addParameter(q, "wordCollection", dfc.getWords());
        addParameterFromArray(q, "type.superType", dfc.getSuperType());
        //addParameter(q, "fileNames", dfc.getFileNames());
        addParameterFromArray(q, "type", dfc.getType());
        addParameterFromArray(q, "stage", dfc.getStage());
        addParameterFromArray(q, "site", dfc.getSite());
        addParameterFromArray(q, "geometryType", dfc.getGeometryType());
        addParameter(q, "domain", dfc.getDomain());
        addParameterFromArray(q, "placer", dfc.getPlacer());
        addParameterFromArray(q, "periodicity", dfc.getPeriodicity());
        addParameterFromArray(q, "classification", dfc.getClassification());
        
        List<Document> docs = q.getResultList();
        Documents ret = new Documents(docs, 100l);
        
        return ret;
    }
    
    private Object[] addCriteriaFromArray(
            String field, 
            StringBuilder sql,
            Object[]arr){
        if(arr!=null && arr.length>0){
            ArrayList arl = new ArrayList();
            for(Object item: arr){
                if(item!=null){
                    arl.add(item);
                }
            }
            arr = arl.toArray(arr);
            
            String replaced = field.replace('.', '_');
            
            if(arr.length>0){
                sql.append("\nand (");
                for(int i=0;i<arr.length;i++){
                    if(i!=0)
                        sql.append(" or ");
                    sql.append("d.");
                    sql.append(field);
                    sql.append(" = :")
                            .append(replaced);
                    sql.append(i);
                }
                sql.append(")");
            }
        }
        return arr;
    }

    private void addParameterFromArray(
            Query q,
            String field, 
            Object[]arr){
        field = field.replace('.', '_');
        if(arr!=null && arr.length>0){
            for(int i=0;i<arr.length;i++){
                q.setParameter(field + i, arr[i]);
            }
        }
    }
    
    private void addParameter(
            Query q,
            String field, 
            Object item){
        if(item!=null){
            q.setParameter(field, item);
        }
    }
    
   private void addCriteriaFromTo(
            String field, 
            StringBuilder sql,
            Object from,
            Object to){
        
        if(from!=null){
            if(to!=null){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" between :").
                        append(field).
                        append("From and :").
                        append(field).
                        append("To");
            }else{
                sql.
                        append("\nand d.").
                        append(field).
                        append(" <= :").
                        append(field);
            }
        }else{
            if(to!=null){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" >= :").
                        append(field);
            }
        }
    }
    
    
    private void addParameterFromTo(
            Query q,
            String field, 
            Object from,
            Object to){

        
        if(from!=null){
            if(to!=null){
                q.setParameter(field + "From", from);
                q.setParameter(field + "To", to);
            }else{
                q.setParameter(field, from);
            }
        }else{
            if(to!=null){
                q.setParameter(field, to);
            }
        }
    }

    private void addEq(
            String field, 
            StringBuilder sql,
            Object what){

        if(what!=null){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" = :").
                        append(field);
        }
    }
    
    private void addFullText(
            String field, 
            StringBuilder sql,
            Object what){

        if(what!=null){
                sql.
//                        append("\nand FUNC('freetext', d.").
//                        append(field).
//                        append(", :").
//                        append(field).
//                        append(") = 1 ");
                        append("\nand SQL('freetext(?, ?)', d.").
                        append(field).
                        append(", :").
                        append(field)
                        .append(") ");
                        
        }
    }
    
}
