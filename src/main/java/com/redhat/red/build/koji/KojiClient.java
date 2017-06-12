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
package com.redhat.red.build.koji;

import com.redhat.red.build.koji.config.KojiConfig;
import com.redhat.red.build.koji.model.ImportFile;
import com.redhat.red.build.koji.model.KojiImportResult;
import com.redhat.red.build.koji.model.json.KojiImport;
import com.redhat.red.build.koji.model.json.util.KojiObjectMapper;
import com.redhat.red.build.koji.model.xmlrpc.KojiArchiveInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiArchiveQuery;
import com.redhat.red.build.koji.model.xmlrpc.KojiArchiveType;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildArchiveCollection;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildQuery;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildState;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildType;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildTypeInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiBuildTypeQuery;
import com.redhat.red.build.koji.model.xmlrpc.KojiIdOrName;
import com.redhat.red.build.koji.model.xmlrpc.KojiImageBuildInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiMavenBuildInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiMavenRef;
import com.redhat.red.build.koji.model.xmlrpc.KojiNVR;
import com.redhat.red.build.koji.model.xmlrpc.KojiPackageInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiPackageQuery;
import com.redhat.red.build.koji.model.xmlrpc.KojiPermission;
import com.redhat.red.build.koji.model.xmlrpc.KojiSessionInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiTagInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiTagQuery;
import com.redhat.red.build.koji.model.xmlrpc.KojiTaskInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiTaskRequest;
import com.redhat.red.build.koji.model.xmlrpc.KojiUploaderResult;
import com.redhat.red.build.koji.model.xmlrpc.KojiUserInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiWinBuildInfo;
import com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcBindery;
import com.redhat.red.build.koji.model.xmlrpc.messages.AckResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.AddPackageToTagRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.AllPermissionsRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.AllPermissionsResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.ApiVersionRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ApiVersionResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.CGInlinedImportRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.CheckPermissionRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ConfirmationResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.CreateTagRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetArchiveTypeRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetArchiveTypeResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetArchiveTypesRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetArchiveTypesResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildByIdOrNameRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildByNVRObjRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetImageBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildTypeRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetBuildTypeResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetImageBuildResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetMavenBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetMavenBuildResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetWinBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetWinBuildResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetTagIdRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetTaskRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetTaskRequestRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetTaskRequestResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.GetTaskResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.IdResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListArchivesRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListArchivesResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListBuildsRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListBuildTypesRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListBuildTypesResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.BuildListResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListPackagesRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListPackagesResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListTaggedRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListTagsRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.ListTagsResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.LoggedInUserRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.LoginRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.LoginResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.LogoutRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.RemovePackageFromTagRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.StatusResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.TagBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.TagRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.TagResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.UntagBuildRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.UploadResponse;
import com.redhat.red.build.koji.model.xmlrpc.messages.UserRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.UserResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.commonjava.maven.atlas.ident.ref.ProjectRef;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.rwx.binding.error.BindException;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.http.RequestModifier;
import org.commonjava.rwx.http.UrlBuildResult;
import org.commonjava.rwx.http.UrlBuilder;
import org.commonjava.rwx.http.httpclient4.HC4SyncObjectClient;
import org.commonjava.util.jhttpc.HttpFactory;
import org.commonjava.util.jhttpc.JHttpCException;
import org.commonjava.util.jhttpc.auth.PasswordManager;
import org.commonjava.util.jhttpc.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.redhat.red.build.koji.model.util.KojiFormats.toKojiName;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.ACCEPT_ENCODING_HEADER;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.ADLER_32_CHECKSUM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.CALL_NUMBER_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.EMBEDDED_ERROR_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.IDENTITY_ENCODING_VALUE;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.METADATA_JSON_FILE;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.SESSION_ID_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.SESSION_KEY_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.SSL_LOGIN_PATH;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.UPLOAD_CHECKSUM_TYPE_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.UPLOAD_DIR_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.UPLOAD_FILENAME_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.UPLOAD_OFFSET_PARAM;
import static com.redhat.red.build.koji.model.xmlrpc.KojiXmlRpcConstants.UPLOAD_OVERWRITE_PARAM;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.http.client.utils.HttpClientUtils.closeQuietly;

/**
 * Created by jdcasey on 12/3/15.
 */
