package tukano.controlers.blobs;

import tukano.helpers.Result;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static tukano.helpers.Constants.*;
import tukano.repositories.blobs.BlobsRepository;
import tukano.repositories.blobs.BlobsRepositoryImplementation;


public class BlobsController implements Blobs {

    private static BlobsRepository blobsRepository = BlobsRepositoryImplementation.getInstance();

    public BlobsController(){}

    
    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        File file = new File(BLOBS_DIRECTORY+blobId);

        // Check if the file exists
        if (!file.exists()) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }
        byte[] oldBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!oldBytes.equals(bytes)){
            return Result.error(Result.ErrorCode.CONFLICT);
        }
        blobsRepository.upload(blobId, bytes);
        
        return Result.ok();

    }


    @Override
    public Result<byte[]> download(String blobId) {
        File file = new File(BLOBS_DIRECTORY+blobId);

        // Check if the file exists
        if (!file.exists()) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        
        return Result.ok(blobsRepository.download(blobId));
      
    }


    
}

