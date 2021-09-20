package com.globallogic.bcttt;

import java.util.*;
import java.util.stream.Collectors;

public class TopPriceTransactions implements Iterable<Map.Entry<Double, String>> {

    private final SortedMap<Double, String> orderedTxs = new TreeMap<>(Comparator.reverseOrder());

    private final int limit;

    public TopPriceTransactions(int limit) {
        this.limit = limit;
    }

    public boolean add(Double price, String message) {
        if (orderedTxs.size() < limit) {
            orderedTxs.put(price, message);
            return true;
        } else {
            Double leastPrice = orderedTxs.lastKey();
            if (orderedTxs.containsKey(price)) {
                orderedTxs.put(price, message);
                return true;
            } else if (price > leastPrice) {
                orderedTxs.remove(leastPrice);
                orderedTxs.put(price, message);
                return true;
            }
        }
        return false;
    }

    public String toString(int maxLineLength) {
        return "\n    price  message\n" + orderedTxs.entrySet().stream()
                .map(entry -> String.format("%9.2f  %s",
                        entry.getKey(),
                        entry.getValue()
                                .replaceAll("\\s+", "")
                                .substring(0, Math.min(maxLineLength, entry.getValue().length()))))
                .map(line -> line.length() < maxLineLength - 3 ? line : (line + "..."))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return toString(50);
    }

    @Override
    public Iterator<Map.Entry<Double, String>> iterator() {
        return orderedTxs.entrySet().iterator();
    }
}
