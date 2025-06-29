package punsub.in.impl;

import avro.model.OrderProductStock;
import dto.ProductDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import punsub.in.KafkaMessageHandler;
import service.ProductService;
import util.ObjectMapperUtil;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class KafkaMessageHandlerImpl implements KafkaMessageHandler {

    Log log = LogFactory.getLog(KafkaMessageHandlerImpl.class);

    @Inject
    ProductService productService;

    @Incoming("stock-sync")
    @Override
    public CompletionStage<Void> handleMessage(Message<OrderProductStock> orderProductStockEvent) {
        log.info("Descontar stock de la orden: +" + orderProductStockEvent.getPayload().getOrderId());
        List<ProductDto> productDtoList = ObjectMapperUtil.toProductDto(orderProductStockEvent.getPayload());
        return Multi.createFrom().iterable(productDtoList)
                .onItem()
                .transformToUni(it -> productService.discountStockFlow(it))
                .concatenate()
                .collect().asList()
                .onItem().ignore().andContinueWithNull()
                .subscribeAsCompletionStage()
                .thenCompose(result -> orderProductStockEvent.ack());
    }
}


