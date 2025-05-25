package com.was.app1;


import com.was.HttpMethod;
import com.was.HttpRequest;
import com.was.HttpResponse;

import java.io.IOException;

public abstract class HttpServelet implements SimpleServlet {

    @Override
    public void service(HttpRequest req, HttpResponse res){
        HttpMethod httpMethod = req.getMethod();
        try{
           if(httpMethod == HttpMethod.GET){
                doGet(req,res);
           }else if(httpMethod == HttpMethod.POST){
                doPost(req,res);
           }
           // ...
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    }

}
