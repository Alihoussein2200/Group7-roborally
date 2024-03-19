package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class ImpossibleMoveException extends Exception {

    private Player player;
    private Space space;
    private Heading heading;

    /**
     * Constructor for the ImpossibleMoveException
     * @param player
     * @param space
     * @param heading
     */
    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}