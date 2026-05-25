package com.thecoderscorner.demo.trading.price;

import com.thecoderscorner.demo.trading.staticdata.StaticDataService;
import com.thecoderscorner.demo.trading.staticdata.StaticMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.AbstractPreferences;

@Slf4j
public class PriceDistributor {
    private final ScheduledExecutorService executorService;
    private final PriceConflationService conflationService;
    private final ScheduledFuture<?> priceTask;
    List<PriceMessage> priceMessages = new ArrayList<>(256);
    private StaticDataService staticDataService;

    public PriceDistributor(ScheduledExecutorService executorService,
                            PriceConflationService conflationService,
                            StaticDataService staticDataService) {
        this.executorService = executorService;
        this.conflationService = conflationService;
        this.staticDataService = staticDataService;
        priceTask = this.executorService.scheduleAtFixedRate(this::receiveChanges, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        priceTask.cancel(true);
        priceMessages.clear();
    }

    private void receiveChanges() {
        priceMessages.clear();
        if(conflationService.receiveChangesIntoList(priceMessages)) {
            log.info("Received {} price messages for conflation, total updates {}",
                    priceMessages.size(), conflationService.getConflationCount());
            for(var pm : priceMessages) {
                sendPrice(pm);
            }
        }
    }

    private void sendPrice(PriceMessage price) {
        var instrument = staticDataService.getStaticData(price.getTicker());
        if(instrument == null) {
            log.error("Failed to find instrument for ticker {}", price.getTicker());
            return;
        }
        var ticks = price.getTickPrice().asLong();
        var tpp = instrument.getTicksPerPoint();
        var px = String.format("%d.%02d", ticks / tpp, ticks % tpp);
        StringBuilder sb = new StringBuilder(512);
        if(instrument.isBlocked()) sb.append("BLOCKED ");
        if(instrument.isPreMarket()) sb.append("PRE-MARKET ");
        log.info("SEND {} {} {} - {} rx at {} {} {}", price.getTicker(), price.getSource(), sb, px,
                price.getMillisEpoch().asInstant(), instrument.getProductType(), instrument.getTradeableVenue());
    }
}
