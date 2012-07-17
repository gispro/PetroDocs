/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.*;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Pedro Assuncao (assuncas@gmail.com)
 * @May 26, 2009
 *
 */
@Controller
public class AjaxProxyController {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "proxy")
    public final void proxyAjaxCall(
            @RequestParam(required = true, value = "url") String url,
            final HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // URL needs to be url decoded
        url = URLDecoder.decode(url, "utf-8");

        //OutputStreamWriter writer =
        //        new OutputStreamWriter(response.getOutputStream());
        
        HttpClient client = new DefaultHttpClient();
        HttpUriRequest method;
        if (request.getMethod().equals("GET")) {
            method = (HttpUriRequest) new HttpGet(url);
        } else if (request.getMethod().equals("POST")) {
            method = (HttpUriRequest) new HttpPost(url);
            ContentProducer cp = new ContentProducer() {
                @Override
                public void writeTo(OutputStream out) throws IOException {
                    BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
                    BufferedOutputStream bos = new BufferedOutputStream(out);
                    int byt;
                    while((byt=bis.read())!=-1){
                        bos.write(byt);
                    }
                    bos.flush();
                    bis.close();
                    bos.close();
                }
            };
            ((HttpPost)method).setEntity(new EntityTemplate(cp));
        } else {
            throw new RuntimeException(
                    "This proxy only supports GET and POST methods.");
        }
        
        /*{
            Enumeration<String> headers = request.getHeaderNames();
            while(headers.hasMoreElements()){
                String headerName = headers.nextElement();
                String header = request.getHeader(headerName);

                method.setHeader(headerName, header);
            }
        }*/
        
        
        
        
        //HttpGet method = new HttpGet(url);
        HttpResponse hr = client.execute(method);

        Header[] headers = hr.getAllHeaders();
        for (Header header : headers) {
            if ("Content-Type".equalsIgnoreCase(header.getName())) {
                response.setContentType(header.getValue());
            }
        }

        BufferedInputStream is = new BufferedInputStream( hr.getEntity().getContent() );
        BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
        int byt;
        while((byt = is.read())!=-1){
            os.write(byt);
        }
        os.flush();
        os.close();
        is.close();
    }
}