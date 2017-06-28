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

import com.redhat.red.build.koji.model.json.FileBuildComponent;
import com.redhat.red.build.koji.model.json.RPMBuildComponent;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.redhat.red.build.koji.model.json.KojiJsonConstants.ARCH;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.CHECKSUM;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.CHECKSUM_TYPE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.EPOCH;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.FILE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.FILENAME;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.FILESIZE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.NAME;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.RELEASE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.RPM;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.SIGMD5;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.SIGNATURE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.TYPE;
import static com.redhat.red.build.koji.model.json.KojiJsonConstants.VERSION;
import static org.commonjava.rwx.vocab.ValueType.DOUBLE;
import static org.commonjava.rwx.vocab.ValueType.INT;
import static org.commonjava.rwx.vocab.ValueType.NIL;
import static org.commonjava.rwx.vocab.ValueType.STRING;

/**
 * Handle polymorphism of {@link com.redhat.red.build.koji.model.json.BuildComponent}.
 *
 * Created by jdcasey on 1/14/16.
 */
public class BuildComponentValueBinder
        extends CustomValueBinder
{
    public BuildComponentValueBinder( Binder parent, Class<?> type, XBRBindingContext context )
    {
        super( parent, type, context );
    }

    @Override
    public void generate( XmlRpcListener listener, Object value, Map<Class<?>, Mapping<?>> recipes )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        if ( value instanceof RPMBuildComponent )
        {
            RPMBuildComponent rpm = (RPMBuildComponent) value;
            Map<String, String> map = new HashMap<>();
            map.put( TYPE, RPM );
            map.put( NAME, rpm.getName() );
            map.put( VERSION, rpm.getVersion() );
            map.put( RELEASE, rpm.getRelease() );
            map.put( EPOCH, rpm.getEpoch() );
            map.put( ARCH, rpm.getArch() );
            map.put( SIGMD5, rpm.getSigmd5() );
            map.put( SIGNATURE, rpm.getSignature() );

            fireStructMapEvents( listener, map );
        }
        else if ( value instanceof FileBuildComponent )
        {
            FileBuildComponent file = (FileBuildComponent) value;
            Map<String, Object> map = new HashMap<>();
            map.put( TYPE, FILE );
            map.put( FILENAME, file.getFilename() );
            map.put( FILESIZE, file.getFilesize() );
            map.put( CHECKSUM, file.getChecksum() );
            map.put( CHECKSUM_TYPE, file.getChecksumType() );
            fireStructMapEvents( listener, map );
        }
        else
        {
            throw new XmlRpcException( "Invalid value type: {} for converter: {} (expects: {} or a subclass)",
                                       value.getClass().getName(), getClass().getName(), Date.class.getName() );
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
            if ( v instanceof Long )
            {
                vt = INT;
            }

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
        throw new XmlRpcException( "Parsing not implemented for: %s", value.getClass() );
    }

    @Override
    protected ValueType getResultType( Object v, ValueType t )
            throws XmlRpcException
    {
        return t;
    }
}
