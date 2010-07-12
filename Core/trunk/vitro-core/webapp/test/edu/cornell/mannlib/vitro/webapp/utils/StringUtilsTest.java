/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.utils;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.Assert;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;

public class StringUtilsTest extends AbstractTestClass {
    
    protected static List<String> stringList = new ArrayList<String>();
    protected static List<Integer> intList = new ArrayList<Integer>();
    static {
        stringList.add("apple");
        stringList.add("banana");
        stringList.add("orange");  
        
        intList.add(1);
        intList.add(2);
        intList.add(3);
    }

    @Test
    public void testCapitalize() {
        String s1 = "cat";
        Assert.assertEquals("Cat", StringUtils.capitalize(s1));
        
        String s2 = "Cat";
        Assert.assertEquals(s2, StringUtils.capitalize(s2));
        
        String s3 = "CAT";
        Assert.assertEquals(s3, StringUtils.capitalize(s3));
        
    }
    
    @Test
    public void testQuote() {
        String s1 = "cat";
        Assert.assertEquals("\"cat\"", StringUtils.quote(s1));
        
        String s2 = "";
        Assert.assertEquals("", StringUtils.quote(s2));
    }
    
    @Test 
    public void testJoinNoArgs() {
        
        Assert.assertEquals("apple,banana,orange", StringUtils.join(stringList));
        Assert.assertEquals("1,2,3", StringUtils.join(intList));
    }
    
    @Test 
    public void testJoinArgs() {
        
        Assert.assertEquals("apple:banana:orange", StringUtils.join(stringList, false, ":"));
        Assert.assertEquals("\"apple\"|\"banana\"|\"orange\"", StringUtils.join(stringList, true, "|"));
        Assert.assertEquals("\"apple\",\"banana\",\"orange\"", StringUtils.join(stringList, true, null));
        Assert.assertEquals("apple,banana,orange", StringUtils.join(stringList, false, null));
        Assert.assertEquals("apple...banana...orange", StringUtils.join(stringList, false, "..."));
        
    }    
    
    @Test 
    public void testQuotedList() {
        
        Assert.assertEquals("\"apple\"|\"banana\"|\"orange\"", StringUtils.quotedList(stringList, "|"));
        Assert.assertEquals("\"apple\",\"banana\",\"orange\"", StringUtils.quotedList(stringList, null));
    }
    
    @Test
    public void testEqualsOneOf() {
        
        String s1 = "cat";
        Assert.assertTrue(StringUtils.equalsOneOf(s1, "dog", "mouse", "cat", "horse"));
        Assert.assertTrue(StringUtils.equalsOneOf(s1, "cat"));
        Assert.assertFalse(StringUtils.equalsOneOf(s1, "dog", "mouse", "horse"));
        Assert.assertFalse(StringUtils.equalsOneOf(s1));       
    }
}
