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

import java.util.Collections;
import java.util.List;

public class KojiTaskRequest
{
    private List<Object> request;

    public KojiTaskRequest()
    {
    }

    public List<Object> getRequest()
    {
        if (request == null)
        {
            request = Collections.emptyList();
        }

        return Collections.unmodifiableList(request);
    }

    public void setRequest( List<Object> request )
    {
        this.request = request;
    }

    public KojiTaskRequest withRequest(List<Object> request)
    {
        this.request = request;
        return this;
    }

    public KojiBuildRequest asBuildRequest()
    {
        if (request.size() < 2)
        {
            return null;
        }

        KojiBuildRequest buildRequest = new KojiBuildRequest(request);

        return buildRequest;
    }

    public KojiMavenBuildRequest asMavenBuildRequest()
    {
        if (request.size() < 3)
        {
            return null;
        }

        KojiMavenBuildRequest mavenBuildRequest = new KojiMavenBuildRequest(request);

        return mavenBuildRequest;
    }

    public KojiBuildRequest asBuildRequest(String method)
    {
        if ( method.equals("build") )
        {
            KojiBuildRequest buildRequest = new KojiBuildRequest(request);

            return buildRequest;
        }

        if ( method.equals("maven") )
        {
            KojiMavenBuildRequest mavenBuildRequest = new KojiMavenBuildRequest(request);

            return mavenBuildRequest;
        }

        return null;
    }

    @Override
    public String toString() {
        return "KojiTaskRequest{request=" + request + "}";
    }
}
