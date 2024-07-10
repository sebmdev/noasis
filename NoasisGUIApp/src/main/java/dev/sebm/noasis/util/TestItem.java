package dev.sebm.noasis.util;

import dev.sebm.noasis.jsonresponses.models.FlashCard;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TestItem {
    private final FlashCard correctFlashCard;
    private final FlashCard[] dummyFlashCards;
    @Setter
    private FlashCard answer;

    public TestItem(FlashCard correctFlashCard, FlashCard[] dummyFlashCards) {
        this.correctFlashCard = correctFlashCard;
        this.dummyFlashCards = dummyFlashCards;
    }

    public boolean checkAnswer() {
        return answer.equals(correctFlashCard);
    }
}
