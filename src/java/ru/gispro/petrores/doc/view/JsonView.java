package ru.gispro.petrores.doc.view;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.web.servlet.View;

/**
 *
 * @author fkravchenko
 */
public class JsonView implements View{

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
            mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
            mapper.getSerializationConfig().setAnnotationIntrospector(introspector);   
            
            //resp.setHeader("Content-Type", "text/html; charset=UTF-8"); // "application/json");
            
            JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(resp.getOutputStream(), JsonEncoding.UTF8);
            
            ObjectNode json = mapper.valueToTree(map);
            //json.put("success", true);
            mapper.writeTree(generator, json);
            //mapper.writeValue(generator, ret);
            generator.flush();
    }
    
}
