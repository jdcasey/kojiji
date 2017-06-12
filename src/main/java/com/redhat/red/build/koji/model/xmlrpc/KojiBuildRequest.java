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
package com.redhat.red.build.koji.model.xmlrpc;

import java.util.List;

public class KojiBuildRequest
{
    protected String source;

    protected String target;

    public KojiBuildRequest( List<Object> request )
    {
        if ( request.size() >= 2 )
        {
            if ( request.get(0) instanceof String )
            {
                this.source = (String) request.get(0);
            }

            if ( request.get(0) instanceof String )
            {
                this.target = (String) request.get(1);
            }
        }
    }

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget( String target )
    {
        this.target = target;
    }

    @Override
    public String toString()
    {
        return "KojiBuildRequest{source=" + source + ", target=" + target + "}";
    }
}
