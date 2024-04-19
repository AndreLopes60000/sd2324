package tukano.clients.rest;

import java.net.URI;
import tukano.helpers.Result;
import java.util.logging.Logger;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import java.util.function.Supplier;
import jakarta.ws.rs.core.GenericType;
import static tukano.helpers.Result.ok;
import static tukano.helpers.Result.error;
import jakarta.ws.rs.ProcessingException;
import tukano.resources.rest.RESTResource;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.WebApplicationException;
import static tukano.helpers.Result.ErrorCode;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class RESTClient extends RESTResource{
    private static Logger Log = Logger.getLogger(RESTClient.class.getName());

	protected static final int READ_TIMEOUT = 5000;
	protected static final int CONNECT_TIMEOUT = 5000;

	protected static final int MAX_RETRIES = 10;
	protected static final int RETRY_SLEEP = 5000;

	final URI serverURI;
	final Client client;
	final ClientConfig config;

	RESTClient(URI serverURI) {
		this.serverURI = serverURI;
		this.config = new ClientConfig();

		config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

		this.client = ClientBuilder.newClient(config);
	}

	protected <T> T reTry(Supplier<T> func) {
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				return func.get();
			} catch (ProcessingException x) {
				System.err.println( x.getMessage() );
				Log.fine("ProcessingException: " + x.getMessage());
				sleep_ms(RETRY_SLEEP);
			}
			catch (WebApplicationException x){
				throw x;
			}
			catch (Exception x) {
				Log.fine("Exception: " + x.getMessage());
				x.printStackTrace();
				break;
			}
		return null;
	}

	protected <T> Result<T> toJavaResult(Response r, GenericType<T> entityType) {
		try {
			var status = r.getStatusInfo().toEnum();
			if (status == Status.OK && r.hasEntity())
				return ok(r.readEntity(entityType));
			else
			if( status == Status.NO_CONTENT) return ok();

			return error(getErrorCodeFrom(status.getStatusCode()));
		} finally {
			r.close();
		}
	}

	protected <T> Result<T> toJavaResult(Response r, Class<T> entityType) {
		try {
			var status = r.getStatusInfo().toEnum();
			if (status == Status.OK && r.hasEntity())
				return ok(r.readEntity(entityType));
			else
			if( status == Status.NO_CONTENT) return ok();

			return error(getErrorCodeFrom(status.getStatusCode()));
		} finally {
			r.close();
		}
	}

	public static ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
			case 200, 209 -> ErrorCode.OK;
			case 409 -> ErrorCode.CONFLICT;
			case 403 -> ErrorCode.FORBIDDEN;
			case 404 -> ErrorCode.NOT_FOUND;
			case 400 -> ErrorCode.BAD_REQUEST;
			case 500 -> ErrorCode.INTERNAL_ERROR;
			case 501 -> ErrorCode.NOT_IMPLEMENTED;
			default -> ErrorCode.INTERNAL_ERROR;
		};
	}

	@Override
	public String toString() {
		return serverURI.toString();
	}

	private void sleep_ms(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
