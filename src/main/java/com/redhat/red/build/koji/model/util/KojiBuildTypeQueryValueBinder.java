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

import com.redhat.red.build.koji.model.xmlrpc.KojiBuildTypeQuery;

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

import java.util.HashMap;
import java.util.Map;

public class KojiBuildTypeQueryValueBinder
        extends CustomValueBinder
{
    public KojiBuildTypeQueryValueBinder( Binder parent, Class<?> type, XBRBindingContext context )
    {
        super( parent, type, context );
    }

    @Override
    public void generate( XmlRpcListener listener, Object value, Map<Class<?>, Mapping<?>> recipes )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.debug( "Serializing: {}", value );

        if ( value == null )
        {
            logger.debug( "Generating nil" );
            listener.value( null, ValueType.NIL );
            return;
        }
        else if ( value instanceof KojiBuildTypeQuery )
        {
            KojiBuildTypeQuery query = (KojiBuildTypeQuery) value;
            Map<String, Object> map = new HashMap<>(2);

            String name = query.getName();

            if (name != null)
            {
                map.put( "name", name );
            }

            Integer id = query.getId();

            if (id != null)
            {
                map.put( "id", id );

            }

            fireStructMapEvents( listener, map );
        }
        else
        {
            throw new XmlRpcException( "Invalid value type: {} for converter: {} (expects: {} or a subclass)",
                                       value.getClass().getName(), getClass().getName(), KojiBuildTypeQuery.class.getName() );
        }
    }

    private void fireStructMapEvents( XmlRpcListener listener, Map<String, ?> map )
            throws XmlRpcException
    {
        listener.startStruct();

        for ( Map.Entry<String, ?> entry : map.entrySet() )
        {
            String k = entry.getKey();
            Object v = entry.getValue();

            ValueType vt = ValueType.typeFor( v );

            listener.startStructMember( k );
            listener.value( v, vt );
            listener.structMember( k, v, vt );
            listener.endStructMember();
        }

        listener.endStruct();
        listener.value( map, ValueType.STRUCT );
    }

    @Override
    public Object getResult( Object value, ValueType type )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );

        if ( value == null )
        {
            logger.debug("Got nil");
            return null;
        }

        if ( !(value instanceof Map<?, ?>) )
        {
            logger.debug("Got wrong type of object {} (type: {})", value, type);
            return null;
        }

        logger.debug( "Setting value: {} (type: {})", value, type );
        logger.debug( "Setting value on parent: {}", getParent() );

        Map<?, ?> map = (Map<?, ?>) value;

        KojiBuildTypeQuery query = new KojiBuildTypeQuery();

        if (map.containsKey("name"))
        {
            Object name = map.get("name");

            if (name instanceof String) {
                query.setName((String) name);
            }
        }

        if (map.containsKey("id"))
        {
            Object id = map.get("id");

            if (id instanceof Integer) {
                query.setId((int) id);
            }
        }

        return query;
    }

    @Override
    protected ValueType getResultType( Object v, ValueType t )
            throws XmlRpcException
    {
        return t;
    }
}
