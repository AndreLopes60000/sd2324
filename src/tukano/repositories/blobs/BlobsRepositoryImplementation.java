package tukano.repositories.blobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static tukano.helpers.Constants.*;

public class BlobsRepositoryImplementation  implements BlobsRepository{

    private static BlobsRepository singleton;

    public static synchronized BlobsRepository getInstance(){
        if (singleton == null){
            singleton = new BlobsRepositoryImplementation();
        }
        return singleton;
    }

    @Override
    public void upload(String blobId, byte[] bytes) {
        File file = new File(BLOBS_DIRECTORY,blobId);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] download(String blobId) {
        File file = new File(BLOBS_DIRECTORY+blobId);
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
    
}
