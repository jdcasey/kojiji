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
