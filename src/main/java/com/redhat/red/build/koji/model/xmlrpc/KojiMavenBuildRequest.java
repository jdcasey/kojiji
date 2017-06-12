package com.redhat.red.build.koji.model.xmlrpc;

import java.util.List;
import java.util.Map;

public class KojiMavenBuildRequest extends KojiBuildRequest
{
    private Map<?, ?> options;

    public KojiMavenBuildRequest(List<Object> request)
    {
        super( request );

        if ( request.size() >= 3)
        {
            if (request.get(2) instanceof Map<?, ?>)
            {
                this.options = (Map<?, ?>) request.get(2);
            }

        }
    }

    public String getScmUrl()
    {
        return getSource();
    }

    public void setScmUrl(String scmUrl)
    {
        setSource(scmUrl);
    }

    public Map<?, ?> getOptions()
    {
        return options;
    }

    public void setOptions(Map<?, ?> options)
    {
        this.options = options;
    }

    @Override
    public String toString()
    {
        return "KojiMavenBuildRequest{source=" + source + ", target=" + target + ", options=" + options + "}";
    }
}
