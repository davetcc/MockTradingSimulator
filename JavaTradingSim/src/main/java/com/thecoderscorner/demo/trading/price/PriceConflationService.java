package com.thecoderscorner.demo.trading.price;

import com.thecoderscorner.lowlatency.bytestruct.Utf8View;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class PriceConflationService {
    private final ConcurrentMap<Utf8View, StoredPriceMessage> conflatedPrices = new ConcurrentHashMap<>(128);
    private final AtomicLong conflationCount = new AtomicLong(0);

    public void conflatePrice(PriceMessage msg){
//        log.info("Received {} of {}", msg.getTicker().toString(), msg.getTickPrice().asLong());
        var sm = conflatedPrices.get(msg.getTicker());
        if(sm == null) {
            sm = new StoredPriceMessage();
            sm.fromExisting(msg);
            conflatedPrices.put(sm.getPriceMessage().getTicker(), sm);
        } else {
            sm.fromExisting(msg);
        }
        conflationCount.incrementAndGet();
    }

    public long getConflationCount() {
        var toReturn = conflationCount.get();
        conflationCount.set(0);
        return toReturn;
    }

    public boolean receiveChangesIntoList(List<PriceMessage> changedElements) {
        for(var sm : conflatedPrices.values()) {
            if(sm.isChanged()) {
                sm.resetChangeFlag();
                changedElements.add(sm.getPriceMessage());
            }
        }
        return !changedElements.isEmpty();
    }

    static class StoredPriceMessage {
        @Getter
        private final PriceMessage priceMessage = new PriceMessage();
        private final AtomicBoolean changed = new AtomicBoolean(true);

        public void fromExisting(PriceMessage priceMsg) {
            this.priceMessage.copyDataFromAnother(priceMsg);
            changed.set(true);
        }

        public boolean isChanged() {
            return changed.get();
        }

        public void resetChangeFlag() {
            changed.set(false);
        }
    }
}
