/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
@RequestMapping(value = "/findDocument")
public class FindDocumentController{// implements ServletContextAware{
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;
    
    //private ServletContext servletContext;
    private String rootPath = null;
    private static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    //private static JsonView view = new JsonView();
        
        

    public FindDocumentController() {
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Documents find(@RequestBody DocumentFindCriteria dfc, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        int[]parCnt = new int[]{0};
        
        StringBuilder from = new StringBuilder();
        StringBuilder where = new StringBuilder();
        
        //dfc.setYear( (Integer[])addCriteriaFromArray("year", where, dfc.getYear(), false) );
        addEq("number", where, dfc.getNumber());
        addEq("archiveNumber", where, dfc.getArchiveNumber());
        addFullText("title", where, dfc.getTitle());
        addFullText("fullTitle", where, dfc.getFullTitle());
        addFullText("comment", where, dfc.getComment());
        addFullText("originationDetails", where, dfc.getOriginationDetails());
        addFullText("limitationDetails", where, dfc.getLimitationDetails());
        addCriteriaFromTo("originationDate", where, dfc.getOriginationDateFrom(), dfc.getOriginationDateTo());
        addCriteriaFromTo("approvalDate", where, dfc.getApprovalDateFrom(), dfc.getApprovalDateTo());
        addCriteriaFromTo("registrationDate", where, dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo());
        addCriteriaFromTo("placementDate", where, dfc.getPlacementDateFrom(), dfc.getPlacementDateTo());
        addCriteriaFromTo("updateDate", where, dfc.getUpdateDateFrom(), dfc.getUpdateDateTo());
        dfc.setPageCount((Integer[])addCriteriaFromArray("pageCount", where, dfc.getPageCount(), false));
        dfc.setScale( (Integer[]) addCriteriaFromArray("scale", where, dfc.getScale(), false) );
        dfc.setResolution( (Integer[]) addCriteriaFromArray("resolution", where, dfc.getResolution(), false) );
        dfc.setPageCount( (Integer[]) addCriteriaFromArray("pageCount", where, dfc.getPageCount(), false) );
        addEq("projectionCode", where, dfc.getProjectionCode());
        /////dfc.setAuthor((Author[]) addCriteriaFromArray("authorCollection", where, dfc.getAuthor(), true) );
        ///////dfc.setGeoObjects( (GeoObject[]) addCriteriaFromArray("geoObjects", where, dfc.getGeoObjects(), true) );
        //addEq("wordCollection", where, dfc.getWords());
        ///////////////////dfc.setSuperType( (SuperType[]) addCriteriaFromArray("type.superType", where, dfc.getSuperType(), true) );
        //addEq("filenames", sql, dfc.getFileNames());
        dfc.setType( (Type[]) addCriteriaFromArray("type", where, dfc.getType(), true) );
        dfc.setStage( (Stage[]) addCriteriaFromArray("stage", where, dfc.getStage(), true) );
        dfc.setSite( (Site[]) addCriteriaFromArray("site", where, dfc.getSite(), true) );
        dfc.setGeometryType( (GeoType[])  addCriteriaFromArray("geometryType", where, dfc.getGeometryType(), true) );
        addEq("domain", where, dfc.getDomain());
        dfc.setPlacer( (Author[]) addCriteriaFromArray("placer", where, dfc.getPlacer(), true) );
        dfc.setPeriodicity( (Periodicity[]) addCriteriaFromArray("periodicity", where, dfc.getPeriodicity(), true) );
        dfc.setClassification( (Classification[]) addCriteriaFromArray("classification", where, dfc.getClassification(), true) );
        
        where.replace(0, 4, "\n");
        where.insert(0, "select * from Document d "+from.toString()+" where");
        
        Query q = entityManager.createNativeQuery(where.toString());
        
        //addParameterFromArray(q, "year", dfc.getYear(), false, parCnt);
        addParameter(q, "number", dfc.getNumber(), parCnt);
        addParameter(q, "archiveNumber", dfc.getArchiveNumber(), parCnt);
        addParameter(q, "title", dfc.getTitle(), parCnt);
        addParameter(q, "fullTitle", dfc.getFullTitle(), parCnt);
        addParameter(q, "comment", dfc.getComment(), parCnt);
        addParameter(q, "originationDetails", dfc.getOriginationDetails(), parCnt);
        addParameter(q, "limitationDetails", dfc.getLimitationDetails(), parCnt);
        addParameterFromTo(q, "originationDate", dfc.getOriginationDateFrom(), dfc.getOriginationDateTo(), parCnt);
        addParameterFromTo(q, "approvalDate", dfc.getApprovalDateFrom(), dfc.getApprovalDateTo(), parCnt);
        addParameterFromTo(q, "registrationDate", dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo(), parCnt);
        addParameterFromTo(q, "placementDate", dfc.getPlacementDateFrom(), dfc.getPlacementDateTo(), parCnt);
        addParameterFromTo(q, "updateDate", dfc.getUpdateDateFrom(), dfc.getUpdateDateTo(), parCnt);
        addParameterFromArray(q, "pageCount", dfc.getPageCount(), false, parCnt);
        addParameterFromArray(q, "scale", dfc.getScale(), false, parCnt);
        addParameterFromArray(q, "resolution", dfc.getResolution(), false, parCnt);
        addParameter(q, "projectionCode", dfc.getProjectionCode(), parCnt);
        //////addParameterFromArray(q, "authorCollection", dfc.getAuthor(), true, parCnt);
        /////addParameterFromArray(q, "geoObjects", dfc.getGeoObjects(), true, parCnt);
        //////addParameter(q, "wordCollection", dfc.getWords(), parCnt);
        ///addParameterFromArray(q, "type.superType", dfc.getSuperType(), parCnt);
        //addParameter(q, "fileNames", dfc.getFileNames());
        addParameterFromArray(q, "type", dfc.getType(), true, parCnt);
        addParameterFromArray(q, "stage", dfc.getStage(), true, parCnt);
        addParameterFromArray(q, "site", dfc.getSite(), true, parCnt);
        addParameterFromArray(q, "geometryType", dfc.getGeometryType(), true, parCnt);
        addParameter(q, "domain", dfc.getDomain(), parCnt);
        addParameterFromArray(q, "placer", dfc.getPlacer(), true, parCnt);
        addParameterFromArray(q, "periodicity", dfc.getPeriodicity(), true, parCnt);
        addParameterFromArray(q, "classification", dfc.getClassification(), true, parCnt);
        
        List<Document> docs = q.getResultList();
        Documents ret = new Documents(docs, 100l);
        
        return ret;
    }
    
    private Object[] addCriteriaFromArray(
            String field, 
            StringBuilder sql,
            Object[]arr,
            boolean ormObj) throws Exception{
        if(arr!=null && arr.length>0){
            ArrayList arl = new ArrayList();
            
            //Method m = null;
            //if(ormObj)
            //    m = arr.getClass().getComponentType().getMethod("getId");
            
            for(Object item: arr){
                if(item!=null){
                        arl.add(item);
                }
            }
            arr = arl.toArray(arr);
            
            //String replaced = field.replace('.', '_');
            
            if(arr.length>0){
                sql.append("\nand (");
                for(int i=0;i<arr.length;i++){
                    if(i!=0)
                        sql.append(" or ");
                    sql.append("d.");
                    sql.append(field);
                    if(ormObj)
                        sql.append("Id");
                    sql.append(" = ?");
                    //sql.append(i);
                }
                sql.append(")");
            }
        }
        return arr;
    }

    private void addParameterFromArray(
            Query q,
            String field, 
            Object[]arr,
            boolean ormObj,
            int[]parCnt) throws Exception{
        
        Method m = null;
        if(arr!=null && ormObj)
            m = arr.getClass().getComponentType().getMethod("getId");
        
        if(arr!=null && arr.length>0){
            for(int i=0;i<arr.length;i++){
                if(ormObj)
                    q.setParameter(++parCnt[0], m.invoke(arr[i]) );
                else
                    q.setParameter(++parCnt[0], arr[i]);
            }
        }
    }
    
    private void addParameter(
            Query q,
            String field, 
            Object item,
            int[]parCnt){
        if(item!=null){
            q.setParameter(++parCnt[0], item);
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
                        append(" between ? and ?");
            }else{
                sql.
                        append("\nand d.").
                        append(field).
                        append(" <= ?");
            }
        }else{
            if(to!=null){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" >= ?");
            }
        }
    }
    
    
    private void addParameterFromTo(
            Query q,
            String field, 
            Object from,
            Object to,
            int[]parCnt){

        
        if(from!=null){
            if(to!=null){
                q.setParameter(++parCnt[0], from);
                q.setParameter(++parCnt[0], to);
            }else{
                q.setParameter(++parCnt[0], from);
            }
        }else{
            if(to!=null){
                q.setParameter(++parCnt[0], to);
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
                        append(" = ?");
        }
    }
    
    private void addFullText(
            String field, 
            StringBuilder sql,
            Object what){

        if(what!=null){
                sql.append("\nand freetext(").
                        append(field).
                        append(", ?)");
                        
        }
    }
    
}
