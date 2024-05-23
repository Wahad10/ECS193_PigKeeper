**<span style="text-decoration:underline;">Week 3</span>**

_Thurs, Jan 25_

With TA:

- React Native (cross platform)

-TA has documentation on how to deploy (for next quarter)

What would our beta would look like at the end of the qtr

- focusing on completing a feature

- not expected to have a full scalable app

- ask what feature it should be

should we start focusing on UI next quarter?

- UI should be focused

- want it to be good looking

- existing libraries? have to investigate for mobile apps

for react native, do we need to require a mac

- no not required

- need a mac to test iOS because xcode

testing

- important as documentation, app gets passed

computer vison:

- ask if that's a minimum requirement

- ta considers it as a stretch goal

after talking to client, next stage:

- what we intend to design

- look at designs of other gaming apps, corporate apps

- system architecture diagrams, show how we plan to design this



<p id="gdcalert1" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image1.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert2">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image1.png "image_tooltip")


code quality:

- each phase has its own file

"home.js" - controls the routing

post, get request

how important it is to have backend for a game that doesn't have a login?

- depends? extra features, stretch goals

- focus on the game

if client just want android, then focus on kotlin

SUMMARY:

GOAL: need to have an app that follows the features that the client wants

Questions to ask: 

what is the goal of this quarter? what is the goal of the next quarter? underpromise

what are the main things you want to keep track of in this game? get a list of features

ask if this client meeting will be recurring?

is this okay? 

With client: 



* What are your expectations, and are there any specific preferences you have?
    * 
* What would a minimum viable product look like?
    * can at least show something onto the android system, whatever platform, at least on android
    * what is the wireframing, what we're going to implement
    * beginning with test cases and the structure
    * getting the tests, documentation, workflow set up, know that we're making a maintainable product
    * _not concerned with making something pretty_
    * identifying stretch type goals
* Is automatic dice recognition a must-have feature in your minimum viable product?
* What are the main things you want to keep track of in this game? What are the features that you would like implemented?
* Should we focus on developing an Android version of the game? 
* What does “sitting” out a game mean for scorekeeping of the entire set?
* What does “by how many points the player won by” mean when determining the overall winner of the set? Does it mean compared to the player in 2nd place? Last place? The average of the other players’ scores?
* When starting a set, is the number of rounds predetermined, or do you keep playing until you “end” the game? (e.g. Start a set of 5 games, 7 games, etc.)
* Does “getting skunked” stack? (e.g. if 2 players get skunked, does it quadruple the points gained by the winner?)
* Do you want to implement a player limit?
* Does losing your turn negate the points earned in that turn?
* When a player reaches 100, the player either wins or another round of turns is played? How do we make that distinction?
* When a player reaches 100 and another round of turns is played, is that the final round of turns regardless of what happens? What if another player reaches 100 in this final round? Does the round still end at the player that first reached 100?
* Which turn special cases override each other? For example, do snake-eyes and boxcars override doubles? What about the three doubles in a row?
* What is the goal of this quarter? (alpha)
* What is the goal of next quarter? (beta)
    * ideal:
        * love to have the phone set up to watch the game
        * keep track of everything
        * recognizing voices, dices
        * person hitting a button saying "yeah" 
    * keeping tally of all games were played and who (users) were in
    * saying who won
    * "points" 
    * want to be able to enter this info quickly, keep track of scores
    * nice to undo
    * one die, two die game options
    * **_being able to select/customize rules in their version of pig_**
        * Default rules (just started for the first time)
            * see proposal: [PigKeeper Tracker Proposal PDF](https://drive.google.com/file/d/1dv5R1mugpXM4g6wCXsUTQCYB2_OXBAKp/view)
        * examples of rules:
            * roll a 7 youre back to where u were
            * roll a doubles other than double ones, it's double the value, and have to roll again
            * roll doubles three times in a row, youre back to zero
                * precedence: triple double > snake eyes
                * something we should figure out and why, how these rules should be ordered
            * snake eyes or box cars, go to zeros
            * if you hit 100 exactly, you go back to zero
            * hit multiple of 100? 
        * when you win:
            * you win the difference from each person
            * e.g: you win double if someone has 0
            * if difference between one person is 10, they owe $10, the other person 50, then 50
        * keep track of if they win and lose and how much by
        * sum how much they win and lose total
        * no predetermined set of rounds
            * be able to let a person sit out a round
            * be able to add or remove (or sit out) members during a session
            * be able to change order if people switch seats
            * order enforced by the game
            * pass button
            * be able to see who's starting
                * roll a die one time, person with the highest starts, if there is a tie, u reroll
        * nice to have storage, keeping the scores of previous games while the session is going
            * keep track of the current game, not the games that you've played forever
        * be able to play with digital dice?
            * random number generator
* Will this client meeting be recurring every week? Every other week?
    * doesnt need to meet every week
    * 4-5pm works for client thurs
    * mon-wed: can't go super late
    * thurs-fri: 8am
    * 4pm friday? 

Some things to think about down the line:

- how are you going to teach someone how to use this game

- how we might have some tutorials 

- want the tutorial to flow with how the game is going to flow

- tutorial for the UI

- basic rules for the game described when users are downloading the application

- one person keeping track of the game, phone isn't being passed around

current version:

- tapping on the name to increase the point

-edit score, manually have to put it

-nice to have everyone's scores &lt;- what the current version has that he likes to keep

-who's the leader

potentially have iOS if android is going much more smoothly than we expected

storing data:

- no need for authentication

-doesn't need anything for the backend

-doesn't think we need to go there

**_keep it simple_**

SUMMARY:

IDEALLY: ANDROID APP 

<span style="text-decoration:underline;">WEEK 4</span>

02.01 with TA



* make sure that we keep documentation
    * initial stages of the wireframe
* What is the max number of players?
    * make it finite so that it will look nice
    * 8-10 
    * the whole pool
* ask at the end of class
* want to finalize wireframes next week
* focus on the default state
    * work on the customizable next time
* GOAL FOR THE QUARTER: get the game working with the default state
    * this quarter, two dice, assume everything is correct
    * next quarter, work on one dice, think about what UI to use when score is messed up
* make sure that the app isn't slowing down the game
* find a way that it's him touching less on the phone and more playing on the game
* click, click, single back
* all focus on frontend

CODING:



* get a play button on one page and rules
    * rules: how to play the game, don't make it too wordy
* make sure that we don't hardcode, have the intention that it's going to be dynamic
* check TA lecture slides
* Testing examples:
    * play a game

For next week: look at more UI tests



* react native quick testing output results

<span style="text-decoration:underline;">Week 5</span>



<p id="gdcalert2" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image2.jpg). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert3">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image2.jpg "image_tooltip")


