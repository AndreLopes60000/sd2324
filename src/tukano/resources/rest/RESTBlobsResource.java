package tukano.resources.rest;

import jakarta.inject.Singleton;
import tukano.api.rest.RestBlobs;
import tukano.controlers.blobs.Blobs;
import tukano.controlers.blobs.BlobsController;

@Singleton
public class RESTBlobsResource extends RESTResource implements RestBlobs{

    private final Blobs impl;

    public RESTBlobsResource(){
        this.impl = new BlobsController();
    }

    @Override
    public void upload(String blobId, byte[] bytes) {
        super.fromJavaResult(impl.upload(blobId, bytes));
    }

    @Override
    public byte[] download(String blobId) {
        return super.fromJavaResult(impl.download(blobId));
    }
    
}
