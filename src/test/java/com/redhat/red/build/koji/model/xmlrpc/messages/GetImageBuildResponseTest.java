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

import java.util.List;

import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.impl.estream.EventStreamGeneratorImpl;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.junit.Test;

import com.redhat.red.build.koji.model.xmlrpc.KojiImageBuildInfo;

public class GetImageBuildResponseTest
    extends AbstractKojiMessageTest
{
    @Test
    public void verifyVsCapturedHttp()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();

        GetImageBuildResponse response = new GetImageBuildResponse( new KojiImageBuildInfo( 564910 ) );

        bindery.render( eventParser, response );

        List<Event<?>> objectEvents = eventParser.getEvents();
        eventParser.clearEvents();

        List<Event<?>> capturedEvents = parseEvents( "getImageBuild-response.xml" );

        assertEquals( objectEvents, capturedEvents );
    }

    @Test
    public void roundTrip()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();

        KojiImageBuildInfo info = new KojiImageBuildInfo();
        info.setBuildId( 564910 );

        bindery.render( eventParser, new GetImageBuildResponse( info ) );

        List<Event<?>> objectEvents = eventParser.getEvents();
        EventStreamGeneratorImpl generator = new EventStreamGeneratorImpl( objectEvents );

        GetImageBuildResponse parsed = bindery.parse( generator, GetImageBuildResponse.class );
        assertNotNull( parsed );

        assertThat( parsed.getImageBuildInfo(), equalTo( info ) );
    }
}
