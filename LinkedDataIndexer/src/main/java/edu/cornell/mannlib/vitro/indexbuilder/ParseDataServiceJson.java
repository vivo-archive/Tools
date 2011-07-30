package edu.cornell.mannlib.vitro.indexbuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;

/**
 * This is a class intended to parse Json from the JsonServlet. 
 */
public class ParseDataServiceJson{

  /**
   * This parses a json response from the vivo dataservice
   * into a list of URLs to subsiquently request against the
   * vivo dataservice.
   */   
    public static String[] parseInitialIndividualsByVClassForURLs(String text,String action) throws JSONException{
        JSONObject json = new JSONObject(text);
        int count = json.getInt( "totalCount" );
        JSONArray individuals = json.getJSONArray( "individuals" );
        int individualsPerPage = individuals.length();
        String vclass = "unset";
        try{
            vclass = URLEncoder.encode(json.getJSONObject("vclass").getString("URI"),"UTF-8");
        }catch( UnsupportedEncodingException e){
            e.printStackTrace();            
        }
        
        int pageCount = 0;
        if( individualsPerPage > 0 ) {
            pageCount = count/individualsPerPage;
            if( count % individualsPerPage > 0 )
                pageCount ++;

            String urls[] = new String[ pageCount ];
            for( int i=1 ; i <= pageCount ; i++)
                urls[i-1]= "/dataservice?"+ action + "&vclassId="+vclass+"&page=" + i ;
            return urls;
        }else{
            return new String[0];
        }
    }

    public static String[] parseIndividualsByVClassForURIs(String text) throws JSONException{
        JSONObject json = new JSONObject(text);
        JSONArray individuals = json.getJSONArray( "individuals" );
        int count = individuals.length();
        String uris[] = new String[ count ];
        for( int i = 0 ; i< count ; i++){
            JSONObject ind = individuals.getJSONObject(i);
            uris[i]= ind.getString("URI");
        }
        return uris;
    }

    // public static String[] parseInitialIndividualsByVClassForURLs(HttpResponse response) 
    //     throws JSONException,  IOException {
    //     if( response != null ){
    //         HttpEntity entity = response.getEntity();
    //         if (entity != null) {
    //             return EntityUtils.toByteArray(entity);
    //         } else {
    //             return null;
    //         }
    //     }
    // }
}

