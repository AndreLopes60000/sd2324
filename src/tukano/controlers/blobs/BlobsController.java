package tukano.controlers.blobs;

import static tukano.helpers.Constants.USERS_SERVICE;

import tukano.api.Short;
import tukano.helpers.Result;

import java.net.URI;
import java.util.logging.Logger;


import tukano.api.User;
import tukano.clients.UsersClientFactory;
import tukano.controlers.users.Users;
import tukano.helpers.Discovery;
import tukano.repositories.shorts.ShortsRepository;
import tukano.repositories.shorts.ShortsRepositoryImplementation;
import tukano.resources.rest.RESTShortsResource;


public class BlobsController implements Blobs {

    private static final Discovery discovery = Discovery.getInstance();

    private static Logger Log = Logger.getLogger(RESTShortsResource.class.getName());

    private static ShortsRepository shortsRepository = ShortsRepositoryImplementation.getInstance();

    private Users usersClient;

    public BlobsController(){}

    
    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }


    @Override
    public Result<byte[]> download(String blobId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'download'");
    }


    
}

