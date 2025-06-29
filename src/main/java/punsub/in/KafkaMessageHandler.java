package punsub.in;

import avro.model.OrderProductStock;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

public interface KafkaMessageHandler {

    CompletionStage<Void> handleMessage(Message<OrderProductStock> orderProductStockMessage);
}
