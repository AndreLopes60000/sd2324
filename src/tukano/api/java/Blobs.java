package tukano.api.java;

import java.util.function.Consumer;

import tukano.helpers.Result;

/**
 * Interface of blob service for storing short videos media ...
 */
public interface Blobs {
	String NAME = "blobs";

	/**
	 * Uploads a short video blob resource. Must validate the blobId to ensure it
	 * was generated by the Shorts service.
	 * 
	 * @param String blobId the identifier generated by the Shorts service for this
	 *               blob
	 * @param bytes  the contents in bytes of the blob resource
	 * 
	 * @return OK(void) if the upload is new or if the blobId and bytes match an
	 *         existing blob;
	 *         CONFLICT if a blobId exists but bytes do not match;
	 *         FORBIDDEN if the blobId is not valid
	 */
	Result<Void> upload(String blobId, byte[] bytes);

	/**
	 * Downloads a short video blob resource in a single byte chunk of bytes.
	 * 
	 * @param blobId the id of the blob;
	 * @return (OK, bytes), if the blob exists;
	 * 			 NOT_FOUND, if no blob matches the provided blobId
	 */
	Result<byte[]> download(String blobId);

	/**
	 * Downloads a short video blob resource as a result suitable for streaming
	 * large-sized byte resources 
	 * 
	 * The default implementation just sinks a single chunk of bytes taken from download(blobId)
	 * 
	 * @param blobId the id of the blob
	 * @param sink - the consumer of the chunks of data
	 * @return (OK,), if the blob exists;
	 *		   NOT_FOUND, if no blob matches the provided blobId
	 */
	default Result<Void> downloadToSink(String blobId, Consumer<byte[]> sink) {
		var res = download(blobId);
		if (!res.isOK())
			return Result.error(res.error());

		sink.accept(res.value());
		return Result.ok();
	}
}
