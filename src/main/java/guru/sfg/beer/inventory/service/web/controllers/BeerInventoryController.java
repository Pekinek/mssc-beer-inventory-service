package guru.sfg.beer.inventory.service.web.controllers;

import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.beer.inventory.service.web.mappers.BeerInventoryMapper;
import com.mmocek.commons.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by jt on 2019-05-31.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerInventoryController {

    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerInventoryMapper beerInventoryMapper;

    @GetMapping("api/v1/beer/{beerId}/inventory")
    public List<BeerInventoryDto> listBeersById(@PathVariable UUID beerId){
        log.debug("Finding Inventory for beerId: " + beerId);

        return beerInventoryRepository.findAllByBeerId(beerId)
                .stream()
                .map(beerInventoryMapper::beerInventoryToBeerInventoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("api/v1/beerUpc/{beerUpc}/inventory")
    public List<BeerInventoryDto> listBeersByUpc(@PathVariable String beerUpc){
        log.debug("Finding Inventory for beerUpc: " + beerUpc);

        return beerInventoryRepository.findAllByUpc(beerUpc)
                                      .stream()
                                      .map(beerInventoryMapper::beerInventoryToBeerInventoryDto)
                                      .collect(Collectors.toList());
    }
}
