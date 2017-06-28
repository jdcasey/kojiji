/**
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.build.koji.model.xmlrpc.messages;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.impl.estream.EventStreamGeneratorImpl;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.junit.Test;

import com.redhat.red.build.koji.model.xmlrpc.KojiTaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetTaskRequestResponseTest
    extends AbstractKojiMessageTest
{
    private static final List<Object> createRequest()
    {
        List<Object> request = new ArrayList<>(3);

        request.add( "git://git.app.eng.bos.redhat.com/jbossws/jbossws-cxf.git#5a03c4de49c5e9017f052fd8dbfcc85deee2672f" );
        request.add( "jb-eap-7.0-rhel-7-maven-candidate" );

        Map<String, Object> options = new HashMap<>(2);

        List<String> goals = new ArrayList<>(1);
        goals.add( "install" );

        options.put( "goals", goals );

        Map<String, String> properties = new HashMap<>(10);
        properties.put( "cxf.xjcplugins.version",       "3.0.5.redhat-1" );
        properties.put( "jbossws.spi.version",          "3.1.2.Final-redhat-1" );
        properties.put( "jbossws.api.version",          "1.0.3.Final-redhat-1" );
        properties.put( "jbossws.common.version",       "3.1.3.Final-redhat-1" );
        properties.put( "jbossws.common.tools.version", "1.2.2.Final-redhat-1" );
        properties.put( "cxf.version",                  "3.1.6.redhat-1" );
        properties.put( "repo-reporting-removal",       "true" );
        properties.put( "maven.test.skip",              "true" );
        properties.put( "jaxb.impl.version",            "2.2.11.redhat-4" );
        properties.put( "no-testsuite",                 "true" );

        options.put( "properties", properties );

        request.add(options);

        return request;
    }

    @Test
    public void verifyVsCapturedHttp()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();

        List<Object> request = createRequest();

        GetTaskRequestResponse response = new GetTaskRequestResponse( request );

        bindery.render( eventParser, response );

        List<Event<?>> objectEvents = eventParser.getEvents();
        eventParser.clearEvents();

        List<Event<?>> capturedEvents = parseEvents( "getTaskRequest-response.xml" );

        assertEquals( objectEvents, capturedEvents );
    }

    @Test
    public void verify_FromCapture_PropertiesAndProfiles()
            throws Exception
    {
        String path = "getTaskRequest-response-propertiesAndProfiles.xml";
        GetTaskRequestResponse parsed = parseAs( path, GetTaskRequestResponse.class );

        KojiTaskRequest parsedRequest = parsed.getTaskRequest();
        List<Object> rawRequest = parsedRequest.getRequest();

        assertThat( rawRequest.size(), equalTo( 3 ) );

        assertThat( rawRequest.get( 0 ), equalTo( "git://git.foo.com/myproj.git#aabbccddeeff001122334455" ) );
        assertThat( rawRequest.get( 1 ), equalTo( "my-tag" ) );

        Map<String, Object> map = (Map<String, Object>) rawRequest.get( 2 );

        assertThat( map.size(), equalTo( 2 ) );

        Map<String, Object> properties = (Map<String, Object>) map.get( "properties" );

        assertThat( properties.containsKey( "skipTests" ), equalTo( true ) );
        assertThat( properties.get( "version.incremental.suffix" ), equalTo( "build" ) );

        List<Object> profiles = (List<Object>) map.get( "profiles" );
        assertThat( profiles.size(), equalTo( 1 ) );
        assertThat( profiles.get( 0 ), equalTo( "release" ) );
    }

    @Test
    public void verify_FromCapture_PropertiesAndGoals()
            throws Exception
    {
        String path = "getTaskRequest-response-propertiesAndGoals.xml";
        GetTaskRequestResponse parsed = parseAs( path, GetTaskRequestResponse.class );

        KojiTaskRequest parsedRequest = parsed.getTaskRequest();
        List<Object> rawRequest = parsedRequest.getRequest();

        assertThat( rawRequest.size(), equalTo( 3 ) );

        assertThat( rawRequest.get( 0 ), equalTo( "git://git.foo.com/myproj.git#aabbccddeeff001122334455" ) );
        assertThat( rawRequest.get( 1 ), equalTo( "my-tag" ) );

        Map<String, Object> map = (Map<String, Object>) rawRequest.get( 2 );
        assertThat( map.size(), equalTo( 2 ) );

        List<String> goals = (List<String>) map.get( "goals" );
        assertThat( goals.size(), equalTo( 2 ) );
        assertThat( goals.get( 0 ), equalTo( "install" ) );
        assertThat( goals.get( 1 ), equalTo( "javadoc:aggregate-jar" ) );

        Map<String, Object> properties = (Map<String, Object>) map.get( "properties" );
        assertThat( properties, notNullValue() );
        assertThat( properties.containsKey( "skipTests" ), equalTo( true ) );
        assertThat( properties.containsKey( "performRelease" ), equalTo( true ) );
        assertThat( properties.get( "version.incremental.suffix" ), equalTo( "build" ) );
    }

    @Test
    public void roundTrip()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();

        List<Object> request = createRequest();

        bindery.render( eventParser, new GetTaskRequestResponse( request ) );

        List<Event<?>> objectEvents = eventParser.getEvents();
        EventStreamGeneratorImpl generator = new EventStreamGeneratorImpl( objectEvents );

        GetTaskRequestResponse parsed = bindery.parse( generator, GetTaskRequestResponse.class );
        assertNotNull( parsed );

        KojiTaskRequest taskRequest = new KojiTaskRequest().withRequest(request);

        assertThat( parsed.getTaskRequest(), equalTo( taskRequest ) );
    }
}