With client: 



* Is automatic dice recognition a must-have feature in your minimum viable product?
    * Stretch feature for next quarter?
* How to decide turn order?  The turn order roll decides who starts the round and then we let the user input the order at the start of every round?
    * Combine sitting out and reordering turn order on one screen, get rid of turn order roll
* How much storage needed?  Keep track of last pot even if app is closed?  Keep track of players that played before?  Shared view model can share across screens but how to keep data alive? Data store, shared preferences
    * Last pot (debt owed), last players
* Do you want to implement a player limit?
    * Max players is 8 to 10 depending on screen real estate on drag drop turn order screen, at least 8, maye shrink buttons at 10
* What are your default rules?
    * Rolling snake eyes (11)	- Score goes to zero (turn is lost)
    * Rolling box cars (66)	- none just double
    * Rolling other doubles	- Score added is double the dice, and must roll again enforced
    * Rolling 3 doubles in row	- Score goes to zero (turn is lost)
    * Rolling 7		- Lose turn (but keep score)
    * Rolling die off table	- Lose turn (but keep score)
        * Match these consequence with events above
        * • Lose turn (but keep score?)
        * • Score resets to beginning of turn (turn is lost)
        * • Score goes to zero (turn is lost)
        * • Score added is double the dice
        * • Must roll again
    * XHave to roll at least once or can pass turn? Roll at least once
    * XFirst to 100 or more wins (instantly or one more round?), one more round
    * XTotal of 100 exactly resets the player’s score to zero
    * Total of exactly a multiple of 100 resets the player’s score to zero
    * XOnce the leader is over 100 there is one more round ending back at leader
    * XEnding game with zero “getting skunked” doubles the points gained by winner
        * Does “getting skunked” stack? (e.g. if 2 players get skunked, does it quadruple the points gained by the winner?)
            * NO, Just double the debt owed to the winner (double ur loss and double winners gains)
    * Win by total number of rounds/sets won or by overall points?
* Which turn special cases override each other? Should this also be customizable? For example, do snake-eyes and boxcars override doubles? What about the three doubles in a row?
    * 
* Show (just winner OR all current round scores OR all full game scores) after round over?  Does the pot store aggregate scores over all rounds/sets or just last round?  In the pot, should it be winner is gaining money and everyone else is losing?
    * 
* What does “sitting” out a game mean for scorekeeping of the entire set?
    * Keep the score/pot saved and still show the name in last pot
* When starting a set, is the number of rounds predetermined, or do you keep playing until you “end” the game? (e.g. Start a set of 5 games, 7 games, etc.)  Can you end game in middle of round or do you have to play till end?  Might accidentally end round though?
    * 
* When a player reaches 100, the player either wins or another round of turns is played? How do we make that distinction?
    * Customizable rule
* When a player reaches 100 and another round of turns is played, is that the final round of turns regardless of what happens? What if another player reaches 100 in this final round? Does the round still end at the player that first reached 100?
    * D
* Undo just one die roll round?
    * D
* Should we have multiple game options?  Manual input, COmputer Vision input, Digital Dice? One vs two Dice?
    * D

SItting out screen should also have easy drag drop reordering

Or do on separate screen?

<span style="text-decoration:underline;">Thursday, 2/22 With TA</span>

- able to make one more sprint

- for now, wrap up with the rules

- show demo for tomorrow's meeting

- keeping up with documentation

- begin investigating compute vision

- continue playing button

	- repurpose existing infrastructure  
