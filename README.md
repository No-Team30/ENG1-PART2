# ENG1-PART2
## Description
The Auber (Player) is on a spaceship.
This spaceship is being attacked by rogue dinosaurs (infiltrators)
Both the Auber and Infiltrators have special abilities.
The infiltrators want to destroy all the critical systems of the space station.
The players job is to prevent this, by arresting all infiltrators and saving all the residents (other dinosaurs)

## Game Modes
Once the game has launched, you have 4 options from the main menu:
+ Play (This will launch a new game, that is player controlled)
+ Load (This will attempt to load a previous save game and resume it, WILL CRASH IF NO SAVE FILE EXISTS)
+ Demo (This will launch a new game, controlled completely by the computer)
+ Exit (This will exit the game)

### Demo Mode
This will cause the player to automatically travel around the map, arresting and jailing infiltrators.
Unfortunately it does not allow use of player abilities yet.

Once the game has ended, it will automatically start a new demo mode after 10 seconds.

## HUD
The top left panel shows the status of all systems.
They become red if they have been damaged, and greyed out if the health drops  to zero (sabotaged)

To the right of this is the teleport dropdown

To the right of this is the health bar

The top right, shows the amount of arrested and jailed infiltrators.

The bottom left, shows the status of the players abilities

# Win/Lose Conditions
The player can win by jailing all infiltrators
The player can lose by:
  Having their health reach 0
  All systems being sabotaged

## Player Controls
If a player controlled game is created:
+ ESCAPE - Pauses the game
+ ARROW KEYS - Moves the player in the desired direcetion
+ D - Triggers the reinforced systems ability 
+ F - Triggers the highlight infiltrator ability
+ S - Triggers the slowdown enemies ability
+ A - Toggles the arrest button

## Teleporting
If the player stands on a blue square *INSERT IMAGE*, then they are able to teleport
This is done, by using the dropdown menu, in the top left of the HUD.
Once the player selects the destination, they are teleported to that position.
There are no restrictions on the amount of times this can be done.

## Player Abilities
The reinforced systems ability, is a one time use, that prevents any damage to all systems for 5 seconds
The highlight infiltrator ability, forces the closest infiltrator to flash for 5 seconds. This has a cooldown of 30 seconds.
The slowdown enemies ability, causes all enemies to slow down for 5 seconds. This has a cooldown of 60 seconds.

## Arresting Infiltrators
If the player comes into contact with an infiltrator (and the arrest key has been toggled), the infiltator becomes arrested.
This means the infiltrator follows the player around, until the player jails them.
Multiple infitltrators can be arrested at once

## Jailing Infiltrators
If the player has arrested some infiltrators, there are two ways to jail them:
  By walking to the jail in the top left corner
  Selecting the jail option on a teleporter pad
  
This causes the infiltrator to become locked in the jail
All infiltrators (8) need to be jailed for the player to win.

## Infiltrator Abilities
There are 5 abilities that each infiltrator can have one of:
+ The ability to damage the player, reducing their health
+ Ghost mode - This causes the infiltrator to go invisible for a short period of time
+ Faster Sabotage - This allows the infiltrators to damage systems quicker
+ Slow Down - This can be used to slow down the player for a short period of time
+ Freeze - This causes the player to freeze for a short period of time (triggered on contact)

## Settings Page
This can be found in the pause menu, and allows rebinding of player controls

