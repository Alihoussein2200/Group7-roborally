/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    /**
     * Constructor for the board.
     * @param width
     * @param height
     * @param boardName
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
        initializeWalls();
    }

    /**
     * Constructor for the board.
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    /**
     * Returns the gameid of the board.
     * @return
     */
    public Integer getGameId() {
        return gameId;
    }
/**
     * Sets the gameid of the board.
     * @param gameId
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }
/**
     *get the specific space on the board
     * @return
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }
/**
     *get the specific player number
     * @return
     */
    public int getPlayersNumber() {
        return players.size();
    }
/**
     *add a player to the board
     * @return
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }
/**
     *get the player
     * @return
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }
/**
     *rget the current player
     * @return
     */
    public Player getCurrentPlayer() {
        return current;
    }
/**
     *set the current player
     * @return
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }
/**
     *get the phase
     * @return
     */
    public Phase getPhase() {
        return phase;
    }
/**
     *set the phase
     * @return
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }
/**
     *get the step
     * @return
     */
    public int getStep() {
        return step;
    }
/**
     *set the step
     * @return
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }
/**
     *get the step mode
     * @return
     */
    public boolean isStepMode() {
        return stepMode;
    }
/**
     *set the step mode
     * @return
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }
/**
     *get the player number
     * @return
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    private int counter;
    /**
     *get the counter
     * @return
     */
    public int getCounter(){
        return counter;
    }
/**
     *set the counter
     * @return
     */
    public  void setCounter(int counter){
        this.counter = counter;
        notifyChange();
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }
/**
     *get the status message
     * @return
     */
    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V1 add the move count to the status message
        // XXX: V2 changed the status so that it shows the phase, the current player and the number of steps
        return "Phase = " + getPhase().toString() + ", " +
                "Player = " + getCurrentPlayer().getName() + ", " +
                "Move = " + getCounter();
    }
/**
     *initialize the walls
     * @return
     */
    private void initializeWalls() {
        // Remove all walls
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                getSpace(x, y).getWalls().clear();
            }
        }
        // Add walls to specific spaces
        addWall(1, 5, Heading.WEST);
        addWall(5, 2, Heading.NORTH);
        addWall(4, 0, Heading.SOUTH);
        addWall(6, 2, Heading.EAST);
        addWall(0, 5, Heading.NORTH);
        addWall(5, 2, Heading.SOUTH);
        addWall(2, 5, Heading.EAST);
        addWall(3, 1, Heading.WEST);
        addWall(1, 7, Heading.SOUTH);
        addWall(7, 0, Heading.WEST);
        addWall(3, 6, Heading.NORTH);
        addWall(2, 4, Heading.WEST);
        addWall(6, 4, Heading.EAST);
        addWall(3, 3, Heading.NORTH);
        addWall(4, 7, Heading.WEST);
        addWall(1, 3, Heading.WEST);
        addWall(6, 5, Heading.NORTH);
        addWall(0, 2, Heading.EAST);
        addWall(4, 6, Heading.SOUTH);
        addWall(7, 5, Heading.WEST);

    }

    /**
     * Adds a wall to the specified space in the given direction.
     *
     * @param x         The x-coordinate of the space.
     * @param y         The y-coordinate of the space.
     * @param direction The direction in which to add the wall.
     */
    public void addWall(int x, int y, @NotNull Heading direction) {
        Space space = getSpace(x, y);
        if (space != null) {
            space.getWalls().add(direction);
        }
    }


}




