package io.gridgo.core.support.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedPromise;
import org.joo.promise4j.impl.JoinedResults;

import io.gridgo.connector.Connector;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.impl.MultipartMessage;

public class JoinProducerTemplate extends AbstractProducerTemplate {

	@Override
	public Promise<Message, Exception> sendWithAck(List<Connector> connectors, Message message) {
		return executeProducerWithMapper(connectors, message, c -> sendWithAck(c, message));
	}

	@Override
	public Promise<Message, Exception> call(List<Connector> connectors, Message message) {
		return executeProducerWithMapper(connectors, message, c -> call(c, message));
	}

	private Promise<Message, Exception> executeProducerWithMapper(List<Connector> connectors, Message message,
			Function<Connector, Promise<Message, Exception>> mapper) {
		var promises = new ArrayList<Promise<Message, Exception>>();
		connectors.stream().map(mapper).forEach(promises::add);
		return JoinedPromise.from(promises).filterDone(this::convertJoinedResult);
	}

	private Message convertJoinedResult(JoinedResults<Message> results) {
		return new MultipartMessage(results);
	}
}