public class KojiClient
        implements Closeable
{
    private HC4SyncObjectClient xmlrpcClient;

    private HttpFactory httpFactory;

    private ExecutorService executorService;

    private ExecutorCompletionService<KojiUploaderResult> uploadService;

    private KojiObjectMapper objectMapper;

    private KojiConfig config;

    private KojiXmlRpcBindery bindery;

    private AtomicInteger callCount = new AtomicInteger( 0 );

    private static final RequestModifier STANDARD_REQUEST_MODIFIER = ( request ) -> {
        request.setHeader( ACCEPT_ENCODING_HEADER, IDENTITY_ENCODING_VALUE );
        Logger logger = LoggerFactory.getLogger( KojiClient.class );
        logger.debug( "\n\n\n\nTarget URI: {}\n\n\n\n", request.getURI() );
    };

    private static final UrlBuilder NO_OP_URL_BUILDER = ( url ) -> new UrlBuildResult( url );

    private UrlBuilder sessionUrlBuilder( KojiSessionInfo session )
    {
        return sessionUrlBuilder( session, null );
    }

    private UrlBuilder sessionUrlBuilder( KojiSessionInfo session, Supplier<Map<String, Object>> paramEditor )
    {
        return ( url ) -> {
            if ( session == null )
            {
                return new UrlBuildResult( url );
            }

            Map<String, String> params = new HashMap<>();
            params.put( SESSION_ID_PARAM, Integer.toString( session.getSessionId() ) );
            params.put( SESSION_KEY_PARAM, session.getSessionKey() );
            params.put( CALL_NUMBER_PARAM, Integer.toString( callCount.getAndIncrement() ) );

            if ( paramEditor != null )
            {
                Map<String, Object> extraParams = paramEditor.get();
                if ( extraParams != null )
                {
                    MalformedURLException error = (MalformedURLException) extraParams.get( EMBEDDED_ERROR_PARAM );
                    if ( error != null )
                    {
                        return new UrlBuildResult( error );
                    }
                    else
                    {
                        extraParams.forEach( ( key, value ) -> {
                            params.put( key, String.valueOf( value ) );
                        } );
                    }
                }
            }

            String result = UrlUtils.buildUrl( url, params );

            Logger logger = LoggerFactory.getLogger( KojiClient.class );
            logger.debug( "\n\n\n\nBuild URL: {}\n\n\n\n", result );
            return new UrlBuildResult( result );
        };
    }

    public KojiClient( KojiConfig config, PasswordManager passwordManager, ExecutorService executorService )
            throws BindException
    {
        this.config = config;
        this.bindery = new KojiXmlRpcBindery();
        this.httpFactory = new HttpFactory( passwordManager );
        this.executorService = executorService;
        setup();
    }

    @Override
    public synchronized void close()
    {
        if ( xmlrpcClient != null )
        {
            xmlrpcClient.close();
            xmlrpcClient = null;
        }
    }

    public void setup()
    {
        uploadService = new ExecutorCompletionService<KojiUploaderResult>( executorService );
        objectMapper = new KojiObjectMapper();

        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.debug( "SETUP: Starting KojiClient for: " + config.getKojiURL() );
        try
        {
            xmlrpcClient = new HC4SyncObjectClient( httpFactory, bindery, config.getKojiSiteConfig() );
        }
        catch ( IOException e )
        {
            logger.error( "Cannot construct koji HTTP site-config: " + e.getMessage(), e );
            xmlrpcClient.close();
            xmlrpcClient = null;
        }

        if ( xmlrpcClient != null )
        {
            try
            {
                ApiVersionResponse response =
                        xmlrpcClient.call( new ApiVersionRequest(), ApiVersionResponse.class, NO_OP_URL_BUILDER,
                                           STANDARD_REQUEST_MODIFIER );

                if ( 1 != response.getApiVersion() )
                {
                    logger.error( "Cannot connect to koji at: " + config.getKojiURL() + ". API Version reported is '"
                                          + response.getApiVersion() + "' but this client only supports version 1." );
                    xmlrpcClient.close();
                    xmlrpcClient = null;
                }
            }
            catch ( XmlRpcException e )
            {
                logger.error(
                        "Cannot retrieve koji API version from: " + config.getKojiURL() + ". (Reason: " + e.getMessage()
                                + ")", e );
                xmlrpcClient.close();
                xmlrpcClient = null;
            }
        }
    }

    public int getApiVersion()
            throws KojiClientException
    {
        checkConnection();

        try
        {
            ApiVersionResponse response =
                    xmlrpcClient.call( new ApiVersionRequest(), ApiVersionResponse.class, NO_OP_URL_BUILDER,
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? -1 : response.getApiVersion();
        }
        catch ( XmlRpcException e )
        {
            throw new KojiClientException( "Cannot retrieve koji API version from: %s. (Reason: %s)", e,
                                           config.getKojiURL(), e.getMessage() );
        }
    }

    public KojiSessionInfo login()
            throws KojiClientException
    {
        checkConnection();

        try
        {
            UrlBuilder urlBuilder = ( url ) -> new UrlBuildResult( UrlUtils.buildUrl( url, SSL_LOGIN_PATH ) );

            RequestModifier requestModifier =
                    ( request ) -> request.setHeader( ACCEPT_ENCODING_HEADER, IDENTITY_ENCODING_VALUE );

            LoginResponse loginResponse =
                    xmlrpcClient.call( new LoginRequest(), LoginResponse.class, urlBuilder, requestModifier );

            if ( loginResponse == null )
            {
                return null;
            }

            KojiSessionInfo session = loginResponse.getSessionInfo();
            setLoggedInUser( session );

            return session;
        }
        catch ( XmlRpcException e )
        {
            throw new KojiClientException( "Failed to login: %s", e, e.getMessage() );
        }
    }

    public <T> T withKojiSession( KojiCustomCommand<T> command )
            throws KojiClientException
    {
        KojiSessionInfo session = null;
        T result = null;
        try
        {
            session = login();
            result = command.execute( session );
        }
        catch ( Exception e )
        {
            Logger logger = LoggerFactory.getLogger( getClass() );
            if ( logger.isDebugEnabled() )
            {
                logger.error( "Koji withSession lambda failed", e );
            }

            if ( e instanceof KojiClientException )
            {
                throw e;
            }
            else
            {
                throw new KojiClientException( "Koji withSession lambda command failed: %s", e, e.getMessage() );
            }
        }
        finally
        {
            logout( session );
        }

        return result;
    }

    private interface KojiInternalCommand<T>
    {
        T execute()
                throws KojiClientException, XmlRpcException;
    }

    private <T> T doXmlRpcAndThrow( KojiInternalCommand<T> cmd, String message, Object... params )
            throws KojiClientException
    {
        checkConnection();

        try
        {
            return cmd.execute();
        }
        catch ( XmlRpcException e )
        {
            throw new KojiClientException( "%s. Reason: %s", e, String.format( message, params ), e.getMessage() );
        }
    }

    private <T> T doXmlRpcAndWarn( KojiInternalCommand<T> cmd, String message, Object... params )
    {
        try
        {
            checkConnection();

            return cmd.execute();
        }
        catch ( XmlRpcException | KojiClientException e )
        {
            Logger logger = LoggerFactory.getLogger( getClass() );
            String formatted = String.format( "%s. Reason: %s", String.format( message, params ), e.getMessage() );
            if ( logger.isDebugEnabled() )
            {
                logger.warn( formatted, e );
            }
            else
            {
                logger.warn( formatted );
            }
        }

        return null;
    }

    public KojiUserInfo getLoggedInUserInfo( String username )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            UserResponse response =
                    xmlrpcClient.call( new UserRequest( username ), UserResponse.class, NO_OP_URL_BUILDER,
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getUserInfo();
        }, "Failed to retrieve current user info." );
    }

    public KojiUserInfo getLoggedInUserInfo( KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            UserResponse response =
                    xmlrpcClient.call( new LoggedInUserRequest(), UserResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            if ( response != null )
            {
                return response == null ? null : response.getUserInfo();
            }

            return null;

        }, "Failed to retrieve current user info." );
    }

    public void logout( KojiSessionInfo session )
    {
        if ( session == null )
        {
            return;
        }

        if ( xmlrpcClient != null )
        {
            try
            {
                StatusResponse response =
                        xmlrpcClient.call( new LogoutRequest(), StatusResponse.class, sessionUrlBuilder( session ),
                                           STANDARD_REQUEST_MODIFIER );

                if ( isNotEmpty( response.getError() ) )
                {
                    Logger logger = LoggerFactory.getLogger( getClass() );
                    logger.error( "Failed to logout from Koji: {}", response.getError() );
                }
            }
            catch ( XmlRpcException e )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.error( String.format( "Failed to logout: %s", e.getMessage() ), e );
            }
        }
    }

    private void checkConnection()
            throws KojiClientException
    {
        if ( xmlrpcClient == null )
        {
            throw new KojiClientException( "Connection to koji at %s is closed. Perhaps it failed to initialize?",
                                           config.getKojiURL() );
        }
    }

    public Set<KojiPermission> getAllPermissions( KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            AllPermissionsResponse response =
                    xmlrpcClient.call( new AllPermissionsRequest(), AllPermissionsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getPermissions();
        }, "Failed to retrieve listing of koji permissions." );
    }

    public boolean hasPermission( String permission, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ConfirmationResponse response =
                    xmlrpcClient.call( new CheckPermissionRequest( permission ), ConfirmationResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? false : response.isSuccess();
        }, "Failed to check whether logged-in user has permission: %s", permission );
    }

    public Integer createTag( CreateTagRequest request, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            IdResponse response = xmlrpcClient.call( request, IdResponse.class, sessionUrlBuilder( session ),
                                                     STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getId();
        }, "Failed to create tag: %s", request );
    }

    public KojiTagInfo getTag( int tagId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            TagResponse response =
                    xmlrpcClient.call( new TagRequest( tagId ), TagResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getTagInfo();
        }, "Failed to retrieve tag: %s", tagId );
    }

    public KojiTagInfo getTag( String tagName, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            TagResponse response =
                    xmlrpcClient.call( new TagRequest( tagName ), TagResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getTagInfo();
        }, "Failed to retrieve tag: %s", tagName );
    }

    public Integer getTagId( String tagName, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            IdResponse response =
                    xmlrpcClient.call( new GetTagIdRequest( tagName ), IdResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getId();
        }, "Failed to retrieve tag: %s", tagName );
    }

    public Integer getPackageId( String packageName, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            IdResponse response = xmlrpcClient.call( new GetTagIdRequest( packageName ), IdResponse.class,
                                                     sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getId();
        }, "Failed to retrieve package: %s", packageName );
    }

    public Map<String, KojiArchiveType> getArchiveTypeMap( KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            GetArchiveTypesResponse response =
                    xmlrpcClient.call( new GetArchiveTypesRequest(), GetArchiveTypesResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            if ( response == null )
            {
                return Collections.emptyMap();
            }

            Map<String, KojiArchiveType> types = new HashMap<>();
            response.getArchiveTypes()
                    .forEach( ( at ) -> at.getExtensions().forEach( ( ext ) -> types.put( ext, at ) ) );


            return types;
        }, "Failed to retrieve list of acceptable archive types" );
    }

    public KojiArchiveType getArchiveType( GetArchiveTypeRequest request, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            GetArchiveTypeResponse response =
                    xmlrpcClient.call( request, GetArchiveTypeResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getArchiveType();
        }, "Failed to retrieve archive type for request: %s", request );
    }

    public KojiImportResult importBuild( KojiImport importInfo, Iterable<Supplier<ImportFile>> importedFileSuppliers,
                                         KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            try
            {
                String dirname = generateUploadDirname( session, importInfo );

                Map<String, KojiClientException> uploadErrors =
                        uploadForImport( null, importedFileSuppliers, dirname, session );

                //            Map<String, KojiClientException> uploadErrors =
                //                    uploadForImport( importInfo, importedFileSuppliers, dirname, session );

                if ( !uploadErrors.isEmpty() )
                {
                    return new KojiImportResult( importInfo ).withUploadErrors( uploadErrors );
                }

                GetBuildResponse response =
                        xmlrpcClient.call( new CGInlinedImportRequest( importInfo, dirname ), GetBuildResponse.class,
                                           sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

                //            StatusResponse response = xmlrpcClient.call( new CGUploadedImportRequest( dirname ), StatusResponse.class,
                //                                                         sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

//                if ( !isEmpty( response.getError() ) )
//                {
//                    throw new KojiClientException( "Error response from Koji server: %s", response.getError() );
//                }

//                KojiBuildInfo build = getBuildInfo( importInfo.getBuildNVR(), session );

                return new KojiImportResult( importInfo ).withBuildInfo( response.getBuildInfo() );
            }
            catch ( RuntimeException e )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.error( "FAIL: " + e.getMessage(), e );
                throw e;
            }
        }, "Failed to execute content-generator import" );
    }

    public List<KojiBuildType> listBuildTypes( KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            ListBuildTypesResponse response =
                    xmlrpcClient.call( new ListBuildTypesRequest(), ListBuildTypesResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiBuildType> types = response.getBuildTypes();
            return types == null ? Collections.emptyList() : types;
        }, "Failed to retrieve list of available build types" );
    }

    public List<KojiBuildType> listBuildTypes( KojiBuildTypeQuery query, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            ListBuildTypesResponse response =
                    xmlrpcClient.call( new ListBuildTypesRequest( query ), ListBuildTypesResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiBuildType> types = response.getBuildTypes();
            return types == null ? Collections.emptyList() : types;
        }, "Failed to retrieve list of available build types for build type query: %s", query );
    }

    public List<KojiBuildInfo> listBuilds( KojiBuildQuery query, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            BuildListResponse response =
                    xmlrpcClient.call( new ListBuildsRequest( query ),
                                       BuildListResponse.class, sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiBuildInfo> builds = response.getBuilds();
            return builds == null ? Collections.emptyList() : builds;
        }, "Failed to retrieve list of builds for build query: %s", query );

    }

    public List<KojiTagInfo> listAllTags( KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListTagsResponse response =
                    xmlrpcClient.call( new ListTagsRequest(), ListTagsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiTagInfo> tags = response.getTags();
            return tags == null ? Collections.emptyList() : tags;
        }, "Failed to retrieve list of all tags" );
    }

    public List<KojiTagInfo> listTags( KojiBuildInfo buildInfo, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListTagsResponse response =
                    xmlrpcClient.call( new ListTagsRequest( new KojiTagQuery( buildInfo ) ), ListTagsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiTagInfo> tags = response.getTags();
            return tags == null ? Collections.emptyList() : tags;
        }, "Failed to retrieve list of tags for build: %s", buildInfo );
    }

    public List<KojiTagInfo> listTags( KojiNVR nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListTagsResponse response =
                    xmlrpcClient.call( new ListTagsRequest( new KojiTagQuery( nvr ) ), ListTagsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiTagInfo> tags = response.getTags();
            return tags == null ? Collections.emptyList() : tags;
        }, "Failed to retrieve list of tags for build: %s", nvr );
    }

    public List<KojiTagInfo> listTags( String nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListTagsResponse response =
                    xmlrpcClient.call( new ListTagsRequest( new KojiTagQuery( nvr ) ), ListTagsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiTagInfo> tags = response.getTags();
            return tags == null ? Collections.emptyList() : tags;
        }, "Failed to retrieve list of tags for build: %s", nvr );
    }

    public List<KojiTagInfo> listTags( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListTagsResponse response =
                    xmlrpcClient.call( new ListTagsRequest( new KojiTagQuery( buildId ) ), ListTagsResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiTagInfo> tags = response.getTags();
            return tags == null ? Collections.emptyList() : tags;
        }, "Failed to retrieve list of tags for build: %s", buildId );
    }

    public List<KojiArchiveInfo> listArchives( KojiArchiveQuery query, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            ListArchivesResponse response = xmlrpcClient.call( new ListArchivesRequest( query ),
                                                               ListArchivesResponse.class, sessionUrlBuilder( session ),
                                                               STANDARD_REQUEST_MODIFIER );

            List<KojiArchiveInfo> archives = response.getArchives();
            return archives == null ? Collections.emptyList() : archives;
        }, "Failed to retrieve list of artifacts matching archive query: %s", query );
    }

    public List<KojiArchiveInfo> listMavenArchivesMatching( String groupId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            ListArchivesResponse response = xmlrpcClient.call( new ListArchivesRequest(
                                                                       new KojiArchiveQuery().withMavenRef(
                                                                               new KojiMavenRef().withGroupId( groupId ) ) ),
                                                               ListArchivesResponse.class, sessionUrlBuilder( session ),
                                                               STANDARD_REQUEST_MODIFIER );

            List<KojiArchiveInfo> archives = response.getArchives();
            return archives == null ? Collections.emptyList() : archives;
        }, "Failed to retrieve list of Maven archives matching groupId: %s", groupId );
    }

    public List<KojiArchiveInfo> listMavenArchivesMatching( String groupId, String artifactId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            ListArchivesResponse response = xmlrpcClient.call( new ListArchivesRequest(
                                                                       new KojiArchiveQuery().withMavenRef(
                                                                               new KojiMavenRef().withGroupId( groupId ).withArtifactId( artifactId ) ) ),
                                                               ListArchivesResponse.class, sessionUrlBuilder( session ),
                                                               STANDARD_REQUEST_MODIFIER );

            List<KojiArchiveInfo> archives = response.getArchives();
            return archives == null ? Collections.emptyList() : archives;
        }, "Failed to retrieve list of Maven archives matching: %s:%s", groupId, artifactId );
    }

    public List<KojiArchiveInfo> listArchivesMatching( ProjectRef ga, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            ListArchivesResponse response =
                    xmlrpcClient.call( new ListArchivesRequest( ga ), ListArchivesResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            List<KojiArchiveInfo> archives = response.getArchives();
            return archives == null ? Collections.emptyList() : archives;
        }, "Failed to retrieve list of archives for: %s", ga );
    }

    public List<KojiBuildArchiveCollection> listArchivesForBuilds( ProjectRef ga, KojiBuildState state,
                                                                   KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            BuildListResponse buildsResponse =
                    xmlrpcClient.call( new ListBuildsRequest( new KojiBuildQuery( ga ).withState( state ) ),
                                       BuildListResponse.class, sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            if ( buildsResponse == null )
            {
                return Collections.emptyList();
            }

            List<KojiBuildArchiveCollection> builds = new ArrayList<>();
            buildsResponse.getBuilds().forEach( ( build ) -> {
                KojiBuildArchiveCollection collection = listArchivesForBuild( build, session );
                if ( collection != null )
                {
                    builds.add( collection );
                }
            } );

            return builds;

        }, "Failed to retrieve list of archives for: %s", ga );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( KojiNVR nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return listArchivesForBuild( new GetBuildByNVRObjRequest( nvr ), session );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( KojiIdOrName build, KojiSessionInfo session )
            throws KojiClientException
    {
        return listArchivesForBuild( new GetBuildByIdOrNameRequest( build ), session );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( String nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return listArchivesForBuild( new GetBuildByIdOrNameRequest( nvr ), session );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        return listArchivesForBuild( new GetBuildByIdOrNameRequest( buildId ), session );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( GetBuildRequest request, KojiSessionInfo session )
            throws KojiClientException
    {
        KojiBuildInfo build = doXmlRpcAndThrow( () -> {
            GetBuildResponse buildResponse =
                    xmlrpcClient.call( request, GetBuildResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            if ( buildResponse == null )
            {
                throw new KojiClientException( "No such build for request: %s", request );
            }

            return buildResponse.getBuildInfo();
        }, "Failed to retrieve build for: %s", request );

        return listArchivesForBuild( build, session );
    }

    public KojiBuildArchiveCollection listArchivesForBuild( final KojiBuildInfo build, final KojiSessionInfo session )
    {
        ListArchivesResponse archivesResponse = doXmlRpcAndWarn( () -> xmlrpcClient.call(
                new ListArchivesRequest( new KojiArchiveQuery().withBuildId( build.getId() ) ),
                ListArchivesResponse.class, sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER ),
                                                                 "Failed to retrieve archives for build: '%s'",
                                                                 build.getNvr() );

        if ( archivesResponse != null )
        {
            List<KojiArchiveInfo> archives = archivesResponse.getArchives();
            return new KojiBuildArchiveCollection( build, archives );
        }

        return null;
    }

    public List<KojiBuildInfo> listBuildsContaining( ProjectVersionRef gav, KojiSessionInfo session )
            throws KojiClientException
    {
        List<KojiArchiveInfo> archives = doXmlRpcAndThrow( () -> {
            ListArchivesResponse response =
                    xmlrpcClient.call( new ListArchivesRequest( gav ), ListArchivesResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? Collections.emptyList() : response.getArchives();
        }, "Failed to retrieve list of archives for: %s", gav );

        List<KojiBuildInfo> builds = new ArrayList<>();

        if ( archives != null && !archives.isEmpty())
        {
            archives.forEach( ( archive ) -> {
                KojiBuildInfo build = doXmlRpcAndWarn( () -> {
                    GetBuildResponse buildResponse =
                            xmlrpcClient.call( new GetBuildByIdOrNameRequest( archive.getBuildId() ),
                                               GetBuildResponse.class, sessionUrlBuilder( session ),
                                               STANDARD_REQUEST_MODIFIER );

                    return buildResponse == null ? null : buildResponse.getBuildInfo();
                }, "Failed to retrieve build for: %s", archive.getBuildId() );

                if ( build != null )
                {
                    builds.add( build );
                }
            } );
        }

        return builds;
    }

    public List<KojiPackageInfo> listPackagesForTag( String tag, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            IdResponse r =
                    xmlrpcClient.call( new GetTagIdRequest( tag ), IdResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            if ( r == null || r.getId() == null )
            {
                throw new KojiClientException( "No such tag: %s", tag );
            }

            ListPackagesResponse response =
                    xmlrpcClient.call( new ListPackagesRequest( new KojiPackageQuery().withTagId( r.getId() ) ),
                                       ListPackagesResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            return response == null ? Collections.emptyList() : response.getPackages();
        }, "Failed to retrieve package list for tag: %s", tag );
    }

    public boolean addPackageToTag( String tag, String pkg, KojiSessionInfo session )
            throws KojiClientException
    {
        return addPackageToTag( tag, pkg, null, session );
    }

    public boolean addPackageToTag( String tag, ProjectRef ga, KojiSessionInfo session )
            throws KojiClientException
    {
        return addPackageToTag( tag, toKojiName( ga ), null, session );
    }

    public boolean addPackageToTag( String tag, ProjectRef gav, String ownerName, KojiSessionInfo session )
            throws KojiClientException
    {
        return addPackageToTag( tag, toKojiName( gav ), ownerName, session );
    }

    public boolean addPackageToTag( String tag, String pkg, String ownerName, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            setLoggedInUser( session );

            IdResponse r =
                    xmlrpcClient.call( new GetTagIdRequest( tag ), IdResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            if ( r == null || r.getId() == null )
            {
                throw new KojiClientException( "No such tag: %s", tag );
            }

            ListPackagesResponse listPackagesResponse =
                    xmlrpcClient.call( new ListPackagesRequest( new KojiPackageQuery().withTagId( r.getId() ).withUserId( session.getUserInfo().getUserId() ) ),
                                       ListPackagesResponse.class, sessionUrlBuilder( session ),
                                       STANDARD_REQUEST_MODIFIER );

            boolean add = true;
            if ( listPackagesResponse != null )
            {
                List<KojiPackageInfo> packages = listPackagesResponse.getPackages();
                if ( packages.parallelStream()
                             .filter( ( info ) -> info.getPackageName().equals( pkg ) )
                             .findFirst()
                             .isPresent() )
                {
                    add = false;
                }
            }
            else
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.debug( "List-packages for tag: {} returned null result!", tag );
            }

            if ( add )
            {
                String owner = ownerName;
                if ( isEmpty( owner ) )
                {
                    owner = session.getUserInfo().getUserName();
                }

                xmlrpcClient.call( new AddPackageToTagRequest( r.getId(), pkg, owner ), AckResponse.class,
                                   sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

                return true;
            }

            return false;

        }, "Failed to retrieve package list for tag: %s", tag );
    }

    private void setLoggedInUser( KojiSessionInfo session )
            throws KojiClientException
    {
        if ( session.getUserInfo() == null )
        {
            session.setUserInfo( getLoggedInUserInfo( session ) );
        }
    }

    public void removePackageFromTag( String tag, String pkg, KojiSessionInfo session )
            throws KojiClientException
    {
        doXmlRpcAndThrow( () -> {
            xmlrpcClient.call( new RemovePackageFromTagRequest( tag, pkg ), AckResponse.class,
                               sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );
            return null;
        }, "Failed to remove package '%s' from tag '%s'", pkg, tag );
    }

    public Integer tagBuild( String tag, String buildNvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            IdResponse response = xmlrpcClient.call( new TagBuildRequest( tag, buildNvr ), IdResponse.class,
                                                 sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getId();
        }, "Failed to start tagging request for build: %s into tag: %s", buildNvr, tag  );
    }

    public KojiTaskInfo getTaskInfo( int taskId, KojiSessionInfo session )
        throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            GetTaskResponse taskResponse = xmlrpcClient.call( new GetTaskRequest( taskId ), GetTaskResponse.class,
                               sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return taskResponse == null ? null : taskResponse.getTaskInfo();
        }, "Failed to load task info for: %s", taskId );
    }

    public KojiTaskInfo getTaskInfo( int taskId, boolean request, KojiSessionInfo session )
            throws KojiClientException
    {
            return doXmlRpcAndThrow( ()->{
                GetTaskResponse taskResponse = xmlrpcClient.call( new GetTaskRequest( taskId, request ), GetTaskResponse.class,
                                   sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

                return taskResponse == null ? null : taskResponse.getTaskInfo();
            }, "Failed to load task info for: %s", taskId );
    }

    public KojiTaskRequest getTaskRequest( int taskId, KojiSessionInfo session )
            throws KojiClientException
    {
            return doXmlRpcAndThrow( ()->{
                GetTaskRequestResponse taskRequestResponse = xmlrpcClient.call( new GetTaskRequestRequest( taskId ), GetTaskRequestResponse.class,
                                   sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

                return taskRequestResponse == null ? null : taskRequestResponse.getTaskRequest();
            }, "Failed to load task request for: %s", taskId );
    }

    public List<KojiBuildInfo> listTagged( KojiTagInfo tag, KojiSessionInfo session )
            throws KojiClientException
    {
        return listTagged( tag.getName(), session );
    }

    public List<KojiBuildInfo> listTagged( String tagName, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()-> {
            BuildListResponse buildsResponse =
                    xmlrpcClient.call( new ListTaggedRequest( tagName ), BuildListResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            if ( buildsResponse == null )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.debug( "No builds response was returned!" );
                return Collections.emptyList();
            }

            List<KojiBuildInfo> builds = buildsResponse.getBuilds();
            if ( builds == null || builds.isEmpty() )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.debug( "No builds tagged in: '{}'", tagName );
                return Collections.emptyList();
            }

            return builds;
        }, "Failed to list builds tagged in: %s", tagName );
    }

    public boolean untagBuild( String tag, String buildNvr, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( ()->{
            BuildListResponse buildsResponse =
                    xmlrpcClient.call( new ListTaggedRequest( tag ).withPrefix( buildNvr ), BuildListResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            if ( buildsResponse == null )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.debug( "No builds response was returned!" );
                return false;
            }

            List<KojiBuildInfo> builds = buildsResponse.getBuilds();
            if ( builds == null || builds.isEmpty() )
            {
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.debug( "Build: '{}' is not tagged in: '{}'", buildNvr, tag );
                return false;
            }

            xmlrpcClient.call( new UntagBuildRequest( tag, buildNvr ), AckResponse.class, sessionUrlBuilder( session ),
                               STANDARD_REQUEST_MODIFIER );

            return true;
        }, "Failed to untag build: %s from: %s", buildNvr, tag );
    }

    public KojiBuildInfo getBuildInfo( KojiNVR nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        KojiBuildInfo buildInfo = doXmlRpcAndThrow( () -> {
            GetBuildResponse response = xmlrpcClient.call( new GetBuildByNVRObjRequest( nvr ), GetBuildResponse.class,
                                                           sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getBuildInfo();
        }, "Failed to retrieve build info for: %s", nvr );

        return withBuildTypeInfo( buildInfo, session );
    }

    public KojiBuildInfo getBuildInfo( String nvr, KojiSessionInfo session )
            throws KojiClientException
    {
        KojiBuildInfo buildInfo = doXmlRpcAndThrow( () -> {
            GetBuildResponse response = xmlrpcClient.call( new GetBuildByIdOrNameRequest( nvr ), GetBuildResponse.class,
                                                           sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getBuildInfo();
        }, "Failed to retrieve build info for: %s", nvr );

        return withBuildTypeInfo( buildInfo, session );
    }

    public KojiBuildInfo getBuildInfo( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        KojiBuildInfo buildInfo = doXmlRpcAndThrow( () -> {
            GetBuildResponse response =
                    xmlrpcClient.call( new GetBuildByIdOrNameRequest( buildId ), GetBuildResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getBuildInfo();
        }, "Failed to retrieve build info for: %s", buildId );

        return withBuildTypeInfo( buildInfo, session );
    }

    public KojiBuildTypeInfo getBuildTypeInfo(int buildId, KojiSessionInfo session)
            throws KojiClientException
    {
        KojiBuildTypeInfo buildTypeInfo = doXmlRpcAndThrow( () -> {
            GetBuildTypeResponse response =
                    xmlrpcClient.call( new GetBuildTypeRequest( buildId ), GetBuildTypeResponse.class,
                                       sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getBuildTypeInfo();
        }, "Failed to retrieve build info for: %s", buildId );

        return buildTypeInfo;
    }

    private KojiBuildInfo withBuildTypeInfo( KojiBuildInfo buildInfo, KojiSessionInfo session )
            throws KojiClientException
    {
        if ( buildInfo != null )
        {
            KojiBuildTypeInfo buildTypeInfo = getBuildTypeInfo(buildInfo.getId(), session);

            if ( buildTypeInfo != null )
            {
                buildInfo = buildTypeInfo.addBuildTypeInfo(buildInfo);
            }
        }

        return buildInfo;
    }

    public KojiMavenBuildInfo getMavenBuildInfo( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            GetMavenBuildResponse response =
                    xmlrpcClient.call( new GetMavenBuildRequest( buildId ), GetMavenBuildResponse.class,
                            sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getMavenBuildInfo();
        }, "Failed to retrieve maven build info for: %s", buildId );
    }

    public KojiWinBuildInfo getWinBuildInfo( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            GetWinBuildResponse response =
                    xmlrpcClient.call( new GetWinBuildRequest( buildId ), GetWinBuildResponse.class,
                            sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getWinBuildInfo();
        }, "Failed to retrieve win build info for: %s", buildId );
    }

    public KojiImageBuildInfo getImageBuildInfo( int buildId, KojiSessionInfo session )
            throws KojiClientException
    {
        return doXmlRpcAndThrow( () -> {
            GetImageBuildResponse response =
                    xmlrpcClient.call( new GetImageBuildRequest( buildId ), GetImageBuildResponse.class,
                            sessionUrlBuilder( session ), STANDARD_REQUEST_MODIFIER );

            return response == null ? null : response.getImageBuildInfo();
        }, "Failed to retrieve image build info for: %s", buildId );
    }

    protected String generateUploadDirname( KojiSessionInfo session, KojiImport importInfo )
            throws KojiClientException
    {
        setLoggedInUser( session );

        return String.format( "kojiji-upload/%s-%s-%s-%s/", new SimpleDateFormat( "yyyyMMdd-HHmmssSSS" ).format( new Date() ),
                              importInfo.getBuildNVR().getName(), importInfo.getBuildNVR().getVersion(), session.getUserInfo().getUserName() );
    }

    protected Map<String, KojiClientException> uploadForImport( KojiImport buildInfo,
                                                                Iterable<Supplier<ImportFile>> uploadedFileSuppliers,
                                                                String dirname, KojiSessionInfo session )
            throws KojiClientException
    {
        AtomicInteger count = new AtomicInteger( 0 );

        if ( buildInfo != null )
        {
            // there are two ways to call CGImport: with the metadata uploaded (here), and with it inlined in the request.
            // if buildInfo is null, we're using the second approach.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try
            {
                objectMapper.writeValue( baos, buildInfo );
            }
            catch ( IOException e )
            {
                throw new KojiClientException( "Failed to serialize import info to JSON. Reason: %s", e, e.getMessage() );
            }

            byte[] data = baos.toByteArray();
            uploadService.submit(
                    newUploader( ()->new ImportFile( METADATA_JSON_FILE, new ByteArrayInputStream( data ), data.length ),
                                 dirname, session ) );

            count.incrementAndGet();
        }

        uploadedFileSuppliers.forEach( ( importFileSupplier ) -> {
            uploadService.submit( newUploader( importFileSupplier, dirname, session ) );
            count.incrementAndGet();
        } );

        Logger logger = LoggerFactory.getLogger( getClass() );
        Map<String, KojiClientException> uploadErrors = new HashMap<>();
        Set<UploadResponse> responses = new HashSet<>();
        int total = count.get();
        do
        {
            logger.debug( "Waiting for %d uploads.", count.get() );

            try
            {
                Future<KojiUploaderResult> future = uploadService.take();
                KojiUploaderResult result = future.get();
                KojiClientException error = result.getError();
                if ( error != null )
                {
                    uploadErrors.put( result.getUploadFilePath(), error );
                }
                else
                {
                    responses.add( result.getResponse() );
                }
            }
            catch ( InterruptedException e )
            {
                logger.debug( "Interrupted while uploading. Aborting upload." );
                break;
            }
            catch ( ExecutionException e )
            {
                throw new KojiClientException( "Failed to execute %d uploads for: %s. Reason: %s", e, total, buildInfo,
                                               e.getMessage() );
            }
        }
        while ( count.decrementAndGet() > 0 );

        return uploadErrors;
    }

    protected Callable<KojiUploaderResult> newUploader( Supplier<ImportFile> importFileSupplier, String dirname, KojiSessionInfo session )
    {
        return () -> {

            ImportFile importFile = importFileSupplier.get();
            KojiUploaderResult result = new KojiUploaderResult( importFile );

            try
            {
                result.setResponse( upload( importFile.getStream(), importFile.getFilePath(), importFile.getSize(), dirname, session ) );
            }
            catch ( KojiClientException e )
            {
                result.setError( e );
            }

            return result;
        };
    }

    protected UploadResponse upload( InputStream stream, String filepath, long size, String uploadDir, KojiSessionInfo session )
            throws KojiClientException
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try
        {
            client = httpFactory.createClient( config.getKojiSiteConfig() );

            String url = sessionUrlBuilder( session, () -> {
                Map<String, Object> params = new HashMap<>();

                try
                {
                    params.put( UPLOAD_DIR_PARAM, encodeParam( UPLOAD_DIR_PARAM, uploadDir ) );
                    params.put( UPLOAD_CHECKSUM_TYPE_PARAM, ADLER_32_CHECKSUM );
                    params.put( UPLOAD_FILENAME_PARAM, encodeParam( UPLOAD_FILENAME_PARAM, filepath ) );
                    params.put( UPLOAD_OFFSET_PARAM, Integer.toString( 0 ) );
                    params.put( UPLOAD_OVERWRITE_PARAM, Integer.toString( 1 ) );
                }
                catch ( MalformedURLException e )
                {
                    params.put( EMBEDDED_ERROR_PARAM, e );
                }

                return params;
            } ).buildUrl( config.getKojiURL() ).throwError().get();

            HttpPost request = new HttpPost( url );
            request.setEntity( new InputStreamEntity( stream, size, ContentType.APPLICATION_OCTET_STREAM ) );

            response = client.execute( request );

            if ( response.getStatusLine().getStatusCode() == 200 )
            {
                return bindery.parse( response.getEntity().getContent(), UploadResponse.class );
            }
            else
            {
                throw new KojiClientException( "Failed to upload: %s to dir: %s. Server response: %s", filepath,
                                               uploadDir, response.getStatusLine() );
            }
        }
        catch ( IOException | JHttpCException | XmlRpcException e )
        {
            throw new KojiClientException( "Failed to upload: %s to dir: %s. Reason: %s", e, filepath, uploadDir,
                                           e.getMessage() );
        }
        finally
        {
            closeQuietly( response );
            closeQuietly( client );

        }
    }

    private String encodeParam( String param, String value )
            throws MalformedURLException
    {
        try
        {
            return URLEncoder.encode( value, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            String msg = String.format( "Failed to encode %s parameter: %s. Reason: %s", param, value, e.getMessage() );

            Logger logger = LoggerFactory.getLogger( getClass() );
            logger.error( msg, e );

            throw new MalformedURLException( msg );
        }
    }
}
