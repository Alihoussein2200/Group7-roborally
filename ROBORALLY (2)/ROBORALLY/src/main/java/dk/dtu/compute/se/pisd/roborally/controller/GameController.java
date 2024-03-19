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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        Player current = board.getCurrentPlayer();
        if (current != null && space.getPlayer() == null) {
            // Move current player to the given space
            current.setSpace(space);

            // Increase the counter of moves
            board.setCounter(board.getCounter() + 1);

            // Set the next player as the current player
            switchToNextPlayer();
        }

    }

    /**
     * Switches the current player to the next player in the sequence.
     */
    private void switchToNextPlayer() {
        int currentNumber = board.getPlayerNumber(board.getCurrentPlayer());
        int nextPlayerNumber = (currentNumber + 1) % board.getPlayersNumber();
        Player next = board.getPlayer(nextPlayerNumber);
        board.setCurrentPlayer(next);
    }

    /**
     *Sets the phase to PROGRAMMING.
     * The current player is set to the first player, and the step is set to 0.
     * The program fields of all players are set to visible, and the card fields of all players are set to visible.
     * @param
     * @return
     * @author S230995
     * @author
     * @author
     * @author
     */

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Generates a random command card.
     * @param
     * @return
     * @autor S230995
     * @autor
     * @autor
     * @autor
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Sets the phase to ACTIVATION.
     *
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Makes the program fields of all players visible.
     * @param register
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Makes the program fields of all players invisible.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes the programs of all players.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes the next step of the programs of all players.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continues the execution of the programs of all players.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes the next step of the programs of all players.
     * @param option
     */
    public void executeOptionAndContinue(Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer != null && option != null) {// Execute the chosen option
            board.setPhase(Phase.ACTIVATION); // Switch back to ACTIVATION phase

            executeNextStepX(option);
            while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
                executeNextStep();
            }
        }

    }

    /**
     * Executes the next step of the programs of all players.
     *
     * @param option
     */
    private void executeNextStepX(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(!command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        assert false;
                    } else {
                        executeCommand(currentPlayer, option);
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the next step of the programs of all players.
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.   getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    } else {
                        executeCommand(currentPlayer, command);
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the given command for the given player.
     * @param player
     * @param command
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case U_TURN:
                    this.UTurn(player);
                    break;
                default:

                    //add counter in this method

            }
        }
    }

    /**
     * Moves the player forward based on their current heading.
     *
     * @param player The player to move. If the player or their heading is null, no action is taken.
     */
    public void moveForward(@NotNull Player player) {
        if (player != null && player.getHeading() != null) {
            // Get the current space and direction of the player
            Space currentSpace = player.getSpace();
            Heading direction = player.getHeading();
            // Calculate the next space based on the player's direction
            Space nextSpace = board.getNeighbour(currentSpace, direction);

            // Check if the next space exists and if there are any walls blocking the way in the player's current direction
            if (nextSpace != null && !currentSpace.getWalls().contains(direction) && !nextSpace.getWalls().contains(direction.next().next())) {
                // If the next space is occupied by another robot, try to push that robot
                if (nextSpace.getPlayer() != null) {
                    try {
                        pushRobot(player, nextSpace.getPlayer());
                    } catch (ImpossibleMoveException e) {
                        // If the push was not successful, do not move the player
                        return;
                    }
                }
                // Move the player to the next space
                player.setSpace(nextSpace);
            }
        }
    }


    /**
     * Fast forwards the player by moving the player three spaces ahead.
     *
     * @param player The player to fast forward. The action is skipped if the player or their heading is null.
     */

   public void fastForward(@NotNull Player player) {
        if (player != null && player.getHeading() != null) {
            for (int i = 0; i < 2; i++) {
                moveForward(player);
            }
        }
    }



    /**
     * Rotates the player's heading to the right.
     *
     * @param player The player to rotate. No action is taken if the player or their heading is null.
     */


    public void turnRight(@NotNull Player player) {
        if(player != null && player.getHeading() != null){
            Heading nextHeading = player.getHeading().next();
            if(nextHeading != null) {
                player.setHeading(nextHeading);
            }
        }
    }

    /**
     * Rotates the player's heading to the left.
     *
     * @param player The player to rotate. No action is taken if the player or their heading is null.
     */


    public void turnLeft(@NotNull Player player) {
        if(player != null && player.getHeading() != null){
            Heading nextHeading = player.getHeading().prev();
            if(nextHeading != null) {
                player.setHeading(nextHeading);
            }
        }
    }

    /**
     * Moves a card from "hand" to "program".
     * @param source
     * @param target
     * @return
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Turn the player 180 degrees.
     * @param player
     */
    public void UTurn(@NotNull Player player) {
        if (player != null && player.getHeading() != null) {
            for (int i = 0; i < 2; i++) {
                turnRight(player);
            }
        }
    }

    /**
     * Pushes a row of robots in the direction of the pushing robot.
     *
     * @param pushing The robot doing the pushing.
     * @param pushed  The robot being pushed.
     */

    public void pushRobot(Player pushing, Player pushed) throws ImpossibleMoveException {
        Space pushingRobotSpace = pushing.getSpace();
        Space pushedRobotSpace = pushed.getSpace();
        Heading direction = pushing.getHeading();

        // Kontrollere om den robot der skal skubbes kan blive skubbet i den retning
        Space nextSpace = board.getNeighbour(pushedRobotSpace, direction);
        if (nextSpace != null) {
            // Kontrollere om der er en wall i den retning der skal skubbes
            if (pushedRobotSpace.getWalls().contains(direction) || nextSpace.getWalls().contains(direction.next().next())) {
                // Hvis der er en wall i den retning der skal skubbes, så kan robotten ikke skubbes
                throw new ImpossibleMoveException(pushing, pushedRobotSpace, direction);
            }
            if (nextSpace.getPlayer() != null) {
                // Hvis er felt er optaget af en anden robot, så prøv at skubbe den robot
                // i samme retning som robotten der skubber
                pushRobot(pushing, nextSpace.getPlayer());
            }
            //Rykker den skubbede robot til det næste felt
            moveToSpace(pushed, nextSpace, direction);
            // rykker den skubbede robot til det næste felt
            pushing.setSpace(pushedRobotSpace);
        } else {
            // Hvis robotten ikke kan skubbes, så kastes en exception
            throw new ImpossibleMoveException(pushing, pushedRobotSpace, direction);
        }
    }

    private void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        Player other = space.getPlayer();

        if (other != null) {
            Space target = board.getNeighbour(space, heading);

            if (target != null) {
                // Check if there is a wall between the current space and the target space
                if (!space.getWalls().contains(heading) && !target.getWalls().contains(heading.next().next())) {
                    moveToSpace(other, target, heading);
                } else {
                    throw new ImpossibleMoveException(player, space, heading);
                }
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }

        player.setSpace(space);
    }






    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
