/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.indexbuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CatalystPageToURIsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParseForURIs() throws Exception {
        CatalystPageToURIs cptu = new CatalystPageToURIs();
        
        //TODO: load text hmtl
        InputStream is = this.getClass().getResourceAsStream("./CatalystSearchPageExample.html");
        String html = convertStreamToString( is );
        
        Assert.assertNotNull( html );
        Assert.assertTrue( html.length() > 0 );
        
        Collection<String> uris = cptu.parseForURIs( html );
        
        Assert.assertNotNull(uris);
        Assert.assertTrue( uris.size() > 15 );
        
        //TODO: look in the HTML and add the URIs for all the people in there

        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/92381"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/67379"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/16486"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/99588"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/101523"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/92485"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/100717"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/18413"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/97200"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/21502"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/71285"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/14385"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/100188"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/60007"));
        Assert.assertTrue( uris.contains("http://connects.catalyst.harvard.edu/profiles/profile/person/60957"));
    }
    
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
      }

}
