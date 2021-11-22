package guru.sfg.beer.inventory.service.services;

import com.mmocek.commons.model.AllocateOrderRequest;
import com.mmocek.commons.model.AllocateOrderResult;
import guru.sfg.beer.inventory.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER)
    public void listen(AllocateOrderRequest request) {
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrderDto());

        try{
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrderDto());
            builder.pendingInventory(!allocationResult);
            builder.allocationError(false);
        } catch (Exception e){
            log.error("Allocation failed for order id: " + request.getBeerOrderDto().getId(), e);
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE, builder.build());
    }
}
