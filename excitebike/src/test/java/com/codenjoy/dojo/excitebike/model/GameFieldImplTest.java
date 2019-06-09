package com.codenjoy.dojo.excitebike.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.excitebike.model.items.bike.Bike;
import com.codenjoy.dojo.excitebike.services.parse.MapParserImpl;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameFieldImplTest {

    private GameFieldImpl game;
    private Bike bike;
    private Dice dice;
    private EventListener listener;
    private Player player;
    private PrinterFactory printer = new PrinterFactoryImpl();

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void dice(int... ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    private void givenFl(String board) {
        MapParserImpl parser = new MapParserImpl(board);
        Bike bike = parser.getBikes().get(0);

        game = new GameFieldImpl(parser, dice);
        listener = mock(EventListener.class);
        player = new Player(listener);
        game.newGame(player);
        player.setHero(bike);
        bike.init(game);
        this.bike = game.getBikes().get(0);
    }

    private void assertE(String expected) {
        assertEquals(TestUtils.injectN(expected),
                printer.getPrinter(game.reader(), player).print());
    }

    @Test
    public void shouldFieldAtStart() {
        givenFl("■■■■■" +
                " o ▼ " +
                "  » ░" +
                " ▲ ▒ " +
                "■■■■■");

        assertE("■■■■■" +
                " o ▼ " +
                "  » ░" +
                " ▲ ▒ " +
                "■■■■■");
    }

    @Test
    public void shouldShiftTrack() {
        givenFl("■■■■■" +
                " o ▼ " +
                "  » ░" +
                " ▲ ▒ " +
                "■■■■■");

        game.tick();

        assertE("■■■■■" +
                " o▼  " +
                " » ░ " +
                "▲ ▒  " +
                "■■■■■");
    }

    @Test
    public void shouldReplaceShiftableElementToBike() {
        givenFl("■■■■■" +
                " o▼  " +
                "     " +
                "     " +
                "■■■■■");

        game.tick();

        assertE("■■■■■" +
                " o   " +
                "     " +
                "     " +
                "■■■■■");

//        game.tick();
//
//        assertE("■■■■■" +
//                "▼o   " +
//                "     " +
//                "     " +
//                "■■■■■");
    }

    @Test
    public void shouldMoveBikeVertically() {
        givenFl("■■■■■" +
                " o   " +
                "     " +
                "     " +
                "■■■■■");

        bike.down();
        game.tick();

        assertE("■■■■■" +
                "     " +
                " o   " +
                "     " +
                "■■■■■");

        bike.up();
        game.tick();

        assertE("■■■■■" +
                " o   " +
                "     " +
                "     " +
                "■■■■■");
    }

    @Test
    public void shouldInclineBikeToLeftAndRight() {
        givenFl("■■■■■" +
                "     " +
                "  o  " +
                "     " +
                "■■■■■");

        bike.right();
        game.tick();

        assertE("■■■■■" +
                "     " +
                "  )  " +
                "     " +
                "■■■■■");

        bike.left();
        game.tick();

        assertE("■■■■■" +
                "     " +
                "  o  " +
                "     " +
                "■■■■■");

        bike.left();
        game.tick();

        assertE("■■■■■" +
                "     " +
                "  (  " +
                "     " +
                "■■■■■");
    }

}
