package com.kuri01.Game.Screen.EventHandler;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.kuri01.Game.Card.Model.Card;
import com.kuri01.Game.Card.Model.CardSlot;
import com.kuri01.Game.Card.Model.TriPeaksLayout;
import com.kuri01.Game.Screen.GameScreen;

import java.util.List;

public class InputHandler extends InputAdapter {

    private final Camera camera;
    private TriPeaksLayout layout;
    GameScreen gameScreen;

    public InputHandler(Camera camera, TriPeaksLayout layout, GameScreen gameScreen) {
        this.camera = camera;
        this.layout = layout;
        this.gameScreen = gameScreen;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Bildschirmkoordinaten in Weltkoordinaten umwandeln
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        float width = gameScreen.cardWidth;
        float height = gameScreen.cardHeight;

        // Durch alle Karten-Slots gehen
        for (int i = 0; i < layout.getPyramidCards().size(); i++) {
            List<CardSlot> slot = layout.getPyramidCards();
            Card card = slot.get(i).card;

            if (card != null && card.isFaceUp()) {
                float x = slot.get(i).x;
                float y = slot.get(i).y;

                if (worldCoordinates.x >= x && worldCoordinates.x <= x + width &&
                    worldCoordinates.y >= y && worldCoordinates.y <= y + height) {

                    //Klicken auf Karte erkannt

                    Card tmpCard = gameScreen.getTopCardSlot().card;

                    if (tmpCard != null) {
                        if(layout.isPlayable(card, tmpCard)) {
                            // Karte ist spielbar
                            gameScreen.setTopCardSlot(new CardSlot(4, 0, card));
                            slot.get(i).card = null; // Karte vom Spielfeld entfernen
                            layout.isRemoved(i);
                            gameScreen.increaseComboCounter();
                            if (i < 3) {
                                gameScreen.increasePoints(1);
                                gameScreen.increasePoints(1);
                                gameScreen.setPeak(i);
                            } else {
                                gameScreen.increasePoints(1);
                            }
                            return true;
                        }
                        else{
                            gameScreen.decreasePoints(1);
                        }
                    }
                }
            }
        }

        if (gameScreen.getDeckSlot() != null) {
            if (worldCoordinates.x >= gameScreen.getDeckSlot().x && worldCoordinates.x <= gameScreen.getDeckSlot().x + width &&
                worldCoordinates.y >= gameScreen.getDeckSlot().y && worldCoordinates.y <= gameScreen.getDeckSlot().y + height) {

                gameScreen.drawNewCard();
                gameScreen.decreasePoints(1);
                if (gameScreen.remainingCards() == 0) {
                    gameScreen.setDeckSlot(null);
                }

            }
        }


        return true;
    }

    public void setLayout(TriPeaksLayout layout) {
        this.layout = layout;
    }
}
