package providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;

@Provider
@Consumes({ "application/x-protobuf", "application/json" })
@Produces({ "application/x-protobuf", "application/json" })
public class ProtoBufProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message> {
	private static Printer jsonPrinter = JsonFormat.printer().omittingInsignificantWhitespace();
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public void writeTo(Message t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
			entityStream.write(jsonPrinter.print(t).getBytes(StandardCharsets.UTF_8));
		} else {
			t.writeTo(entityStream);
		}
		
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
			try {
				Method newBuilder = type.getMethod("newBuilder");
				final Message.Builder messageBuilder = (Message.Builder) newBuilder.invoke(type);
				JsonFormat.parser().merge(new InputStreamReader(entityStream, StandardCharsets.UTF_8), messageBuilder);
				return messageBuilder.build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Method parseFromMethod = type.getMethod("parseFrom", InputStream.class);
				return (Message) parseFromMethod.invoke(null, entityStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public long getSize(Message t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
			try {
				final String formatted = jsonPrinter.print(t);
				return formatted.getBytes(StandardCharsets.UTF_8).length;
			} catch (InvalidProtocolBufferException e) {
				return -1;
			}
		}
		return t.getSerializedSize();
	}
	
}

