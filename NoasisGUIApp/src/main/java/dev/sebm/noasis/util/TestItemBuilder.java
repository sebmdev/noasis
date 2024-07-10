package dev.sebm.noasis.util;

import dev.sebm.noasis.jsonresponses.models.FlashCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestItemBuilder {
    private final List<FlashCard> flashCards;

    public TestItemBuilder(List<FlashCard> flashCards) {
        this.flashCards = flashCards;
    }

    public TestItem createItem(FlashCard item) {
        List<FlashCard> copy = new ArrayList<>(flashCards);
        copy.remove(item);
        Collections.shuffle(copy);
        FlashCard[] dummyChoices = new FlashCard[3];
        for (int i = 0; i < 3; i++) {
            dummyChoices[i] = copy.get(i);
        }

        return new TestItem(item, dummyChoices);
    }
}
