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
package com.redhat.red.build.koji.model.util;

import org.apache.commons.lang.StringUtils;
import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.mapping.Mapping;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.binding.spi.BindingContext;
import org.commonjava.rwx.binding.spi.value.CustomValueBinder;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.join;

/**
 * Created by jdcasey on 1/8/16.
 */
public class StringListValueBinder
        extends CustomValueBinder
{
    public StringListValueBinder( Binder parent, Class<?> type, XBRBindingContext context )
    {
        super( parent, type, context );
    }

    @Override
    public void generate( XmlRpcListener listener, Object value, Map<Class<?>, Mapping<?>> recipes )
            throws XmlRpcException
    {
        if ( value == null )
        {
            listener.value( null, ValueType.NIL );
            return;
        }
        else if ( value instanceof Collection<?> )
        {
            listener.value( StringUtils.join( ((Collection<?>) value ), " " ), ValueType.STRING );
        }
        else
        {
            listener.value( value.toString(), ValueType.STRING );
        }
    }

    @Override
    protected ValueType getResultType( Object v, ValueType t )
            throws XmlRpcException
    {
        return ValueType.ARRAY;
    }

    @Override
    public Object getResult( Object value, ValueType type )
            throws XmlRpcException
    {
        if ( value == null )
        {
            return null;
        }

        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Splitting value into array: '{}' (original type: {})", value.toString().trim(),
                      value.getClass().getSimpleName() );
        List<String> parts = new ArrayList<>();

        if ( value instanceof Collection )
        {
            logger.trace( "Iterating collection elements and adding to parts..." );
            ( (Collection<?>) value ).forEach( ( val ) -> {
                logger.trace( "Processing collection element: {} (type: {})", val, val.getClass().getSimpleName() );
                if ( val != null )
                {
                    List<String> valParts = Arrays.asList( val.toString().split( "\\s+" ) );
                    logger.trace( "Split collection element into parts: {}", valParts );
                    parts.addAll( valParts );
                }
            } );
        }
        else
        {
            List<String> valParts = Arrays.asList( value.toString().split( "\\s+" ) );
            logger.trace( "Split value into parts: {}", valParts );
            parts.addAll( valParts );
        }

        logger.trace( "Got {} parts:\n  {}", parts.size(), StringUtils.join( parts, "\n  " ) );
        return parts;
    }
}
