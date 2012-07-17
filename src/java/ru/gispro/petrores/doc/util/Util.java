package ru.gispro.petrores.doc.util;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ru.gispro.petrores.doc.entities.File;

/**
 *
 * @author fkravchenko
 */
public class Util {
    public static Map parseJsonObject(String json) throws IOException, JSONException{
        JSONObject jn = new JSONObject(json);
        return parseJsonObject(jn);
    }
    
    
    public static Map parseJsonObject(JSONObject jn) throws IOException, JSONException{
        Iterator i = jn.keys();
        HashMap<String, String> ret = new HashMap<String, String>(2);
        while (i.hasNext()){
            String fName = (String) i.next();
            ret.put(fName, jn.getString(fName));
        }
        return ret;
    }
    public static Map[] parseJsonArray(String json) throws IOException, JSONException{
        JSONArray jn = new JSONArray(json);
        Map[]ret = new Map[jn.length()];
        for(int i=0;i<ret.length;i++){
            ret[i] = parseJsonObject(jn.getJSONObject(i));
        }
        return ret;
    }
    
    public static String generateOrderByFromExtJsJson(String sort){
        try{
            Map sortMap[];
            StringBuilder sb = new StringBuilder();
            if(sort!=null&& sort.length()>0){
                sortMap = parseJsonArray(sort);
                //System.out.println("**** psoperty: " + sortMap[0].get("property"));
                for(int i=0;i<sortMap.length;i++){
                    if(i==0)
                        sb.append(" ORDER BY o.");
                    else
                        sb.append(", o.");
                    sb.append(sortMap[i].get("property"));
                    sb.append(" ");
                    sb.append(sortMap[i].get("direction"));
                }
                return sb.toString();
            }else{
                return "";
            }
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
    public static void inputToOutWithIndex2(
            String solrUrl, 
            final InputStream input, 
            final OutputStream output, 
            String fileId ,
            String contentType) throws Exception{
        SolrServer server = new CommonsHttpSolrServer(solrUrl);
        ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
        final InputStream dis = new DoubleInput(output, input);
        ContentStream cs = new ContentStreamBase() {
            @Override
            public InputStream getStream() throws IOException {
                return dis;
            }
        };
        //req.addFile(new java.io.File("apache-solr/site/features.pdf"));
        req.addContentStream(cs);
        req.setParam("literal.id", fileId);
        req.setParam("commit", "true");
        //req.setParam(ExtractingParams.EXTRACT_ONLY, "true");
        NamedList<Object> result = server.request(req);
        System.out.println("Result: " + result);        
    }
    
    //private static CommonsHttpSolrServer _solr = null;
    public static void inputToOutWithIndex(
            String solrUrl, 
            final InputStream input, 
            final OutputStream output, 
            String fileId ,
            String contentType) throws MalformedURLException, IOException{
        //HttpClient client = new DefaultHttpClient();
        HttpPost method;

        //method = new HttpPost(solrUrl + "/update/extract");
        fileId = URLEncoder.encode(fileId, "UTF-8");
        method = new HttpPost(solrUrl + "/update/extract?literal.id="+fileId+"&commit=true");

        final BufferedOutputStream boutput = new BufferedOutputStream(output);
        InputStream doubleInput = new InputStream() {
            @Override
            public int read() throws IOException {
                int tmp = input.read();
                
                if(tmp!=-1)
                    boutput.write(tmp);
                
                return tmp;
            }
        };

        MultipartEntity me = new MultipartEntity();
        InputStreamBody isb = new InputStreamBody(doubleInput, contentType, fileId);
        me.addPart("myfile", isb);
        //me.
        
        //EntityTemplate et = new EntityTemplate(cp);
        method.setEntity(me);
        //method.setHeader("Content-Type", "multipart/form-data");
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse hr = httpClient.execute(method);
        
        boutput.flush();
        boutput.close();
        //output.close();
        
        //IOUtils.copy(, System.out);
        
        StringWriter writer = new StringWriter();
        IOUtils.copy(hr.getEntity().getContent(), writer, "UTF-8");
        String theString = writer.toString();        
        
        System.out.println(theString);
        
    }
    
    public static class DoubleInput extends InputStream{

        private final BufferedOutputStream _bos;
        private final InputStream _in;
        
        public DoubleInput(OutputStream out, InputStream in) {
            _bos = new BufferedOutputStream(out);
            _in = in;
        }

        @Override
        public int read() throws IOException {
            int tmp = _in.read();
            _bos.write(tmp);
            return tmp;
        }
        
    }
    
    public static class DoubleOutput extends OutputStream{

        private final OutputStream _out1, _out2;
        
        public DoubleOutput(OutputStream out1, OutputStream out2) {
            _out1 = out1;
            _out2 = out2;
        }

        @Override
        public void write(int b) throws IOException {
            _out1.write(b);
            _out2.write(b);
        }

        
    }
    
}
