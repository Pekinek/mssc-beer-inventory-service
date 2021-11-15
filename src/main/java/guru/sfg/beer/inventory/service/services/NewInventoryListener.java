package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.events.NewInventoryEvent;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.beer.inventory.service.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        BeerDto beerDto = event.getBeerDto();

        Optional<BeerInventory> beerInventoryOptional = beerInventoryRepository.findByUpc(beerDto.getUpc());
        beerInventoryOptional.ifPresent(beerInventory -> {
            beerInventory.setQuantityOnHand(beerDto.getQuantityOnHand());
            log.debug("Inventory updated: " + beerInventory.getUpc() + " Quantity: " + beerInventory.getQuantityOnHand());
            beerInventoryRepository.save(beerInventory);
        });
    }
}