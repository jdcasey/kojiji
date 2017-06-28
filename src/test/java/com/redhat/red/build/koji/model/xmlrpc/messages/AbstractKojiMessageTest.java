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

import org.apache.commons.io.IOUtils;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.impl.estream.EventStreamGeneratorImpl;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.commonjava.rwx.impl.stax.StaxParser;
import com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcBindery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Created by jdcasey on 12/3/15.
 */
public class AbstractKojiMessageTest
{
    protected static final String MESSAGES_BASE = "messages/";

    protected static KojiXmlRpcBindery bindery;

    protected EventStreamParserImpl eventParser;

    @BeforeClass
    public static void setupClass()
            throws Exception
    {
        bindery = new KojiXmlRpcBindery();
    }

    @Before
    public void setup()
            throws Exception
    {
        eventParser = new EventStreamParserImpl();
    }

    protected String readResource( String resourceFile )
            throws Exception
    {
        String resource = MESSAGES_BASE + resourceFile;
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource ))
        {
            if ( is == null )
            {
                Assert.fail( "Cannot find message XML file on classpath: " + resource );
            }

            return IOUtils.toString( is );
        }
    }

    protected List<Event<?>> parseEvents( String resourceFile )
            throws Exception
    {
        String resource = MESSAGES_BASE + resourceFile;
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource ))
        {
            if ( is == null )
            {
                Assert.fail( "Cannot find message XML file on classpath: " + resource );
            }

            eventParser.clearEvents();

            StaxParser parser = new StaxParser( is );
            parser.parse( eventParser );

            List<Event<?>> events = eventParser.getEvents();

            return events;
        }
        finally
        {
            eventParser.clearEvents();
        }
    }

    protected <T> T parseAs( String resourceFile, Class<T> type )
            throws Exception
    {
        String resource = MESSAGES_BASE + resourceFile;
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource ))
        {
            if ( is == null )
            {
                Assert.fail( "Cannot find message XML file on classpath: " + resource );
            }

            Logger logger = LoggerFactory.getLogger( getClass() );
            logger.trace( "Parsing via: {}", bindery );
            return bindery.parse( is, type );
        }
    }

}
